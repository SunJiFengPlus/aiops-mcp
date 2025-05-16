package com.test.aiops.mcp.datasource;

import java.util.Map;

public interface LogDatasource {
    
    /**
     * 获取日志
     * @param serviceName 服务名, 不可为空
     * @param startTime 开始时间, 格式: yyyy-MM-dd HHmm, +00:00 不可为空
     * @param endTime 结束时间, 格式: yyyy-MM-dd HHmm, +00:00 不可为空
     * @param instance 实例, 可为空
     * @param traceId traceId, 可为空
     * @param tags 标签, 可为空
     */
    Object getLogs(String serviceName, String startTime, String endTime, String instance, String traceId,
            Map<String, String> tags);
}
