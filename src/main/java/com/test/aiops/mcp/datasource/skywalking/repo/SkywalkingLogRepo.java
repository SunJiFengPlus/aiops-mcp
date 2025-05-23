package com.test.aiops.mcp.datasource.skywalking.repo;

import com.test.aiops.mcp.datasource.skywalking.config.SkywalkingProperties;
import com.test.aiops.mcp.datasource.skywalking.entity.*;
import com.test.aiops.mcp.util.JacksonUtil;
import com.test.aiops.mcp.util.OkHttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Objects;

/**
 * TODO: 在tools层面通过具体异常信息来引导模型调整传入参数
 */
@Slf4j
@Repository
@ConditionalOnProperty(name = "datasource.skywalking.log.enable", havingValue = "true")
public class SkywalkingLogRepo {
    private final String url;
    private final String graphqlPath;
    private final SkywalkingCommonRepo commonRepo;
    
    public SkywalkingLogRepo(SkywalkingProperties skywalkingProperties, SkywalkingCommonRepo commonRepo) {
        this.url = skywalkingProperties.getEndpoint();
        this.graphqlPath = skywalkingProperties.getGraphqlPath();
        this.commonRepo = commonRepo;
    }
    
    /**
     * 获取日志
     * @param serviceName 服务名, 不可为空
     * @param startTime 开始时间, 格式: yyyy-MM-dd HHmm, +00:00 不可为空
     * @param endTime 结束时间, 格式: yyyy-MM-dd HHmm, +00:00 不可为空
     * @param instance 实例, 可为空
     * @param traceId traceId, 可为空
     * @param tags 标签, 可为空
     */
    public LogListResponse getLogs(String serviceName, String startTime, String endTime, String instance, String traceId,
            Map<String, String> tags) {
        String serviceId = null;
        if (Objects.nonNull(serviceName)) {
            serviceId = commonRepo.getServiceId(serviceName);
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
}
