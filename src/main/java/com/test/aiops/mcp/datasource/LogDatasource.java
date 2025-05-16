package com.test.aiops.mcp.datasource;

import java.util.Map;

public interface LogDatasource<T> {
    
    T getLogs(String serviceName, String startTime, String endTime, String instance, String traceId,
            Map<String, String> tags);
}
