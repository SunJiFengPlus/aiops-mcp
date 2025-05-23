package com.test.aiops.mcp.datasource.skywalking.repo;

import com.test.aiops.mcp.datasource.skywalking.config.SkywalkingProperties;
import com.test.aiops.mcp.datasource.skywalking.entity.TraceQueryRequest;
import com.test.aiops.mcp.datasource.skywalking.entity.TraceListResponse;
import com.test.aiops.mcp.datasource.skywalking.entity.TraceDetailQueryRequest;
import com.test.aiops.mcp.datasource.skywalking.entity.TraceDetailResponse;
import com.test.aiops.mcp.util.JacksonUtil;
import com.test.aiops.mcp.util.OkHttpUtil;

import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.Map;
import java.util.Objects;

/**
 * TODO: 在tools层面通过具体异常信息来引导模型调整传入参数
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "datasource.skywalking.trace.enable", havingValue = "true")
public class SkywalkingTraceRepo {
    private final String url;
    private final String graphqlPath;
    private final SkywalkingCommonRepo commonRepo;
    
    public SkywalkingTraceRepo(SkywalkingProperties skywalkingProperties, SkywalkingCommonRepo commonRepo) {
        this.url = skywalkingProperties.getEndpoint();
        this.graphqlPath = skywalkingProperties.getGraphqlPath();
        this.commonRepo = commonRepo;
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
    public TraceListResponse getTraceList(String serviceName, String startTime, String endTime, String state, 
        String instance, String traceId, Integer maxTraceDuration, Integer minTraceDuration, Map<String, String> tags) {
        
        String serviceId = null;
        if (Objects.nonNull(serviceName)) {
            serviceId = commonRepo.getServiceId(serviceName);
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
}
