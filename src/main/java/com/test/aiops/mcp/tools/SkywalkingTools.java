package com.test.aiops.mcp.tools;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import com.test.aiops.mcp.util.TimeZoneUtil;
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

    private final static DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm");

    @Tool(description = "获取服务在指定时间区间的错误的traceId")
    public Set<String> getErrorTraceIds(
        @ToolParam(description = "服务名", required = false) String serviceName,
        @ToolParam(description = "开始时间, 格式: yyyy-MM-dd HHmm, 例如: 2025-05-16 0920") String startTime,
        @ToolParam(description = "结束时间, 格式: yyyy-MM-dd HHmm, 例如: 2025-05-16 0920") String endTime
    ) {
        // 转换时区, skywalking 接口使用 UTC 时区
        String utcStartTime = TimeZoneUtil.convertToUtc(startTime);
        String utcEndTime = TimeZoneUtil.convertToUtc(endTime);
        
        // 根据错误Trace取
        TraceListResponse traces = skywalkingDatasource.getTraceList(serviceName, utcStartTime, utcEndTime, "ERROR", null, null, null, null, null);
        Set<String> traceIds = traces.getData().getData().getTraces()
            .stream()
            .map(TraceListResponse.Trace::getTraceIds)
            .flatMap(List::stream)
            .distinct()
            .limit(2)
            .collect(Collectors.toSet());
        
        // 根据错误日志取
        Map<String, String> tag = Collections.singletonMap("level", "ERROR");
        skywalkingDatasource.getLogs(serviceName, utcStartTime, utcEndTime, null, null, tag)
            .getData()
            .getQueryLogs()
            .getLogs()
            .stream()
            .sorted(Comparator.comparing(LogListResponse.Log::getTimestamp))
            .map(LogListResponse.Log::getTraceId)
            .distinct()
            .limit(2)
            .forEach(traceIds::add);
        return traceIds;
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
