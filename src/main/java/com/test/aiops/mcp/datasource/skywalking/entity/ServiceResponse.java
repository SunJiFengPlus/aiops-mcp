package com.test.aiops.mcp.datasource.skywalking.entity;

import lombok.Data;
import java.util.List;

@Data
public class ServiceResponse {
    private Data data;

    @lombok.Data
    public static class Data {
        private List<Service> services;
    }

    @lombok.Data
    public static class Service {
        private String id;
        private String value;
        private String label;
        private String group;
        private List<String> layers;
        private boolean normal;
        private String shortName;
    }
} 