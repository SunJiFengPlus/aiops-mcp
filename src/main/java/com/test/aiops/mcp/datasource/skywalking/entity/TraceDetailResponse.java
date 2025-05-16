package com.test.aiops.mcp.datasource.skywalking.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class TraceDetailResponse {
    private Data data;

    @lombok.Data
    @NoArgsConstructor
    public static class Data {
        private Trace trace;
    }

    @lombok.Data
    @NoArgsConstructor
    public static class Trace {
        private List<Span> spans;
    }

    @lombok.Data
    @NoArgsConstructor
    public static class Span {
        private String traceId;
        private String segmentId;
        private Integer spanId;
        private Integer parentSpanId;
        private List<Ref> refs;
        private String serviceCode;
        private String serviceInstanceName;
        private Long startTime;
        private Long endTime;
        private String endpointName;
        private String type;
        private String peer;
        private String component;
        private Boolean isError;
        private String layer;
        private List<KeyValue> tags;
        private List<Log> logs;
        private List<AttachedEvent> attachedEvents;
    }

    @lombok.Data
    @NoArgsConstructor
    public static class Ref {
        private String traceId;
        private String parentSegmentId;
        private Integer parentSpanId;
        private String type;
    }

    @lombok.Data
    @NoArgsConstructor
    public static class KeyValue {
        private String key;
        private String value;
    }

    @lombok.Data
    @NoArgsConstructor
    public static class Log {
        private Long time;
        private List<KeyValue> data;
    }

    @lombok.Data
    @NoArgsConstructor
    public static class AttachedEvent {
        private TimeInfo startTime;
        private String event;
        private TimeInfo endTime;
        private List<KeyValue> tags;
        private List<KeyValue> summary;
    }

    @lombok.Data
    @NoArgsConstructor
    public static class TimeInfo {
        private Long seconds;
        private Integer nanos;
    }
} 