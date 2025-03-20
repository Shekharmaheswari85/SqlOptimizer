package org.example;

import org.apache.calcite.config.Lex;
import org.apache.calcite.sql.*;
import org.apache.calcite.sql.parser.SqlParser;
import org.apache.calcite.sql.parser.SqlParserPos;
import org.apache.calcite.sql.SqlWriterConfig;
import java.util.function.UnaryOperator;

import java.util.ArrayList;
import java.util.List;

public class CalciteColumnSelector {

    public static String extractSingleColumn(String sql, String targetColumn) {
        try {
            SqlParser.Config config = SqlParser.config()
                    .withLex(Lex.MYSQL);
            SqlParser parser = SqlParser.create(sql, config);
            SqlNode sqlNode = parser.parseQuery();
            
            SqlNode modifiedNode = modifySelectList(sqlNode, targetColumn);
            
            UnaryOperator<SqlWriterConfig> config2 = c -> c;
            return modifiedNode.toSqlString(config2).getSql();
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse or modify SQL: " + e.getMessage(), e);
        }
    }

    private static SqlNode modifySelectList(SqlNode node, String targetColumn) {
        if (node instanceof SqlSelect) {
            SqlSelect select = (SqlSelect) node;
            List<SqlNode> newSelectList = new ArrayList<>();
            newSelectList.add(new SqlIdentifier(targetColumn, SqlParserPos.ZERO));
            
            return new SqlSelect(
                select.getParserPosition(),
                null,  // keywordList
                new SqlNodeList(newSelectList, SqlParserPos.ZERO),  // selectList
                select.getFrom(),  // from
                select.getWhere(), // where
                select.getGroup(), // groupBy
                select.getHaving(), // having
                select.getWindowList(), // windowDecls
                select.getOrderList(), // orderBy
                select.getOffset(), // offset
                select.getFetch(), // fetch
                null   // hints
            );
        } else if (node instanceof SqlWith) {
            SqlWith with = (SqlWith) node;
            for (SqlNode withItem : with.withList) {
                if (withItem instanceof SqlWithItem) {
                    SqlWithItem item = (SqlWithItem) withItem;
                    modifySelectList(item.query, targetColumn);
                }
            }
            SqlNode newBody = modifySelectList(with.body, targetColumn);
            return new SqlWith(
                with.getParserPosition(),
                with.withList,
                newBody
            );
        }
        return node;
    }
} 