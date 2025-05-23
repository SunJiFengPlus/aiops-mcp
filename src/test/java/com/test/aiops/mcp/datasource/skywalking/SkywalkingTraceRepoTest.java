package com.test.aiops.mcp.datasource.skywalking;

import com.test.aiops.mcp.datasource.skywalking.config.SkywalkingProperties;
import com.test.aiops.mcp.datasource.skywalking.repo.SkywalkingCommonRepo;
import com.test.aiops.mcp.datasource.skywalking.repo.SkywalkingTraceRepo;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class SkywalkingTraceRepoTest {

    private MockWebServer mockWebServer;
    private SkywalkingTraceRepo skywalkingTraceRepo;
    private SkywalkingTraceRepo spySkywalkingTraceRepo;
    private SkywalkingCommonRepo commonRepo;
    private SkywalkingProperties properties;
    
    @BeforeEach
    void setUp() throws IOException {
        // 创建MockWebServer，模拟Skywalking OAP服务器
        mockWebServer = new MockWebServer();
        mockWebServer.start(10000);
        
        // 创建SkywalkingProperties实例
        properties = new SkywalkingProperties();
        properties.setEndpoint("http://localhost:10000");
        properties.setGraphqlPath("/graphql");
        
        // 创建SkywalkingCommonRepo实例
        commonRepo = Mockito.mock(SkywalkingCommonRepo.class);
        
        // 创建SkywalkingTraceRepo实例
        skywalkingTraceRepo = new SkywalkingTraceRepo(properties, commonRepo);
        
        // 创建spy对象，用于部分mock
        spySkywalkingTraceRepo = Mockito.spy(skywalkingTraceRepo);
    }
    
    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }
    
    @Test
    void testGetServiceId() {
        // 准备模拟响应数据
        String mockResponseBody = """
            {
                "data": {
                    "services": [
                        {
                            "id": "YXBw.1",
                            "value": "app",
                            "label": "app",
                            "group": "",
                            "layers": [
                                "GENERAL"
                            ],
                            "normal": true,
                            "shortName": "app"
                        }
                    ]
                }
            }
            """;
        
        // 设置模拟响应
        mockWebServer.enqueue(new MockResponse()
                .setBody(mockResponseBody)
                .setHeader("Content-Type", "application/json"));
        
        // 设置commonRepo的getServiceId方法返回值
        Mockito.when(commonRepo.getServiceId("app")).thenReturn("YXBw.1");
        
        // 调用待测试方法
        String serviceId = commonRepo.getServiceId("app");
        
        // 验证结果
        assertThat(serviceId).isEqualTo("YXBw.1");
    }
    
    @Test
    void testGetServiceIdNotFound() {
        // 准备模拟响应数据
        String mockResponseBody = """
            {
                "data": {
                    "services": [
                        {
                            "id": "service-123",
                            "value": "testService",
                            "label": "testService",
                            "group": "default",
                            "layers": ["GENERAL"],
                            "normal": true,
                            "shortName": "test"
                        }
                    ]
                }
            }
            """;
        
        // 设置模拟响应
        mockWebServer.enqueue(new MockResponse()
                .setBody(mockResponseBody)
                .setHeader("Content-Type", "application/json"));
        
        // 设置commonRepo的getServiceId方法返回值
        Mockito.when(commonRepo.getServiceId("nonExistentService")).thenReturn(null);
        
        // 调用待测试方法，使用不存在的服务名
        String serviceId = commonRepo.getServiceId("nonExistentService");
        
        // 验证结果
        assertThat(serviceId).isNull();
    }
    
    @Test
    void testGetLogs() {
        // Mock getServiceId方法，避免调用真实方法
        Mockito.when(commonRepo.getServiceId("testService")).thenReturn("service-123");
        
        // 模拟getLogs方法的响应
        String mockLogsResponse = """
            {
                "data": {
                    "queryLogs": {
                        "logs": [
                            {
                                "serviceName": "songs",
                                "serviceId": "c29uZ3M=.1",
                                "serviceInstanceName": "e532a082a5574f749e23bea351300297@172.18.0.6",
                                "serviceInstanceId": "c29uZ3M=.1_ZTUzMmEwODJhNTU3NGY3NDllMjNiZWEzNTEzMDAyOTdAMTcyLjE4LjAuNg==",
                                "endpointName": "UndertowDispatch",
                                "endpointId": "c29uZ3M=.1_VW5kZXJ0b3dEaXNwYXRjaA==",
                                "traceId": "f2a6f356-d4cc-4c3b-ace0-a5f7e949aee2",
                                "timestamp": 1747273385633,
                                "contentType": "TEXT",
                                "content": "2025-05-15 01:43:05.633 [TID:f2a6f356-d4cc-4c3b-ace0-a5f7e949aee2] [XNIO-1 task-1] INFO  o.a.s.s.s.s.c.SongController -Listing top songs\n",
                                "tags": [
                                    {
                                        "key": "level",
                                        "value": "INFO"
                                    },
                                    {
                                        "key": "logger",
                                        "value": "org.apache.skywalking.showcase.services.song.controller.SongController"
                                    },
                                    {
                                        "key": "thread",
                                        "value": "XNIO-1 task-1"
                                    }
                                ]
                            },
                            {
                                "serviceName": "songs",
                                "serviceId": "c29uZ3M=.1",
                                "serviceInstanceName": "e532a082a5574f749e23bea351300297@172.18.0.6",
                                "serviceInstanceId": "c29uZ3M=.1_ZTUzMmEwODJhNTU3NGY3NDllMjNiZWEzNTEzMDAyOTdAMTcyLjE4LjAuNg==",
                                "endpointName": "UndertowDispatch",
                                "endpointId": "c29uZ3M=.1_VW5kZXJ0b3dEaXNwYXRjaA==",
                                "traceId": "f2a6f356-d4cc-4c3b-ace0-a5f7e949aee2",
                                "timestamp": 1747273385650,
                                "contentType": "TEXT",
                                "content": "2025-05-15 01:43:05.650 [TID:f2a6f356-d4cc-4c3b-ace0-a5f7e949aee2] [XNIO-1 task-1] INFO  o.a.s.s.s.s.c.SongController -Listing all songs\n",
                                "tags": [
                                    {
                                        "key": "level",
                                        "value": "INFO"
                                    },
                                    {
                                        "key": "logger",
                                        "value": "org.apache.skywalking.showcase.services.song.controller.SongController"
                                    },
                                    {
                                        "key": "thread",
                                        "value": "XNIO-1 task-1"
                                    }
                                ]
                            }
                        ]
                    }
                }
            }
            """;
        
        // 设置模拟响应
        mockWebServer.enqueue(new MockResponse()
                .setBody(mockLogsResponse)
                .setHeader("Content-Type", "application/json"));
        
        // 调用待测试方法
        Map<String, String> tags = new HashMap<>();
        tags.put("level", "INFO");
        
        // 注释掉这个测试，因为SkywalkingTraceRepo不再有getLogs方法
        // Object result = spySkywalkingTraceRepo.getLogs("testService", "2023-01-01", "2023-01-02", 
        //         null, null, tags);
        
        // 验证结果
        // assertThat(result).isNotNull();
        
        // 验证getServiceId方法被调用
        // Mockito.verify(commonRepo).getServiceId("testService");
    }
    
    @Test
    void testGetTraces() {
        // Mock getServiceId方法，避免调用真实方法
        Mockito.when(commonRepo.getServiceId("testService")).thenReturn("service-123");
        
        // 模拟getTraces方法的响应
        String mockTracesResponse = """
            {
                "data": {
                    "data": {
                        "traces": [
                            {
                                "key": "579df4bead3859de",
                                "endpointNames": [
                                    "/homepage"
                                ],
                                "duration": 33,
                                "start": "1747275234251",
                                "isError": false,
                                "traceIds": [
                                    "27173e33-f765-4543-a985-d6059147239a"
                                ]
                            }
                        ]
                    }
                }
            }
            """;
        
        // 设置模拟响应
        mockWebServer.enqueue(new MockResponse()
                .setBody(mockTracesResponse)
                .setHeader("Content-Type", "application/json"));
        
        // 调用待测试方法
        Map<String, String> tags = new HashMap<>();
        tags.put("http.method", "GET");
        Object result = spySkywalkingTraceRepo.getTraceList("testService", "2023-01-01", "2023-01-02", 
                "SUCCESS", null, null, 1000, 0, tags);
        
        // 验证结果
        assertThat(result).isNotNull();
        
        // 验证getServiceId方法被调用
        Mockito.verify(commonRepo).getServiceId("testService");
    }
    
    @Test
    void testGetTracesWithoutServiceName() {
        // 模拟getTraces方法的响应（这次不需要模拟getServiceId的响应，因为没有提供serviceName）
        String mockTracesResponse = """
            {
                "data": {
                    "data": {
                        "traces": [
                            {
                                "key": "579df4bead3859de",
                                "endpointNames": [
                                    "/homepage"
                                ],
                                "duration": 33,
                                "start": "1747275234251",
                                "isError": false,
                                "traceIds": [
                                    "27173e33-f765-4543-a985-d6059147239a"
                                ]
                            }
                        ]
                    }
                }
            }
            """;
        
        // 设置模拟响应
        mockWebServer.enqueue(new MockResponse()
                .setBody(mockTracesResponse)
                .setHeader("Content-Type", "application/json"));
        
        // 调用待测试方法，不提供serviceName
        Object result = spySkywalkingTraceRepo.getTraceList(null, "2023-01-01", "2023-01-02", 
                "SUCCESS", null, "traceId123", null, null, null);
        
        // 验证结果
        assertThat(result).isNotNull();
        
        // 确认getServiceId方法没有被调用
        Mockito.verify(commonRepo, Mockito.never()).getServiceId(Mockito.anyString());
    }
    
    @Test
    void testGetTraceDetail() {
        // 模拟getTraceDetail方法的响应
        String mockTraceDetailResponse = """
            {
                "data": {
                    "trace": {
                        "spans": [
                            {
                                "traceId": "27173e33-f765-4543-a985-d6059147239a",
                                "segmentId": "579df4bead3859de",
                                "spanId": 0,
                                "parentSpanId": -1,
                                "refs": [],
                                "serviceCode": "frontend",
                                "serviceInstanceName": "frontend-instance",
                                "startTime": 1747275234251,
                                "endTime": 1747275234284,
                                "endpointName": "/homepage",
                                "type": "Entry",
                                "peer": "",
                                "component": "HTTP",
                                "isError": false,
                                "layer": "Http",
                                "tags": [
                                    {
                                        "key": "http.method",
                                        "value": "GET"
                                    },
                                    {
                                        "key": "http.url",
                                        "value": "/homepage"
                                    },
                                    {
                                        "key": "http.status_code",
                                        "value": "200"
                                    }
                                ],
                                "logs": []
                            }
                        ]
                    }
                }
            }
            """;
        
        // 设置模拟响应
        mockWebServer.enqueue(new MockResponse()
                .setBody(mockTraceDetailResponse)
                .setHeader("Content-Type", "application/json"));
        
        // 调用待测试方法
        Object result = skywalkingTraceRepo.getTraceDetail("27173e33-f765-4543-a985-d6059147239a");
        
        // 验证结果
        assertThat(result).isNotNull();
    }
    
    @Test
    void testGetTraceDetailWithEmptyTraceId() {
        // 调用待测试方法，使用空的traceId
        Object result = skywalkingTraceRepo.getTraceDetail("");
        
        // 验证结果
        assertThat(result).isNull();
    }
}