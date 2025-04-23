package org.example.interview.atlassian.code_design.subscription_cost_calculator;

import org.example.interview.atlassian.code_design.subscription_cost_calculator.contants.PlanEnum;
import org.example.interview.atlassian.code_design.subscription_cost_calculator.factory.IProducts;
import org.example.interview.atlassian.code_design.subscription_cost_calculator.factory.ProductFactory;
import org.example.interview.atlassian.code_design.subscription_cost_calculator.model.Product;
import org.example.interview.atlassian.code_design.subscription_cost_calculator.service.ProductsService;

import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws Exception {
        String user1 = "C1";

        // Product 1: JIRA with Trial
        String product1 = "Jira";
        String trialPlan = "TRIAL";
        String trialEndDate = "2022-06-30";
        IProducts jiraProduct = ProductFactory.getProduct(product1);
        float trialCost = jiraProduct.getMonthlyCost(trialPlan);

        Product jiraTrial = new Product();
        jiraTrial.setName(product1);
        jiraTrial.setPlanId(PlanEnum.valueOf(trialPlan));
        jiraTrial.setStartDate("2022-05-01");
        jiraTrial.setTrialEndDate(trialEndDate);
        jiraTrial.setTrialCost(trialCost);
        jiraTrial.setMembershipCost(0f); // No subscription cost during trial
        jiraTrial.setTrial(true);

        // Product 2: BITBUCKET with Subscription
        String product2 = "BITBUCKET";
        String plan2 = "STANDARD";
        String startDate2 = "2022-03-15";
        IProducts bitbucketProduct = ProductFactory.getProduct(product2);
        float bitbucketMonthlyCost = bitbucketProduct.getMonthlyCost(plan2);

        Product bitbucket = new Product();
        bitbucket.setName(product2);
        bitbucket.setPlanId(PlanEnum.valueOf(plan2));
        bitbucket.setStartDate(startDate2);
        bitbucket.setMembershipCost(bitbucketMonthlyCost);
        bitbucket.setTrial(false);

        // Add products to the service
        ProductsService productsService = new ProductsService();
        productsService.addProduct(user1, jiraTrial);
        productsService.addProduct(user1, bitbucket);

        // Transition JIRA from trial to subscription
        String subscriptionPlan = "BASIC";
        float subscriptionCost = jiraProduct.getMonthlyCost(subscriptionPlan);
        productsService.transitionFromTrial(user1, product1, subscriptionPlan, subscriptionCost, "2022-07-01");

        // Get and print monthly costs per product
        Map<String, List<Float>> monthlyCosts = productsService.getMonthlyCost(user1);
        for (Map.Entry<String, List<Float>> entry : monthlyCosts.entrySet()) {
            String productName = entry.getKey();
            List<Float> costs = entry.getValue();
            System.out.println("Monthly costs for product " + productName + ":");
            for (int i = 0; i < costs.size(); i++) {
                System.out.println("  Month " + (i + 1) + ": " + costs.get(i));
            }
        }

        // Get and print yearly costs per product and total accumulated cost
        Map<String, Float> yearlyCosts = productsService.getYearlyCost(user1);
        System.out.println("\nYearly costs per product:");
        for (Map.Entry<String, Float> entry : yearlyCosts.entrySet()) {
            System.out.println("  " + entry.getKey() + ": " + entry.getValue());
        }
    }
}