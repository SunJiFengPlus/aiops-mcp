package com.test.aiops.mcp.datasource.skywalking.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import java.util.List;
import java.util.Map;
import java.util.Collections;
import java.util.Optional;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class LogQueryRequest extends GraphQlRequest<LogQueryRequest.Variables> {
    private static final String QUERY_LOGS = """
            query queryLogs($condition: LogQueryCondition) {
                queryLogs(condition: $condition) {
                    logs {
                        serviceName
                        serviceId
                        serviceInstanceName
                        serviceInstanceId
                        endpointName
                        endpointId
                        traceId
                        timestamp
                        contentType
                        content
                        tags {
                            key
                            value
                        }
                    }
                }
            }
            """;

    @Data
    @Builder
    public static class Variables {
        private Condition condition;
    }

    @Data
    @Builder
    public static class Condition {
        private QueryDuration queryDuration;
        private Paging paging;
        private String serviceId;
        private String serviceInstanceId;
        private List<String> keywordsOfContent;
        private List<String> excludingKeywordsOfContent;
        private List<Tag> tags;
        private RelatedTrace relatedTrace;
    }

    @Data
    public static class QueryDuration {
        private String start;
        private String end;
        private String step = "MINUTE";

        public QueryDuration(String start, String end) {
            this.start = start;
            this.end = end;
        }
    }

    @Data
    @NoArgsConstructor
    public static class Paging {
        private int pageNum = 1;
        private int pageSize = 15;
    }

    @Data
    @Builder
    @AllArgsConstructor
    public static class Tag {
        private String key;
        private String value;
    }

    @Data
    @AllArgsConstructor
    public static class RelatedTrace {
        private String traceId;
    }

    public static LogQueryRequest create(String startTime, String endTime, String serviceId, 
            String instanceId, String traceId, Map<String, String> tags) {
        List<Tag> tagList = Optional.ofNullable(tags)
            .map(t -> t.entrySet().stream()
                .map(entry -> new Tag(entry.getKey(), entry.getValue()))
                .toList())
            .orElse(Collections.emptyList());

        return LogQueryRequest.builder()
            .query(QUERY_LOGS)
            .variables(Variables.builder()
                .condition(Condition.builder()
                    .queryDuration(new QueryDuration(startTime, endTime))
                    .paging(new Paging())
                    .serviceId(serviceId)
                    .serviceInstanceId(instanceId)
                    .keywordsOfContent(Collections.emptyList())
                    .excludingKeywordsOfContent(Collections.emptyList())
                    .tags(tagList)
                    .relatedTrace(traceId != null ? new RelatedTrace(traceId) : null)
                    .build())
                .build())
            .build();
    }
}