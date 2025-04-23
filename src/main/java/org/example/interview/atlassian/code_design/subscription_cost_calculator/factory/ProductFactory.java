package org.example.interview.atlassian.code_design.subscription_cost_calculator.factory;

import lombok.NoArgsConstructor;
import org.example.interview.atlassian.code_design.subscription_cost_calculator.contants.ProductEnum;

@NoArgsConstructor
public class ProductFactory {

    public static IProducts getProduct(String product) {
        ProductEnum productEnum = ProductEnum.fromString(product);
        return switch(productEnum) {
            case JIRA -> new Jira();
            case BITBUCKET -> new BitBucket();
        };
    }
}
