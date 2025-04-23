package org.example.interview.atlassian.code_design.subscription_cost_calculator.factory;

import org.example.interview.atlassian.code_design.subscription_cost_calculator.contants.PlanEnum;

public class BitBucket implements IProducts {

    @Override
    public float getMonthlyCost(String plan) {
        PlanEnum planEnum = PlanEnum.fromString(plan);
        return switch (planEnum) {
            case BASIC -> 19.99f;
            case STANDARD -> 149.99f;
            case PREMIUM -> 349.99f;
            case TRIAL -> 0f;
        };
    }
}
