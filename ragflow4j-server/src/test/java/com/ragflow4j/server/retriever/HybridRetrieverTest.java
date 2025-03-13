package com.ragflow4j.server.retriever;

import com.ragflow4j.core.retriever.ContentRetriever;
import com.ragflow4j.core.retriever.RetrievalResult;
import com.ragflow4j.core.retriever.RetrieverType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class HybridRetrieverTest {

    @Mock
    private ContentRetriever vectorRetriever;

    @Mock
    private ContentRetriever webSearchRetriever;

    private HybridRetriever hybridRetriever;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // 设置模拟检索器的行为
        when(vectorRetriever.getType()).thenReturn(RetrieverType.VECTOR);
        when(webSearchRetriever.getType()).thenReturn(RetrieverType.WEB_SEARCH);

        // 创建测试数据
        RetrievalResult vectorResult1 = createRetrievalResult("Vector content 1", 0.9, RetrieverType.VECTOR);
        RetrievalResult vectorResult2 = createRetrievalResult("Vector content 2", 0.7, RetrieverType.VECTOR);
        List<RetrievalResult> vectorResults = Arrays.asList(vectorResult1, vectorResult2);

        RetrievalResult webResult1 = createRetrievalResult("Web content 1", 0.8, RetrieverType.WEB_SEARCH);
        RetrievalResult webResult2 = createRetrievalResult("Web content 2", 0.6, RetrieverType.WEB_SEARCH);
        List<RetrievalResult> webResults = Arrays.asList(webResult1, webResult2);

        // 设置模拟检索器的返回值
        when(vectorRetriever.retrieve(anyString(), anyInt())).thenReturn(vectorResults);
        when(webSearchRetriever.retrieve(anyString(), anyInt())).thenReturn(webResults);

        when(vectorRetriever.retrieveAsync(anyString(), anyInt()))
                .thenReturn(CompletableFuture.completedFuture(vectorResults));
        when(webSearchRetriever.retrieveAsync(anyString(), anyInt()))
                .thenReturn(CompletableFuture.completedFuture(webResults));
    }

    @Test
    void testBuilderPattern() {
        // 测试Builder模式创建HybridRetriever
        Map<RetrieverType, Double> weights = new HashMap<>();
        weights.put(RetrieverType.VECTOR, 0.7);
        weights.put(RetrieverType.WEB_SEARCH, 0.3);

        hybridRetriever = HybridRetriever.builder()
                .addRetriever(RetrieverType.VECTOR, vectorRetriever, 0.7)
                .addRetriever(RetrieverType.WEB_SEARCH, webSearchRetriever, 0.3)
                .threadPoolSize(4)
                .build();

        assertNotNull(hybridRetriever);
        assertEquals(RetrieverType.HYBRID, hybridRetriever.getType());
    }

    @Test
    void testRetrieve() {
        // 使用构造函数创建HybridRetriever
        Map<RetrieverType, ContentRetriever> retrievers = new HashMap<>();
        retrievers.put(RetrieverType.VECTOR, vectorRetriever);
        retrievers.put(RetrieverType.WEB_SEARCH, webSearchRetriever);

        Map<RetrieverType, Double> weights = new HashMap<>();
        weights.put(RetrieverType.VECTOR, 0.7);
        weights.put(RetrieverType.WEB_SEARCH, 0.3);

        hybridRetriever = new HybridRetriever(retrievers, weights);

        // 执行检索
        List<RetrievalResult> results = hybridRetriever.retrieve("test query", 4);

        // 验证结果
        assertNotNull(results);
        assertEquals(4, results.size());
        
        // 验证结果排序（按分数降序）
        assertTrue(results.get(0).getScore() >= results.get(1).getScore());
        assertTrue(results.get(1).getScore() >= results.get(2).getScore());
        assertTrue(results.get(2).getScore() >= results.get(3).getScore());
    }

    @Test
    void testRetrieveAsync() {
        // 使用Builder模式创建HybridRetriever
        hybridRetriever = HybridRetriever.builder()
                .addRetriever(RetrieverType.VECTOR, vectorRetriever, 0.7)
                .addRetriever(RetrieverType.WEB_SEARCH, webSearchRetriever, 0.3)
                .build();

        // 执行异步检索
        CompletableFuture<List<RetrievalResult>> futureResults = hybridRetriever.retrieveAsync("test query", 4);

        // 等待结果并验证
        List<RetrievalResult> results = futureResults.join();
        assertNotNull(results);
        assertEquals(4, results.size());
        
        // 验证结果排序（按分数降序）
        assertTrue(results.get(0).getScore() >= results.get(1).getScore());
        assertTrue(results.get(1).getScore() >= results.get(2).getScore());
        assertTrue(results.get(2).getScore() >= results.get(3).getScore());
    }

    @Test
    void testUpdateWeight() {
        // 使用Builder模式创建HybridRetriever
        hybridRetriever = HybridRetriever.builder()
                .addRetriever(RetrieverType.VECTOR, vectorRetriever, 0.7)
                .addRetriever(RetrieverType.WEB_SEARCH, webSearchRetriever, 0.3)
                .build();

        // 更新权重
        hybridRetriever.updateWeight(RetrieverType.VECTOR, 0.6);
        hybridRetriever.updateWeight(RetrieverType.WEB_SEARCH, 0.4);
        hybridRetriever.rebalanceWeights(); // 添加重新平衡权重的调用

        // 执行检索
        List<RetrievalResult> results = hybridRetriever.retrieve("test query", 4);

        // 验证结果
        assertNotNull(results);
        assertEquals(4, results.size());
    }

    @Test
    void testAddAndRemoveRetriever() {
        // 创建只有一个检索器的HybridRetriever
        hybridRetriever = HybridRetriever.builder()
                .addRetriever(RetrieverType.VECTOR, vectorRetriever, 1.0)
                .build();

        // 添加新的检索器
        hybridRetriever.addRetriever(RetrieverType.WEB_SEARCH, webSearchRetriever, 0.3);
        hybridRetriever.updateWeight(RetrieverType.VECTOR, 0.7);
        hybridRetriever.rebalanceWeights(); // 添加重新平衡权重的调用

        // 执行检索
        List<RetrievalResult> results = hybridRetriever.retrieve("test query", 4);
        assertNotNull(results);
        assertEquals(4, results.size());

        // 移除检索器
        hybridRetriever.removeRetriever(RetrieverType.WEB_SEARCH);
        hybridRetriever.updateWeight(RetrieverType.VECTOR, 1.0); // 更新剩余检索器的权重
        hybridRetriever.rebalanceWeights(); // 添加重新平衡权重的调用

        // 执行检索
        results = hybridRetriever.retrieve("test query", 2);
        assertNotNull(results);
        assertEquals(2, results.size());
        assertEquals(RetrieverType.VECTOR, results.get(0).getSourceType());
    }

    private RetrievalResult createRetrievalResult(String content, double score, RetrieverType sourceType) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("source", sourceType.name());
        return new RetrievalResult(content, score, metadata, sourceType);
    }
}