package com.test.aiops.mcp.datasource;

import java.util.Map;

public interface TraceDatasource<L, T> {
    
    public L getTraceList(String serviceName, String startTime, String endTime, String state, 
        String instance, String traceId, Integer maxTraceDuration, Integer minTraceDuration, Map<String, String> tags);

    public T getTraceDetail(String traceId);
}
