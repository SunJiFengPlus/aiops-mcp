package com.test.aiops.mcp.tools;

import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;

@Component
public class ToolsConfig {
    @Resource
    private SkywalkingTools skywalkingTools;
    @Resource
    private TimeTools timeTools;

    @Bean
    public ToolCallbackProvider weatherTools() {
        return MethodToolCallbackProvider.builder()
            .toolObjects(skywalkingTools, timeTools)
            .build();
    }
}
