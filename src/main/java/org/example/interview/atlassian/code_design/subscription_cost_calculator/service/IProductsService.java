package org.example.interview.atlassian.code_design.subscription_cost_calculator.service;

import org.example.interview.atlassian.code_design.subscription_cost_calculator.model.Product;

import java.util.List;
import java.util.Map;

public interface IProductsService {
    void addProduct(String userId, Product product);
    void transitionFromTrial(String userId, String productName, String newPlan, float newCost, String newStartDate);
    Map<String, List<Float>> getMonthlyCost(String userId);
    Map<String, Float> getYearlyCost(String userId);
}