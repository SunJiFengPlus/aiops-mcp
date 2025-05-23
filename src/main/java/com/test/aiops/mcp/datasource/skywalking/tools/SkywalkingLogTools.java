package com.test.aiops.mcp.datasource.skywalking.tools;

import com.test.aiops.mcp.datasource.skywalking.entity.LogListResponse;
import com.test.aiops.mcp.datasource.skywalking.repo.SkywalkingLogRepo;
import com.test.aiops.mcp.util.TimeZoneUtil;
import jakarta.annotation.Resource;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@ConditionalOnProperty(name = "datasource.skywalking.log.enable", havingValue = "true")
public class SkywalkingLogTools {

    @Resource
    private SkywalkingLogRepo skywalkingLogRepo;

    @Tool(description = "根据日志获取服务在指定时间区间的错误的traceId")
    public Set<String> getErrorTraceIdsFromLog(
        @ToolParam(description = "服务名", required = false) String serviceName,
        @ToolParam(description = "开始时间, 格式: yyyy-MM-dd HHmm, 例如: 2025-05-16 0920") String startTime,
        @ToolParam(description = "结束时间, 格式: yyyy-MM-dd HHmm, 例如: 2025-05-16 0920") String endTime
    ) {
        // 转换时区, skywalking 接口使用 UTC 时区
        String utcStartTime = TimeZoneUtil.convertToUtc(startTime);
        String utcEndTime = TimeZoneUtil.convertToUtc(endTime);
        
        // 根据错误日志取
        Map<String, String> tag = Collections.singletonMap("level", "ERROR");
        return skywalkingLogRepo.getLogs(serviceName, utcStartTime, utcEndTime, null, null, tag)
            .getData()
            .getQueryLogs()
            .getLogs()
            .stream()
            .sorted(Comparator.comparing(LogListResponse.Log::getTimestamp))
            .map(LogListResponse.Log::getTraceId)
            .distinct()
            .limit(5)
            .collect(Collectors.toSet());
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
        
        return skywalkingLogRepo.getLogs(serviceName, utcStartTime, utcEndTime, null, traceId, null);
    }
}
