package com.ragflow4j.core.scoring;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class JinaScoringServiceTest {

    private MockWebServer mockWebServer;
    private JinaScoringService scoringService;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() throws IOException {
        // 初始化 MockWebServer
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        
        // 初始化测试对象
        String mockUrl = mockWebServer.url("").toString();
        scoringService = JinaScoringService.builder("test-api-key", mockUrl)
                .connectTimeout(5, java.util.concurrent.TimeUnit.SECONDS)
                .readTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
                .writeTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
                .maxRetries(2)
                .build();

        objectMapper = new ObjectMapper();
    }
    
    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void testBuilderWithNullApiKey() {
        assertThrows(IllegalArgumentException.class, () -> {
            JinaScoringService.builder(null);
        });
    }

    @Test
    void testBuilderWithEmptyApiKey() {
        assertThrows(IllegalArgumentException.class, () -> {
            JinaScoringService.builder("");
        });
    }

    @Test
    void testBuilderWithNegativeMaxRetries() {
        assertThrows(IllegalArgumentException.class, () -> {
            JinaScoringService.builder("test-api-key").maxRetries(-1);
        });
    }

    @Test
    void testComputeScoresWithNullQuery() {
        List<String> documents = Arrays.asList("doc1", "doc2");
        assertThrows(IllegalArgumentException.class, () -> {
            scoringService.computeScores(null, documents);
        });
    }

    @Test
    void testComputeScoresWithEmptyDocuments() {
        assertThrows(IllegalArgumentException.class, () -> {
            scoringService.computeScores("query", new java.util.ArrayList<String>());
        });
    }

    @Test
    void testComputeScoresSuccess() throws IOException, InterruptedException {
        // 模拟 API 返回的 JSON 数据
        String responseJson = "{\"results\": [" +
                "{\"document\": \"doc1\", \"score\": 0.95, \"metadata\": {\"key\": \"value1\"}}," +
                "{\"document\": \"doc2\", \"score\": 0.85, \"metadata\": {\"key\": \"value2\"}}" +
                "]}";
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody(responseJson));

        List<String> documents = Arrays.asList("doc1", "doc2");
        List<ScoringResult> results = scoringService.computeScores("test query", documents);

        // 验证请求
        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertEquals("POST", recordedRequest.getMethod());
        assertTrue(recordedRequest.getHeader("Authorization").contains("test-api-key"));
        
        // 验证结果
        assertEquals(2, results.size());
        assertEquals("doc1", results.get(0).getDocument());
        assertEquals(0.95, results.get(0).getScore(), 0.001);
        assertEquals("{\"key\":\"value1\"}", results.get(0).getMetadata());
        assertEquals("doc2", results.get(1).getDocument());
        assertEquals(0.85, results.get(1).getScore(), 0.001);
        assertEquals("{\"key\":\"value2\"}", results.get(1).getMetadata());
    }

    @Test
    void testComputeScoresWithApiFailure() throws IOException {
        // 模拟 API 返回错误
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(500)
                .setBody("{\"error\": \"Internal Server Error\"}"));

        List<String> documents = Arrays.asList("doc1", "doc2");
        Exception exception = assertThrows(RuntimeException.class, () -> {
            scoringService.computeScores("test query", documents);
        });
        assertEquals("Jina API调用失败", exception.getMessage());
    }

    @Test
    void testGenerateCacheKey() {
        List<String> documents = Arrays.asList("doc1", "doc2");
        String cacheKey = scoringService.generateCacheKey("test query", documents);
        String expectedKey = String.format("%s_%d", "test query".hashCode(), documents.hashCode());
        assertEquals(expectedKey, cacheKey);
    }

    @Test
    void testRetryInterceptorSuccess() throws IOException {
        // 测试 RetryInterceptor 在成功时只调用一次
        String responseJson = "{\"results\": [{\"document\": \"doc1\", \"score\": 0.95, \"metadata\": {\"key\": \"value\"}}]}"; 
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody(responseJson));

        List<String> documents = Arrays.asList("doc1");
        List<ScoringResult> results = scoringService.computeScores("test query", documents);

        assertEquals(1, results.size());
        assertEquals(1, mockWebServer.getRequestCount()); // 验证只请求了一次
    }

    @Test
    void testRetryInterceptorWithRetry() throws IOException {
        // 模拟第一次失败，第二次成功
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(503)
                .setBody("{\"error\": \"Service Unavailable\"}"));
                
        String responseJson = "{\"results\": [{\"document\": \"doc1\", \"score\": 0.95, \"metadata\": {\"key\": \"value\"}}]}"; 
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody(responseJson));

        List<String> documents = Arrays.asList("doc1");
        List<ScoringResult> results = scoringService.computeScores("test query", documents);

        assertEquals(1, results.size());
        assertEquals(2, mockWebServer.getRequestCount()); // 验证重试发生
    }
}