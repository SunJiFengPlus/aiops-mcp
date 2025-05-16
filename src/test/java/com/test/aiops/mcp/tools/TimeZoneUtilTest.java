package com.test.aiops.mcp.tools;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class TimeZoneUtilTest {
    
    @ParameterizedTest
    @CsvSource({
        "'2023-01-01 0800', '2023-01-01 0000'",
        "'2023-12-31 1600', '2023-12-31 0800'",
        "'2023-06-15 0000', '2023-06-14 1600'",
        "'2023-02-28 2359', '2023-02-28 1559'",
        "'2023-03-01 0000', '2023-02-28 1600'"
    })
    void testConvertToUtc(String shanghaiTime, String expectedUtcTime) {
        String utcTime = TimeZoneUtil.convertToUtc(shanghaiTime);
        assertThat(utcTime).isEqualTo(expectedUtcTime);
    }
} 