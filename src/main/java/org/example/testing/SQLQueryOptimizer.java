package org.example.testing;

import org.apache.calcite.sql.parser.SqlParser;
import org.apache.calcite.avatica.util.Casing;
import org.apache.calcite.schema.SchemaPlus;
import org.apache.calcite.sql.*;
import org.apache.calcite.sql.parser.SqlParseException;
import org.apache.calcite.tools.*;
import java.util.*;
import java.util.stream.Collectors;

public class SQLQueryOptimizer {
    private final FrameworkConfig config;
    @SuppressWarnings("unused")
    private final Planner planner;

    public SQLQueryOptimizer(SchemaPlus schema) {
        SqlParser.Config parserConfig = SqlParser.config()
            .withCaseSensitive(false)
            .withQuotedCasing(Casing.UNCHANGED)
            .withUnquotedCasing(Casing.UNCHANGED);

        this.config = Frameworks.newConfigBuilder()
            .parserConfig(parserConfig)
            .defaultSchema(schema)
            .programs(Programs.heuristicJoinOrder(Programs.RULE_SET, true, 2))
            .build();

        this.planner = Frameworks.getPlanner(config);
    }

    public String optimizeQueries(List<String> rawQueries) throws SqlParseException {
        // Group queries by table
        Map<String, Set<String>> tableColumns = new HashMap<>();
        Map<String, List<SqlNode>> tableConditions = new HashMap<>();

        // Parse each query and group by table
        for (String query : rawQueries) {
            SqlNode sqlNode = SqlParser.create(query).parseQuery();
            
            if (sqlNode instanceof SqlSelect) {
                SqlSelect select = (SqlSelect) sqlNode;
                String tableName = getTableName(select);
                
                // Collect columns
                Set<String> columns = tableColumns.computeIfAbsent(tableName, k -> new HashSet<>());
                columns.addAll(extractColumns(select));
                
                // Collect where conditions
                if (select.getWhere() != null) {
                    tableConditions.computeIfAbsent(tableName, k -> new ArrayList<>())
                        .add(select.getWhere());
                }
            }
        }

        // Generate optimized queries
        List<String> optimizedQueries = new ArrayList<>();
        
        // Handle same table queries
        for (Map.Entry<String, Set<String>> entry : tableColumns.entrySet()) {
            String tableName = entry.getKey();
            Set<String> columns = entry.getValue();
            List<SqlNode> conditions = tableConditions.getOrDefault(tableName, Collections.emptyList());

            String optimizedQuery = buildOptimizedQuery(tableName, columns, conditions);
            optimizedQueries.add(optimizedQuery);
        }

        return String.join(";\n", optimizedQueries);
    }

    private String buildOptimizedQuery(String tableName, Set<String> columns, List<SqlNode> conditions) {
        StringBuilder query = new StringBuilder();
        query.append("SELECT ");
        query.append(String.join(", ", columns));
        query.append(" FROM ").append(tableName);

        if (!conditions.isEmpty()) {
            query.append(" WHERE ");
            query.append(String.join(" AND ", 
                conditions.stream()
                    .map(Object::toString)
                    .collect(Collectors.toList())));
        }

        return query.toString();
    }

    private String getTableName(SqlSelect select) {
        SqlNode from = select.getFrom();
        if (from instanceof SqlIdentifier) {
            return ((SqlIdentifier) from).toString();
        }
        return "";
    }

    private Set<String> extractColumns(SqlSelect select) {
        Set<String> columns = new HashSet<>();
        SqlNodeList selectList = select.getSelectList();
        
        for (SqlNode node : selectList) {
            if (node instanceof SqlIdentifier) {
                columns.add(node.toString());
            }
        }
        
        return columns;
    }
}