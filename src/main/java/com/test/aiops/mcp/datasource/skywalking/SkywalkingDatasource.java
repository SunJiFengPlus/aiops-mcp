package com.test.aiops.mcp.datasource.skywalking;

import com.test.aiops.mcp.datasource.LogDatasource;
import com.test.aiops.mcp.datasource.TraceDatasource;
import com.test.aiops.mcp.datasource.skywalking.entity.LogQueryRequest;
import com.test.aiops.mcp.datasource.skywalking.entity.LogResponse;
import com.test.aiops.mcp.datasource.skywalking.entity.ServiceQueryRequest;
import com.test.aiops.mcp.datasource.skywalking.entity.ServiceResponse;
import com.test.aiops.mcp.datasource.skywalking.entity.TraceQueryRequest;
import com.test.aiops.mcp.datasource.skywalking.entity.TraceResponse;
import com.test.aiops.mcp.util.JacksonUtil;
import com.test.aiops.mcp.util.OkHttpUtil;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Component
public class SkywalkingDatasource implements LogDatasource, TraceDatasource {
    @Value("${skywalking.oap.server.url}")
    private String url;
    @Value("${skywalking.oap.server.graphql.path:/graphql}")
    private String graphqlPath;
    
    @Override
    public Object getLogs(String serviceName, String startTime, String endTime, String instance, String traceId,
            Map<String, String> tags) {
        String serviceId = getServiceId(serviceName);
        if (Objects.isNull(serviceId)) {
            log.error("获取服务ID失败, serviceName: {}", serviceName);
            return null;
        }

        LogQueryRequest logQueryRequest = LogQueryRequest.create(startTime, endTime, serviceId, instance, traceId, tags);
        String requestBody = JacksonUtil.toJson(logQueryRequest);
        // 日志内容中包含\n, 反序列化时会报错, 移除掉
        // {"content":"2025-05-15 01:43:05.650 [...] Listing all songs\n"}
        String responseBody = OkHttpUtil.post(url + graphqlPath, requestBody).replace("\n\"", "\"");
        return JacksonUtil.fromJson(responseBody, LogResponse.class);
    }

    @Override
    public Object getTraces(String serviceName, String startTime, String endTime, String state, 
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
        return JacksonUtil.fromJson(responseBody, TraceResponse.class);
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
