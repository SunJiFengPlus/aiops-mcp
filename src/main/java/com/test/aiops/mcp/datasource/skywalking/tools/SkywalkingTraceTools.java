package com.test.aiops.mcp.datasource.skywalking.tools;

import java.util.*;
import java.util.stream.Collectors;

import com.test.aiops.mcp.util.TimeZoneUtil;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import com.test.aiops.mcp.datasource.skywalking.repo.SkywalkingTraceRepo;
import com.test.aiops.mcp.datasource.skywalking.entity.TraceDetailResponse;
import com.test.aiops.mcp.datasource.skywalking.entity.TraceListResponse;

import jakarta.annotation.Resource;

@Service
@ConditionalOnProperty(name = "datasource.skywalking.trace.enable", havingValue = "true")
public class SkywalkingTraceTools {

    @Resource
    private SkywalkingTraceRepo skywalkingTraceRepo;

    @Tool(description = "根据Trace获取服务在指定时间区间的错误的traceId")
    public Set<String> getErrorTraceIdsFromTrace(
        @ToolParam(description = "服务名", required = false) String serviceName,
        @ToolParam(description = "开始时间, 格式: yyyy-MM-dd HHmm, 例如: 2025-05-16 0920") String startTime,
        @ToolParam(description = "结束时间, 格式: yyyy-MM-dd HHmm, 例如: 2025-05-16 0920") String endTime
    ) {
        // 转换时区, skywalking 接口使用 UTC 时区
        String utcStartTime = TimeZoneUtil.convertToUtc(startTime);
        String utcEndTime = TimeZoneUtil.convertToUtc(endTime);
        
        // 根据错误Trace取
        TraceListResponse traces = skywalkingTraceRepo.getTraceList(serviceName, utcStartTime, utcEndTime, "ERROR", null, null, null, null, null);
        return traces.getData().getData().getTraces()
            .stream()
            .map(TraceListResponse.Trace::getTraceIds)
            .flatMap(List::stream)
            .distinct()
            .limit(5)
            .collect(Collectors.toSet());
    }

    @Tool(description = "根据traceId获取trace详情")
    public TraceDetailResponse getTraceDetail(
        @ToolParam(description = "traceId") String traceId
    ) {
        return skywalkingTraceRepo.getTraceDetail(traceId);
    }
}
