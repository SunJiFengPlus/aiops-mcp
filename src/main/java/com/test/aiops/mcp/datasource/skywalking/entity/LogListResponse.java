package com.test.aiops.mcp.datasource.skywalking.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class LogListResponse {
    private Data data;

    @lombok.Data
    @NoArgsConstructor
    public static class Data {
        private QueryLogs queryLogs;
    }

    @lombok.Data
    @NoArgsConstructor
    public static class QueryLogs {
        private List<Log> logs;
    }

    @lombok.Data
    @NoArgsConstructor
    public static class Log {
        private String serviceName;
        private String serviceId;
        private String serviceInstanceName;
        private String serviceInstanceId;
        private String endpointName;
        private String endpointId;
        private String traceId;
        private Long timestamp;
        private String contentType;
        private String content;
        private List<KeyValue> tags;
    }

    @lombok.Data
    @NoArgsConstructor
    public static class KeyValue {
        private String key;
        private String value;
    }
} 