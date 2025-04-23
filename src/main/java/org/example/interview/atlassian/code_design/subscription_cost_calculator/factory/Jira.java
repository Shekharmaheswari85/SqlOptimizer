package org.example.interview.atlassian.code_design.subscription_cost_calculator.factory;

import org.example.interview.atlassian.code_design.subscription_cost_calculator.contants.PlanEnum;

public class Jira implements IProducts {

    @Override
    public float getMonthlyCost(String plan) {
        PlanEnum planEnum = PlanEnum.valueOf(plan);
        return switch (planEnum) {
            case BASIC -> 9.99f;
            case STANDARD -> 49.99f;
            case PREMIUM -> 249.99f;
            case TRIAL -> 0f;
        };
    }
}
