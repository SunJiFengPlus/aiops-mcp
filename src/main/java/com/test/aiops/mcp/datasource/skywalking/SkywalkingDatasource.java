package com.test.aiops.mcp.datasource.skywalking;

import com.test.aiops.mcp.datasource.LogDatasource;
import com.test.aiops.mcp.datasource.TraceDatasource;
import com.test.aiops.mcp.datasource.skywalking.entity.LogQueryRequest;
import com.test.aiops.mcp.datasource.skywalking.entity.LogListResponse;
import com.test.aiops.mcp.datasource.skywalking.entity.ServiceQueryRequest;
import com.test.aiops.mcp.datasource.skywalking.entity.ServiceResponse;
import com.test.aiops.mcp.datasource.skywalking.entity.TraceQueryRequest;
import com.test.aiops.mcp.datasource.skywalking.entity.TraceListResponse;
import com.test.aiops.mcp.datasource.skywalking.entity.TraceDetailQueryRequest;
import com.test.aiops.mcp.datasource.skywalking.entity.TraceDetailResponse;
import com.test.aiops.mcp.util.JacksonUtil;
import com.test.aiops.mcp.util.OkHttpUtil;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * TODO: 在tools层面通过具体异常信息来引导模型调整传入参数
 */
@Slf4j
@Component
public class SkywalkingDatasource implements LogDatasource<LogListResponse>, TraceDatasource<TraceListResponse, TraceDetailResponse> {
    @Value("${skywalking.oap.server.url}")
    private String url;
    @Value("${skywalking.oap.server.graphql.path:/graphql}")
    private String graphqlPath;
    
    /**
     * 获取日志
     * @param serviceName 服务名, 不可为空
     * @param startTime 开始时间, 格式: yyyy-MM-dd HHmm, +00:00 不可为空
     * @param endTime 结束时间, 格式: yyyy-MM-dd HHmm, +00:00 不可为空
     * @param instance 实例, 可为空
     * @param traceId traceId, 可为空
     * @param tags 标签, 可为空
     */
    @Override
    public LogListResponse getLogs(String serviceName, String startTime, String endTime, String instance, String traceId,
            Map<String, String> tags) {
        String serviceId = null;
        if (Objects.nonNull(serviceName)) {
            serviceId = getServiceId(serviceName);
            if (Objects.isNull(serviceId)) {
                log.error("获取服务ID失败, serviceName: {}", serviceName);
                return null;
            }
        }

        LogQueryRequest logQueryRequest = LogQueryRequest.create(startTime, endTime, serviceId, instance, traceId, tags);
        String requestBody = JacksonUtil.toJson(logQueryRequest);
        // 日志内容中包含\n, 反序列化时会报错, 移除掉
        // {"content":"2025-05-15 01:43:05.650 [...] Listing all songs\n"}
        String responseBody = OkHttpUtil.post(url + graphqlPath, requestBody).replace("\n\"", "\"");
        return JacksonUtil.fromJson(responseBody, LogListResponse.class);
    }

    /**
     * 获取trace列表
     * TODO: 补充 limit 参数
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
    @Override
    public TraceListResponse getTraceList(String serviceName, String startTime, String endTime, String state, 
        String instance, String traceId, Integer maxTraceDuration, Integer minTraceDuration, Map<String, String> tags) {
        
        String serviceId = null;
        if (Objects.nonNull(serviceName)) {
            serviceId = getServiceId(serviceName);
            if (Objects.isNull(serviceId)) {
                log.error("获取服务ID失败, serviceName: {}", serviceName);
                return null;
            }
        }

        TraceQueryRequest traceQueryRequest = TraceQueryRequest.create(
            startTime, endTime, serviceId, instance, traceId, state, maxTraceDuration, minTraceDuration);
        String requestBody = JacksonUtil.toJson(traceQueryRequest);
        String responseBody = OkHttpUtil.post(url + graphqlPath, requestBody);
        return JacksonUtil.fromJson(responseBody, TraceListResponse.class);
    }

    /**
     * 获取trace详情
     * @param traceId traceId, 不可为空
     * @return TraceDetailResponse trace详情
     */
    @Override
    public TraceDetailResponse getTraceDetail(String traceId) {
        if (ObjectUtils.isEmpty(traceId)) {
            log.error("traceId不能为空");
            return null;
        }

        TraceDetailQueryRequest traceDetailQueryRequest = TraceDetailQueryRequest.create(traceId);
        String requestBody = JacksonUtil.toJson(traceDetailQueryRequest);
        String responseBody = OkHttpUtil.post(url + graphqlPath, requestBody);
        return JacksonUtil.fromJson(responseBody, TraceDetailResponse.class);
    }

    public String getServiceId(String serviceName) {
        String requestBody = JacksonUtil.toJson(new ServiceQueryRequest());
        String responseBody = OkHttpUtil.post(url + graphqlPath, requestBody);
        ServiceResponse serviceResponse = JacksonUtil.fromJson(responseBody, ServiceResponse.class);

        return Optional.ofNullable(serviceResponse)
            .map(ServiceResponse::getData)
            .map(ServiceResponse.Data::getServices)
            .orElse(Collections.emptyList())
            .stream()
            .filter(Objects::nonNull)
            .filter(service -> Objects.equals(serviceName, service.getValue()))
            .findFirst()
            .map(ServiceResponse.Service::getId)
            .orElse(null);
    }
}
