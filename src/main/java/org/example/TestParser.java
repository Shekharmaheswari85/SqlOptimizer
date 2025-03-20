package org.example;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.*;

import java.util.ArrayList;
import java.util.List;

public class TestParser {

    public static String extractSingleColumn(String sql, String targetColumn) throws JSQLParserException {
        Statement statement = CCJSqlParserUtil.parse(sql);

        if (statement instanceof Select) {
            Select selectStatement = (Select) statement;
            SelectBody selectBody = selectStatement.getSelectBody();
            
            if (selectBody instanceof PlainSelect) {
                modifyPlainSelect((PlainSelect) selectBody, targetColumn);
            }

            return selectStatement.toString();
        } else {
            throw new IllegalArgumentException("Input SQL is not a SELECT statement.");
        }
    }

    private static void modifyPlainSelect(PlainSelect plainSelect, String targetColumn) {
        if (plainSelect.getFromItem() instanceof SubSelect) {
            SubSelect subSelect = (SubSelect) plainSelect.getFromItem();
            
            // Handle WITH clause if present
            if (subSelect.getWithItemsList() != null) {
                for (WithItem withItem : subSelect.getWithItemsList()) {
                    SelectBody withSelectBody = withItem.getSubSelect().getSelectBody();
                    if (withSelectBody instanceof PlainSelect) {
                        modifySelectItems((PlainSelect) withSelectBody, targetColumn);
                    }
                }
            }
            
            // Handle the main subquery
            if (subSelect.getSelectBody() instanceof PlainSelect) {
                modifySelectItems((PlainSelect) subSelect.getSelectBody(), targetColumn);
            }
        }

        // Modify the outer select
        modifySelectItems(plainSelect, targetColumn);
    }

    private static void modifySelectItems(PlainSelect select, String targetColumn) {
        List<SelectItem> newSelectItems = new ArrayList<>();
        SelectExpressionItem targetItem = new SelectExpressionItem();
        targetItem.setExpression(new net.sf.jsqlparser.schema.Column(targetColumn));
        newSelectItems.add(targetItem);
        select.setSelectItems(newSelectItems);
    }

    public static void main(String[] args) {
        String sql = "select * from ( WITH RankedData AS (SELECT unified_cust_id, CASE WHEN banner_nm = 'WalmartEA' THEN tot_prch_lst.last_1_mth END AS tot_prch_val_in_last_1_months_ea, CASE WHEN banner_nm = 'WalmartOD' THEN tot_prch_lst.last_1_mth END AS tot_prch_val_in_last_1_months_od, CASE WHEN banner_nm = 'WalmartEA' THEN tot_prch_lst.last_2_mth END AS tot_prch_val_in_last_2_months_ea, CASE WHEN banner_nm = 'WalmartOD' THEN tot_prch_lst.last_2_mth END AS tot_prch_val_in_last_2_months_od, CASE WHEN banner_nm = 'WalmartEA' THEN tot_prch_lst.last_3_mth END AS tot_prch_val_in_last_3_months_ea, CASE WHEN banner_nm = 'WalmartOD' THEN tot_prch_lst.last_3_mth END AS tot_prch_val_in_last_3_months_od, CASE WHEN banner_nm = 'WalmartEA' THEN tot_prch_lst.last_6_mth END AS tot_prch_val_in_last_6_months_ea, CASE WHEN banner_nm = 'WalmartOD' THEN tot_prch_lst.last_6_mth END AS tot_prch_val_in_last_6_months_od, CASE WHEN banner_nm = 'WalmartEA' THEN tot_prch_lst.last_12_mth END AS tot_prch_val_in_last_12_months_ea, CASE WHEN banner_nm = 'WalmartOD' THEN tot_prch_lst.last_12_mth END AS tot_prch_val_in_last_12_months_od, CASE WHEN banner_nm = 'WalmartEA' THEN tot_prch_lst.last_24_mth END AS tot_prch_val_in_last_24_months_ea, CASE WHEN banner_nm = 'WalmartOD' THEN tot_prch_lst.last_24_mth END AS tot_prch_val_in_last_24_months_od, CASE WHEN banner_nm = 'WalmartEA' THEN avg_prch_lst.last_1_mth END AS avg_prch_val_in_last_1_months_ea, CASE WHEN banner_nm = 'WalmartOD' THEN avg_prch_lst.last_1_mth END AS avg_prch_val_in_last_1_months_od, CASE WHEN banner_nm = 'WalmartEA' THEN avg_prch_lst.last_2_mth END AS avg_prch_val_in_last_2_months_ea, CASE WHEN banner_nm = 'WalmartOD' THEN avg_prch_lst.last_2_mth END AS avg_prch_val_in_last_2_months_od, CASE WHEN banner_nm = 'WalmartEA' THEN avg_prch_lst.last_3_mth END AS avg_prch_val_in_last_3_months_ea, CASE WHEN banner_nm = 'WalmartOD' THEN avg_prch_lst.last_3_mth END AS avg_prch_val_in_last_3_months_od, CASE WHEN banner_nm = 'WalmartEA' THEN avg_prch_lst.last_6_mth END AS avg_prch_val_in_last_6_months_ea, CASE WHEN banner_nm = 'WalmartOD' THEN avg_prch_lst.last_6_mth END AS avg_prch_val_in_last_6_months_od, CASE WHEN banner_nm = 'WalmartEA' THEN avg_prch_lst.last_12_mth END AS avg_prch_val_in_last_12_months_ea, CASE WHEN banner_nm = 'WalmartOD' THEN avg_prch_lst.last_12_mth END AS avg_prch_val_in_last_12_months_od, CASE WHEN banner_nm = 'WalmartEA' THEN avg_prch_lst.last_24_mth END AS avg_prch_val_in_last_24_months_ea, CASE WHEN banner_nm = 'WalmartOD' THEN avg_prch_lst.last_24_mth END AS avg_prch_val_in_last_24_months_od, CASE WHEN banner_nm = 'WalmartEA' THEN tot_cashi_saving_lst.last_1_mth END AS tot_cashi_saving_in_last_1_months_ea, CASE WHEN banner_nm = 'WalmartOD' THEN tot_cashi_saving_lst.last_1_mth END AS tot_cashi_saving_in_last_1_months_od, CASE WHEN banner_nm = 'WalmartEA' THEN tot_cashi_saving_lst.last_2_mth END AS tot_cashi_saving_in_last_2_months_ea, CASE WHEN banner_nm = 'WalmartOD' THEN tot_cashi_saving_lst.last_2_mth END AS tot_cashi_saving_in_last_2_months_od, CASE WHEN banner_nm = 'WalmartEA' THEN tot_cashi_saving_lst.last_3_mth END AS tot_cashi_saving_in_last_3_months_ea, CASE WHEN banner_nm = 'WalmartOD' THEN tot_cashi_saving_lst.last_3_mth END AS tot_cashi_saving_in_last_3_months_od, CASE WHEN banner_nm = 'WalmartEA' THEN tot_cashi_saving_lst.last_6_mth END AS tot_cashi_saving_in_last_6_months_ea, CASE WHEN banner_nm = 'WalmartOD' THEN tot_cashi_saving_lst.last_6_mth END AS tot_cashi_saving_in_last_6_months_od, CASE WHEN banner_nm = 'WalmartEA' THEN tot_cashi_saving_lst.last_12_mth END AS tot_cashi_saving_in_last_12_months_ea, CASE WHEN banner_nm = 'WalmartOD' THEN tot_cashi_saving_lst.last_12_mth END AS tot_cashi_saving_in_last_12_months_od, CASE WHEN banner_nm = 'WalmartEA' THEN tot_cashi_saving_lst.last_24_mth END AS tot_cashi_saving_in_last_24_months_ea, CASE WHEN banner_nm = 'WalmartOD' THEN tot_cashi_saving_lst.last_24_mth END AS tot_cashi_saving_in_last_24_months_od, CASE WHEN banner_nm = 'WalmartEA' THEN tot_mbrshp_pass_saving_lst.last_1_mth END AS tot_mbrshp_pass_saving_in_last_1_month_ea, CASE WHEN banner_nm = 'WalmartOD' THEN tot_mbrshp_pass_saving_lst.last_1_mth END AS tot_mbrshp_pass_saving_in_last_1_month_od, CASE WHEN banner_nm = 'WalmartEA' THEN tot_mbrshp_pass_saving_lst.last_2_mth END AS tot_mbrshp_pass_saving_in_last_2_months_ea, CASE WHEN banner_nm = 'WalmartOD' THEN tot_mbrshp_pass_saving_lst.last_2_mth END AS tot_mbrshp_pass_saving_in_last_2_months_od, CASE WHEN banner_nm = 'WalmartEA' THEN tot_mbrshp_pass_saving_lst.last_3_mth END AS tot_mbrshp_pass_saving_in_last_3_months_ea, CASE WHEN banner_nm = 'WalmartOD' THEN tot_mbrshp_pass_saving_lst.last_3_mth END AS tot_mbrshp_pass_saving_in_last_3_months_od, CASE WHEN banner_nm = 'WalmartEA' THEN tot_mbrshp_pass_saving_lst.last_6_mth END AS tot_mbrshp_pass_saving_in_last_6_months_ea, CASE WHEN banner_nm = 'WalmartOD' THEN tot_mbrshp_pass_saving_lst.last_6_mth END AS tot_mbrshp_pass_saving_in_last_6_months_od, CASE WHEN banner_nm = 'WalmartEA' THEN tot_mbrshp_pass_saving_lst.last_12_mth END AS tot_mbrshp_pass_saving_in_last_12_months_ea, CASE WHEN banner_nm = 'WalmartOD' THEN tot_mbrshp_pass_saving_lst.last_12_mth END AS tot_mbrshp_pass_saving_in_last_12_months_od, mbrshp_id as membership_id, mbrshp_start_dt as membership_purchase_date, mbrshp_affiliation_dt as reg_start_date, mbrshp_next_rnewl_dt as next_rnewl_dt, mbrshp_prch_type_nm, banner_nm, CUST_AGE_IN_MTH_VAL, HI_VAL_STORE_NBR, last_trans_ts, MAX(last_trans_ts) OVER (PARTITION BY unified_cust_id,banner_nm) as max_last_trans_ts, MAX(mbrshp_exp_dt) OVER (PARTITION BY unified_cust_id,banner_nm) as max_mbrshp_exp_dt, mbrshp_status_nm, MOST_PRCHD_STORE_NBR, RECENCY_IN_DYS_VAL, regtn_start_dt, MIN(regtn_start_dt) OVER (PARTITION BY unified_cust_id,banner_nm) as min_regtn_start_dt, cust_age_in_dys_val/tot_ords_cnt as purchase_frequency, CASE WHEN banner_nm = 'WalmartEA' THEN is_cust_actv_flag END AS is_cust_actv_flag_ea, CASE WHEN banner_nm = 'WalmartOD' THEN is_cust_actv_flag END AS is_cust_actv_flag_od, CASE WHEN banner_nm = 'WalmartEA' THEN is_cust_inactv_flag END AS is_cust_inactv_flag_ea, CASE WHEN banner_nm = 'WalmartOD' THEN is_cust_inactv_flag END AS is_cust_inactv_flag_od, CASE WHEN banner_nm = 'WalmartEA' THEN faithful_cust_flag END AS faithful_cust_flag_ea, CASE WHEN banner_nm = 'WalmartOD' THEN faithful_cust_flag END AS faithful_cust_flag_od, CASE WHEN banner_nm = 'WalmartEA' THEN lylty_cust_flag END AS lylty_cust_flag_ea, CASE WHEN banner_nm = 'WalmartOD' THEN lylty_cust_flag END AS lylty_cust_flag_od, CASE WHEN banner_nm = 'WalmartEA' THEN escp_cust_flag END AS escp_cust_flag_ea, CASE WHEN banner_nm = 'WalmartOD' THEN escp_cust_flag END AS escp_cust_flag_od, CASE WHEN banner_nm = 'WalmartEA' THEN escping_cust_flag END AS escping_cust_flag_ea, CASE WHEN banner_nm = 'WalmartOD' THEN escping_cust_flag END AS escping_cust_flag_od, CASE WHEN banner_nm = 'WalmartEA' THEN new_cust_flag END AS new_cust_flag_ea, CASE WHEN banner_nm = 'WalmartOD' THEN new_cust_flag END AS new_cust_flag_od, CASE WHEN banner_nm = 'WalmartEA' THEN not_new_cust_flag END AS not_new_cust_flag_ea, CASE WHEN banner_nm = 'WalmartOD' THEN not_new_cust_flag END AS not_new_cust_flag_od, CASE WHEN banner_nm = 'WalmartEA' THEN lost_cust_flag END AS lost_cust_flag_ea, CASE WHEN banner_nm = 'WalmartOD' THEN lost_cust_flag END AS lost_cust_flag_od, CASE WHEN banner_nm = 'WalmartEA' THEN reactivtd_cust_flag END AS reactivtd_cust_flag_ea, CASE WHEN banner_nm = 'WalmartOD' THEN reactivtd_cust_flag END AS reactivtd_cust_flag_od, CASE WHEN banner_nm = 'WalmartEA' THEN reg_only_flag END AS reg_only_flag_ea, CASE WHEN banner_nm = 'WalmartOD' THEN reg_only_flag END AS reg_only_flag_od, CASE WHEN banner_nm = 'WalmartEA' THEN sporadic_cust_flag END AS sporadic_cust_flag_ea, CASE WHEN banner_nm = 'WalmartOD' THEN sporadic_cust_flag END AS sporadic_cust_flag_od, REGTN_STATUS_VAL, FIRST_TRANS_STORE_NBR, 'MX' as market_nm, '1' as cross_sell_recommendation, '1' as newsletter_recommendation, '1' as next_best_offer_recommendation, '1' as recommendation_from_couponbook, '1' as up_sell_recommendation, '1' as retargetting_ecom, '1' as coupons, last_prchd_item_ecomm_lst, CASE WHEN mbrshp_auto_rnewl_ind = 1 THEN 'Automatic' WHEN mbrshp_auto_rnewl_ind = 0 THEN 'Manual' ELSE NULL END AS payment_type , CASE WHEN rnewd_mbr_ind = 1 THEN 'Renewal' WHEN rnewd_mbr_ind = 0 THEN 'New membership' ELSE NULL END AS membership_purchase_type, tot_trans_amt, src_sys_id, ROW_NUMBER() OVER (PARTITION BY unified_cust_id,banner_nm ORDER BY last_trans_ts DESC,regtn_start_dt DESC) AS rn FROM mx_cust_secured_cons_tables.cust_aggr_info WHERE unified_cust_id IS NOT NULL AND banner_nm in ('WalmartOD', 'WalmartEA') ) SELECT * FROM RankedData WHERE RankedData.rn = 1) pTable";
        String targetColumn = "avg_prch_val_in_last_6_months_od";

        System.out.println("Using JSQLParser:");
        try {
            String jsqlResult = extractSingleColumn(sql, targetColumn);
            System.out.println(jsqlResult);
        } catch (JSQLParserException e) {
            System.err.println("JSQLParser Error: " + e.getMessage());
        }
    }
}