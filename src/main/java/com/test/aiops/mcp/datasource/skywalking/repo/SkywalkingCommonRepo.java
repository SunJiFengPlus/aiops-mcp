package com.test.aiops.mcp.datasource.skywalking.repo;

import com.test.aiops.mcp.datasource.skywalking.config.SkywalkingProperties;
import com.test.aiops.mcp.datasource.skywalking.entity.ServiceQueryRequest;
import com.test.aiops.mcp.datasource.skywalking.entity.ServiceResponse;
import com.test.aiops.mcp.util.JacksonUtil;
import com.test.aiops.mcp.util.OkHttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Objects;
import java.util.Optional;

/**
 * Skywalking公共仓库类，提供共享的方法
 */
@Slf4j
@Component
public class SkywalkingCommonRepo {
    private final String url;
    private final String graphqlPath;
    
    public SkywalkingCommonRepo(SkywalkingProperties skywalkingProperties) {
        this.url = skywalkingProperties.getEndpoint();
        this.graphqlPath = skywalkingProperties.getGraphqlPath();
    }
    
    /**
     * 根据服务名称获取服务ID
     * @param serviceName 服务名称
     * @return 服务ID，如果未找到则返回null
     */
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