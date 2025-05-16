package com.test.aiops.mcp.datasource.skywalking.entity;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class TraceQueryRequest extends GraphQlRequest<TraceQueryRequest.Variables> {
    private static final String QUERY_TRACES = """
            query queryTraces($condition: TraceQueryCondition) {
              data: queryBasicTraces(condition: $condition) {
                traces {
                  key: segmentId
                  endpointNames
                  duration
                  start
                  isError
                  traceIds
                }
              }}
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
        private String traceState;
        private String queryOrder;
        private Integer minTraceDuration;
        private Integer maxTraceDuration;
        private String traceId;
        private String serviceId;
        private String serviceInstanceId;
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
        private int pageSize = 20;
    }

    public static TraceQueryRequest create(String startTime, String endTime, String serviceId, 
            String instanceId, String traceId, String state, Integer maxTraceDuration, Integer minTraceDuration) {
        return TraceQueryRequest.builder()
            .query(QUERY_TRACES)
            .variables(Variables.builder()
                .condition(Condition.builder()
                    .queryDuration(new QueryDuration(startTime, endTime))
                    .paging(new Paging())
                    .traceState(state)
                    .queryOrder("BY_START_TIME")
                    .minTraceDuration(minTraceDuration)
                    .maxTraceDuration(maxTraceDuration)
                    .traceId(traceId)
                    .serviceId(serviceId)
                    .serviceInstanceId(instanceId)
                    .build())
                .build())
            .build();
    }
}