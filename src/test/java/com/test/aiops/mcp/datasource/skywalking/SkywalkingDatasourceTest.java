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
        Object result = spySkywalkingDatasource.getTraceList("testService", "2023-01-01", "2023-01-02", 
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
        Object result = spySkywalkingDatasource.getTraceList(null, "2023-01-01", "2023-01-02", 
                "SUCCESS", null, "f2a6f356-d4cc-4c3b-ace0-a5f7e949aee2", null, null, null);
        
        // 验证结果
        assertThat(result).isNotNull();
        
        // 验证getServiceId方法没有被调用
        Mockito.verify(spySkywalkingDatasource, Mockito.never()).getServiceId(Mockito.anyString());
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
                                "traceId": "5ee40d5b-dd98-40a6-b381-8e92b6a8ba1a",
                                "segmentId": "9e2d5827-14f9-41ae-8305-ef14193b0136",
                                "spanId": 0,
                                "parentSpanId": -1,
                                "refs": [],
                                "serviceCode": "agent::ui",
                                "serviceInstanceName": "v1.0.0",
                                "startTime": 1747377289812,
                                "endTime": 1747377289824,
                                "endpointName": "/homepage",
                                "type": "Exit",
                                "peer": "frontend",
                                "component": "ajax",
                                "isError": true,
                                "layer": "Http",
                                "tags": [
                                    {
                                        "key": "http.method",
                                        "value": "post"
                                    },
                                    {
                                        "key": "url",
                                        "value": "http://frontend/test"
                                    }
                                ],
                                "logs": [],
                                "attachedEvents": []
                            },
                            {
                                "traceId": "5ee40d5b-dd98-40a6-b381-8e92b6a8ba1a",
                                "segmentId": "82a379e8-e1f7-4aca-b430-92fcf70982da",
                                "spanId": 0,
                                "parentSpanId": -1,
                                "refs": [
                                    {
                                        "traceId": "5ee40d5b-dd98-40a6-b381-8e92b6a8ba1a",
                                        "parentSegmentId": "9e2d5827-14f9-41ae-8305-ef14193b0136",
                                        "parentSpanId": 0,
                                        "type": "CROSS_PROCESS"
                                    }
                                ],
                                "serviceCode": "agent::frontend",
                                "serviceInstanceName": "eef2a0ef0f1a",
                                "startTime": 1747377289815,
                                "endTime": 1747377289822,
                                "endpointName": "/test",
                                "type": "Entry",
                                "peer": "",
                                "component": "APISIX",
                                "isError": false,
                                "layer": "Http",
                                "tags": [
                                    {
                                        "key": "http.method",
                                        "value": "POST"
                                    },
                                    {
                                        "key": "http.params",
                                        "value": "http://frontend/test"
                                    },
                                    {
                                        "key": "http.status",
                                        "value": "404"
                                    }
                                ],
                                "logs": [],
                                "attachedEvents": []
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
        String traceId = "5ee40d5b-dd98-40a6-b381-8e92b6a8ba1a";
        com.test.aiops.mcp.datasource.skywalking.entity.TraceDetailResponse result = skywalkingDatasource.getTraceDetail(traceId);
        
        // 验证结果不为null
        assertThat(result).isNotNull();
        // 验证数据结构解析正确
        assertThat(result.getData()).isNotNull();
        assertThat(result.getData().getTrace()).isNotNull();
        assertThat(result.getData().getTrace().getSpans()).isNotNull();
        assertThat(result.getData().getTrace().getSpans()).hasSize(2);
        
        // 验证第一个span的数据
        com.test.aiops.mcp.datasource.skywalking.entity.TraceDetailResponse.Span firstSpan = result.getData().getTrace().getSpans().get(0);
        assertThat(firstSpan.getTraceId()).isEqualTo("5ee40d5b-dd98-40a6-b381-8e92b6a8ba1a");
        assertThat(firstSpan.getSegmentId()).isEqualTo("9e2d5827-14f9-41ae-8305-ef14193b0136");
        assertThat(firstSpan.getSpanId()).isEqualTo(0);
        assertThat(firstSpan.getParentSpanId()).isEqualTo(-1);
        assertThat(firstSpan.getServiceCode()).isEqualTo("agent::ui");
        assertThat(firstSpan.getEndpointName()).isEqualTo("/homepage");
        assertThat(firstSpan.getIsError()).isTrue();
        
        // 验证第二个span的数据
        com.test.aiops.mcp.datasource.skywalking.entity.TraceDetailResponse.Span secondSpan = result.getData().getTrace().getSpans().get(1);
        assertThat(secondSpan.getTraceId()).isEqualTo("5ee40d5b-dd98-40a6-b381-8e92b6a8ba1a");
        assertThat(secondSpan.getRefs()).hasSize(1);
        assertThat(secondSpan.getRefs().get(0).getType()).isEqualTo("CROSS_PROCESS");
        assertThat(secondSpan.getServiceCode()).isEqualTo("agent::frontend");
        assertThat(secondSpan.getIsError()).isFalse();
    }
    
    @Test
    void testGetTraceDetailWithEmptyTraceId() {
        // 调用待测试方法，使用空的traceId
        com.test.aiops.mcp.datasource.skywalking.entity.TraceDetailResponse result = skywalkingDatasource.getTraceDetail("");
        
        // 验证结果为null
        assertThat(result).isNull();
        
        // 调用待测试方法，使用null的traceId
        result = skywalkingDatasource.getTraceDetail(null);
        
        // 验证结果为null
        assertThat(result).isNull();
    }
}