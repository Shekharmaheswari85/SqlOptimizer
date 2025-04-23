package org.example.interview.atlassian.code_design.subscription_cost_calculator.contants;

public enum ProductEnum {
    JIRA, BITBUCKET;

    public static ProductEnum fromString(String product) {
        for (ProductEnum productEnum : ProductEnum.values()) {
            if (productEnum.name().equalsIgnoreCase(product)) {
                return productEnum;
            }
        }
        throw new IllegalArgumentException("No enum constant " + ProductEnum.class.getCanonicalName() + "." + product);
    }
}
