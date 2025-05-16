package com.test.aiops.mcp.datasource.skywalking.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ServiceQueryRequest extends GraphQlRequest<ServiceQueryRequest.Variables> {
    private static final String QUERY_SERVICES = """
            query queryServices($layer: String!) {
              services: listServices(layer: $layer) {
                id
                value: name
                label: name
                group
                layers
                normal
                shortName
              }
            }
            """;

    @Data
    public static class Variables {
        private String layer = "GENERAL";
    }

    public ServiceQueryRequest() {
        this.setQuery(QUERY_SERVICES);
        this.setVariables(new Variables());
    }
}