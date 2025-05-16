package com.test.aiops.mcp.datasource.skywalking.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TraceDetailQueryRequest {
    private String query;
    private Map<String, Object> variables;

    public static TraceDetailQueryRequest create(String traceId) {
        String queryString = """
            query queryTrace($traceId: ID!) {
              trace: queryTrace(traceId: $traceId) {
                spans {
                  traceId
                  segmentId
                  spanId
                  parentSpanId
                  refs {
                    traceId
                    parentSegmentId
                    parentSpanId
                    type
                  }
                  serviceCode
                  serviceInstanceName
                  startTime
                  endTime
                  endpointName
                  type
                  peer
                  component
                  isError
                  layer
                  tags {
                    key
                    value
                  }
                  logs {
                    time
                    data {
                      key
                      value
                    }
                  }
                  attachedEvents {
                    startTime {
                      seconds
                      nanos
                    }
                    event
                    endTime {
                      seconds
                      nanos
                    }
                    tags {
                      key
                      value
                    }
                    summary {
                      key
                      value
                    }
                  }
                }
              }
            }
            """;

        Map<String, Object> variables = new HashMap<>();
        variables.put("traceId", traceId);

        return new TraceDetailQueryRequest(queryString, variables);
    }
} 