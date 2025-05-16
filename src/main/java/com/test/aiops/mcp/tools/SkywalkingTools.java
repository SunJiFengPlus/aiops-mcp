package com.test.aiops.mcp.tools;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import com.test.aiops.mcp.datasource.skywalking.SkywalkingDatasource;
import com.test.aiops.mcp.datasource.skywalking.entity.LogListResponse;
import com.test.aiops.mcp.datasource.skywalking.entity.TraceDetailResponse;
import com.test.aiops.mcp.datasource.skywalking.entity.TraceListResponse;

import jakarta.annotation.Resource;

@Service
public class SkywalkingTools {

    @Resource
    private SkywalkingDatasource skywalkingDatasource;

    @Tool(description = "获取固定时间区间内的错误的traceId")
    public List<String> getErrorTraceIds(
        @ToolParam(description = "开始时间, 格式: yyyy-MM-dd HHmm, 例如: 2025-05-16 0920") String startTime,
        @ToolParam(description = "结束时间, 格式: yyyy-MM-dd HHmm, 例如: 2025-05-16 0920") String endTime,
        @ToolParam(description = "traceId数量") Integer limit
    ) {
        // 转换时区, skywalking 接口使用 UTC 时区
        String utcStartTime = TimeZoneUtil.convertToUtc(startTime);
        String utcEndTime = TimeZoneUtil.convertToUtc(endTime);
        
        TraceListResponse traces = skywalkingDatasource.getTraceList(null, utcStartTime, utcEndTime, "ERROR", null, null, null, null, null);
        return traces.getData().getData().getTraces()
            .stream()
            .map(TraceListResponse.Trace::getTraceIds)
            .flatMap(List::stream)
            .limit(limit)
            .collect(Collectors.toList());
    }

    @Tool(description = "根据traceId获取trace详情")
    public TraceDetailResponse getTraceDetail(
        @ToolParam(description = "traceId") String traceId
    ) {
        return skywalkingDatasource.getTraceDetail(traceId);
    }

    @Tool(description = "根据traceId获取日志")
    public LogListResponse getLogs(
        @ToolParam(description = "服务名") String serviceName,
        @ToolParam(description = "开始时间, 格式: yyyy-MM-dd HHmm, 例如: 2025-05-16 0920") String startTime,
        @ToolParam(description = "结束时间, 格式: yyyy-MM-dd HHmm, 例如: 2025-05-16 0920") String endTime,
        @ToolParam(description = "traceId", required = false) String traceId
    ) {
        // 转换时区, skywalking 接口使用 UTC 时区
        String utcStartTime = TimeZoneUtil.convertToUtc(startTime);
        String utcEndTime = TimeZoneUtil.convertToUtc(endTime);
        
        return skywalkingDatasource.getLogs(serviceName, utcStartTime, utcEndTime, null, traceId, null);
    }
}
