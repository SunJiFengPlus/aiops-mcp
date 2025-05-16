package com.test.aiops.mcp.datasource.skywalking;

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

class SkywalkingDatasourceTest {

    private MockWebServer mockWebServer;
    private SkywalkingDatasource skywalkingDatasource;
    private SkywalkingDatasource spySkywalkingDatasource;
    
    @BeforeEach
    void setUp() throws IOException {
        // 创建MockWebServer，模拟Skywalking OAP服务器
        mockWebServer = new MockWebServer();
        mockWebServer.start(10000);
        
        // 创建SkywalkingDatasource实例
        skywalkingDatasource = new SkywalkingDatasource();
        ReflectionTestUtils.setField(skywalkingDatasource, "url", "http://localhost:10000");
        ReflectionTestUtils.setField(skywalkingDatasource, "graphqlPath", "/graphql");
        
        // 创建spy对象，用于部分mock
        spySkywalkingDatasource = Mockito.spy(skywalkingDatasource);
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
        
        // 调用待测试方法
        String serviceId = skywalkingDatasource.getServiceId("app");
        
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
        
        // 调用待测试方法，使用不存在的服务名
        String serviceId = skywalkingDatasource.getServiceId("nonExistentService");
        
        // 验证结果
        assertThat(serviceId).isNull();
    }
    
    @Test
    void testGetLogs() {
        // Mock getServiceId方法，避免调用真实方法
        Mockito.doReturn("service-123").when(spySkywalkingDatasource).getServiceId("testService");
        
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
        Object result = spySkywalkingDatasource.getLogs("testService", "2023-01-01", "2023-01-02", 
                null, null, tags);
        
        // 验证结果
        assertThat(result).isNotNull();
        
        // 验证getServiceId方法被调用
        Mockito.verify(spySkywalkingDatasource).getServiceId("testService");
    }
    
    @Test
    void testGetLogsServiceIdNotFound() {
        // Mock getServiceId方法返回null，模拟服务未找到的情况
        Mockito.doReturn(null).when(spySkywalkingDatasource).getServiceId("nonExistentService");
        
        // 调用待测试方法
        Map<String, String> tags = new HashMap<>();
        tags.put("level", "INFO");
        Object result = spySkywalkingDatasource.getLogs("nonExistentService", "2023-01-01", "2023-01-02", 
                null, null, tags);
        
        // 验证结果为null
        assertThat(result).isNull();
        
        // 验证getServiceId方法被调用
        Mockito.verify(spySkywalkingDatasource).getServiceId("nonExistentService");
    }
    
    @Test
    void testGetTraces() {
        // Mock getServiceId方法，避免调用真实方法
        Mockito.doReturn("service-123").when(spySkywalkingDatasource).getServiceId("testService");
        
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
        Object result = spySkywalkingDatasource.getTraces("testService", "2023-01-01", "2023-01-02", 
                "SUCCESS", null, null, 1000, 0, tags);
        
        // 验证结果
        assertThat(result).isNotNull();
        
        // 验证getServiceId方法被调用
        Mockito.verify(spySkywalkingDatasource).getServiceId("testService");
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
        
        // 调用待测试方法（不提供serviceName）
        Object result = spySkywalkingDatasource.getTraces(null, "2023-01-01", "2023-01-02", 
                "SUCCESS", null, "f2a6f356-d4cc-4c3b-ace0-a5f7e949aee2", null, null, null);
        
        // 验证结果
        assertThat(result).isNotNull();
        
        // 验证getServiceId方法没有被调用
        Mockito.verify(spySkywalkingDatasource, Mockito.never()).getServiceId(Mockito.anyString());
    }
    
    @Test
    void testGetTracesServiceIdNotFound() {
        // Mock getServiceId方法返回null，模拟服务未找到的情况
        Mockito.doReturn(null).when(spySkywalkingDatasource).getServiceId("nonExistentService");
        
        // 调用待测试方法
        Object result = spySkywalkingDatasource.getTraces("nonExistentService", "2023-01-01", "2023-01-02", 
                "SUCCESS", null, null, 1000, 0, null);
        
        // 验证结果为null
        assertThat(result).isNull();
        
        // 验证getServiceId方法被调用
        Mockito.verify(spySkywalkingDatasource).getServiceId("nonExistentService");
    }
}