package com.test.aiops.mcp.tools;

import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * 时区转换工具类
 */
public class TimeZoneUtil {

    /**
     * 将上海时区(+08:00)的时间转换为UTC时区(+00:00)
     * @param shanghaiTime 上海时区时间字符串, 格式: yyyy-MM-dd HHmm
     * @return UTC时区时间字符串, 格式: yyyy-MM-dd HHmm, +00:00
     */
    public static String convertToUtc(String shanghaiTime) {
        if (shanghaiTime == null || shanghaiTime.trim().isEmpty()) {
            return shanghaiTime;
        }
        
        // 解析上海时区时间
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm");
        ZonedDateTime shanghaiDateTime = ZonedDateTime.parse(shanghaiTime, 
                inputFormatter.withZone(ZoneId.of("Asia/Shanghai")));
        
        // 转换为UTC时区
        ZonedDateTime utcDateTime = shanghaiDateTime.withZoneSameInstant(ZoneId.of("UTC"));
        
        // 格式化为所需格式
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm");
        return utcDateTime.format(outputFormatter);
    }
} 