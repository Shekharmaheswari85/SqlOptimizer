package org.example.testing;

import java.util.List;

import java.util.Arrays;

import org.apache.calcite.sql.parser.SqlParseException;

import org.apache.calcite.schema.SchemaPlus;
import org.apache.calcite.jdbc.CalciteSchema;

// Usage example
public class Main {
    private static SchemaPlus createSchema() {
        return CalciteSchema.createRootSchema(true).plus();
    }

    public static void main(String[] args) {
        try {
            // Create schema
            SchemaPlus schema = createSchema(); // Implementation depends on your data source
            
            // Create optimizer
            SQLQueryOptimizer optimizer = new SQLQueryOptimizer(schema);
            
            // Your input queries
            List<String> queries = Arrays.asList(
                "select name from table1",
                "select class from table1",
                "select rollnumber from table1",
                "select rollnumber from table3",
                "select rollnumber from table4",
                "select rollnumber from table5",
                "select marks from subject"
            );
            
            // Get optimized queries
            String optimizedQueries = optimizer.optimizeQueries(queries);
            System.out.println("Optimized Queries:");
            System.out.println(optimizedQueries);
            
        } catch (SqlParseException e) {
            e.printStackTrace();
        }
    }
}
