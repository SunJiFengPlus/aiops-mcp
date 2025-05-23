package com.test.aiops.mcp.config;

import java.time.ZonedDateTime;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

@Service
public class TimeTools {

    @Tool(description = "获取当前时间，包含时区信息")
    public String getCurrentTime() {
        return ZonedDateTime.now().toString();
    }
}
