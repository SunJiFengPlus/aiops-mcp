package com.test.aiops.mcp.datasource.skywalking.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class GraphQlRequest<T> {
    private String query;
    private T variables;
}