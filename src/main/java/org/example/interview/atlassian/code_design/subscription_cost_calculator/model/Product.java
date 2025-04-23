package org.example.interview.atlassian.code_design.subscription_cost_calculator.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.interview.atlassian.code_design.subscription_cost_calculator.contants.PlanEnum;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {
    String name;
    PlanEnum planId;
    float membershipCost;
    String startDate; // Subscription start date
    boolean isTrial; // Indicates if the product is in a trial period
    String trialEndDate; // End date of the trial period
    float trialCost; // Cost during the trial period (if applicable)
}