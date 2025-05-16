package com.test.aiops.mcp.datasource.scg;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.test.aiops.mcp.datasource.scg.entity.Route;
import com.test.aiops.mcp.util.JacksonUtil;
import com.test.aiops.mcp.util.OkHttpUtil;


@Component
public class SpringCloudGatewayDatasource {

    @Value("${spring.cloud.gateway.actuator.route-path:/actuator/gateway/routes}")
    private String routePath;

    /**
     * 获取Spring Cloud Gateway的路由列表
     * @param address 网关地址, 例如 http://127.0.0.1:8080
     * @return 路由列表
     */
    public List<Route> getRoutes(String address) {
        String url = address + routePath;
        String response = OkHttpUtil.get(url);
        if (response == null) {
            return null;
        }
        return JacksonUtil.fromJson(response, new TypeReference<List<Route>>() {});
    }
}
