package org.example.interview.atlassian.code_design.subscription_cost_calculator.contants;

public enum PlanEnum {
    BASIC, STANDARD, PREMIUM, TRIAL;

    public static PlanEnum fromString(String plan) {
        for (PlanEnum planEnum : PlanEnum.values()) {
            if (planEnum.name().equalsIgnoreCase(plan)) {
                return planEnum;
            }
        }
        throw new IllegalArgumentException("No constant with text " + plan + " found");
    }
}
