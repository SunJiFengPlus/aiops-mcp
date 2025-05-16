package com.test.aiops.mcp.datasource;

import java.util.Map;

public interface TraceDatasource {
    /**
     * 获取trace列表
     * @param serviceName 服务名, 当traceId为空时, 不可为空
     * @param startTime 开始时间, 格式: yyyy-MM-dd HHmm, +00:00 不可为空
     * @param endTime 结束时间, 格式: yyyy-MM-dd HHmm, +00:00 不可为空
     * @param state 状态, SUCCESS 或 ERROR 或 ALL, 不可为空
     * @param instance 实例, 可为空
     * @param traceId traceId, 可为空
     * @param maxTraceDuration 最大trace时长, 单位: 毫秒, 可为空
     * @param minTraceDuration 最小trace时长, 单位: 毫秒, 可为空
     * @param tags 标签, 可为空
     */
    public Object getTraces(String serviceName, String startTime, String endTime, String state, 
        String instance, String traceId, Integer maxTraceDuration, Integer minTraceDuration, Map<String, String> tags);
}
