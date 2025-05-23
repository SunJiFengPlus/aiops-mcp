package com.test.aiops.mcp.datasource.skywalking.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "datasource.skywalking")
public class SkywalkingProperties {
    
    private String endpoint;
    private String graphqlPath;
    private LogConfig log;
    private TraceConfig trace;
    
    @Data
    public static class LogConfig {
        private boolean enable;
    }
    
    @Data
    public static class TraceConfig {
        private boolean enable;
    }
}