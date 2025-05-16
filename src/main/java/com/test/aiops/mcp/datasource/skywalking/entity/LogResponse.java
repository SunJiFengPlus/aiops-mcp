package com.test.aiops.mcp.datasource.skywalking.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class LogResponse {
    private Data data;

    @lombok.Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Data {
        private QueryLogs queryLogs;
    }

    @lombok.Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class QueryLogs {
        private List<Log> logs;
    }

    @lombok.Data
    @JsonIgnoreProperties(ignoreUnknown = true)
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
        private List<Tag> tags;
    }

    @lombok.Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Tag {
        private String key;
        private String value;
    }
}