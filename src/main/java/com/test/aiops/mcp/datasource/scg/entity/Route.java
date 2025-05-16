package com.test.aiops.mcp.datasource.scg.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * Spring Cloud Gateway路由定义
 */
@Data
public class Route {
    
    /**
     * 路由谓词
     */
    private String predicate;
    
    /**
     * 路由ID
     */
    @JsonProperty("route_id")
    private String routeId;
    
    /**
     * 路由过滤器列表
     */
    private List<String> filters;
    
    /**
     * 目标URI
     */
    private String uri;
    
    /**
     * 路由顺序
     */
    private Integer order;

}
