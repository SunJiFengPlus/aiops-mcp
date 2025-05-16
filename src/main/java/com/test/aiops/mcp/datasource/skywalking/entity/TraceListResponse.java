package com.test.aiops.mcp.datasource.skywalking.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TraceListResponse {
    private Data data;

    @lombok.Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Data {
        private Traces data;
    }

    @lombok.Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Traces {
        private List<Trace> traces;
    }

    @lombok.Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Trace {
        private String key;
        private List<String> endpointNames;
        private int duration;
        private String start;
        private boolean isError;
        private List<String> traceIds;
    }
}