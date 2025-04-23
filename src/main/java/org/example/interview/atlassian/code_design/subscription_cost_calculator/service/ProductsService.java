package org.example.interview.atlassian.code_design.subscription_cost_calculator.service;

import org.example.interview.atlassian.code_design.subscription_cost_calculator.contants.PlanEnum;
import org.example.interview.atlassian.code_design.subscription_cost_calculator.model.Product;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ProductsService implements IProductsService {

    private final ConcurrentHashMap<String, List<Product>> userProducts;

    public ProductsService() {
        this.userProducts = new ConcurrentHashMap<>();
    }

    @Override
    public void addProduct(String userId, Product product) {
        userProducts.computeIfAbsent(userId, k -> new ArrayList<>()).add(product);
    }

    @Override
    public synchronized void transitionFromTrial(String userId, String productName, String newPlan, float newCost, String newStartDate) {
        List<Product> products = userProducts.get(userId);
        if (products != null) {
            for (Product product : products) {
                if (product.getName().equals(productName) && product.isTrial()) {
                    product.setPlanId(PlanEnum.valueOf(newPlan));
                    product.setMembershipCost(newCost);
                    product.setStartDate(newStartDate);
                    product.setTrial(false);
                    product.setTrialEndDate(null);
                    product.setTrialCost(0f);
                    break;
                }
            }
        }
    }

    @Override
    public synchronized Map<String, List<Float>> getMonthlyCost(String userId) {
        List<Product> products = userProducts.get(userId);
        Map<String, List<Float>> productMonthlyCosts = new HashMap<>();

        if (products != null) {
            for (Product product : products) {
                List<Float> monthlyCosts = new ArrayList<>(Collections.nCopies(12, 0f));
                String startDate = product.getStartDate();
                int startDay = Integer.parseInt(startDate.substring(8, 10));
                int startMonth = Integer.parseInt(startDate.substring(5, 7));

                if (product.isTrial()) {
                    String trialEndDate = product.getTrialEndDate();
                    int trialEndMonth = Integer.parseInt(trialEndDate.substring(5, 7));
                    for (int i = startMonth - 1; i < trialEndMonth; i++) {
                        monthlyCosts.set(i, product.getTrialCost());
                    }
                } else {
                    // Add cost for the start month (partial cost if applicable)
                    if (startDay > 1) {
                        float partialMonthCost = ((30 - startDay + 1) / 30.0f) * product.getMembershipCost();
                        monthlyCosts.set(startMonth - 1, partialMonthCost);
                    } else {
                        monthlyCosts.set(startMonth - 1, product.getMembershipCost());
                    }

                    // Add full costs for the remaining months
                    for (int i = startMonth; i < 12; i++) {
                        monthlyCosts.set(i, product.getMembershipCost());
                    }
                }

                productMonthlyCosts.put(product.getName(), monthlyCosts);
            }
        }

        return productMonthlyCosts;
    }

    @Override
    public synchronized Map<String, Float> getYearlyCost(String userId) {
        Map<String, List<Float>> productMonthlyCosts = getMonthlyCost(userId);
        Map<String, Float> yearlyCosts = new HashMap<>();
        float totalCost = 0;

        for (Map.Entry<String, List<Float>> entry : productMonthlyCosts.entrySet()) {
            String productName = entry.getKey();
            List<Float> monthlyCosts = entry.getValue();
            float yearlyCost = 0;

            for (Float cost : monthlyCosts) {
                yearlyCost += cost;
            }

            yearlyCosts.put(productName, yearlyCost);
            totalCost += yearlyCost;
        }

        yearlyCosts.put("Total", totalCost);
        return yearlyCosts;
    }
}