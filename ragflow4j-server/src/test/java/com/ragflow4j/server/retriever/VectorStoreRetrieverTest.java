package com.ragflow4j.server.retriever;

import com.ragflow4j.core.embedding.DocumentEmbedding;
import com.ragflow4j.core.retriever.RetrievalResult;
import com.ragflow4j.core.retriever.RetrieverType;
import com.ragflow4j.core.vectorstore.SearchResult;
import com.ragflow4j.core.vectorstore.VectorStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

class VectorStoreRetrieverTest {

    @Mock
    private VectorStore vectorStore;

    @Mock
    private DocumentEmbedding documentEmbedding;

    private VectorStoreRetriever vectorStoreRetriever;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // 设置模拟向量存储的行为
        SearchResult result1 = new SearchResult("doc1", 0.9f);
        SearchResult result2 = new SearchResult("doc2", 0.7f);
        List<SearchResult> searchResults = Arrays.asList(result1, result2);

        when(vectorStore.search(any(float[].class), anyInt()))
                .thenReturn(CompletableFuture.completedFuture(searchResults));

        // 设置模拟文档嵌入的行为
        when(documentEmbedding.embed(any(String.class)))
                .thenReturn(new float[]{0.1f, 0.2f, 0.3f});
    }

    @Test
    void testBuilderPattern() {
        // 测试Builder模式创建VectorStoreRetriever
        vectorStoreRetriever = VectorStoreRetriever.builder()
                .vectorStore(vectorStore)
                .documentEmbedding(documentEmbedding)
                .threadPoolSize(4)
                .build();

        assertNotNull(vectorStoreRetriever);
        assertEquals(RetrieverType.VECTOR, vectorStoreRetriever.getType());
    }

    @Test
    void testRetrieve() {
        // 使用构造函数创建VectorStoreRetriever
        vectorStoreRetriever = new VectorStoreRetriever(vectorStore, documentEmbedding);

        // 执行检索
        List<RetrievalResult> results = vectorStoreRetriever.retrieve("test query", 2);

        // 验证结果
        assertNotNull(results);
        assertEquals(2, results.size());
        assertEquals("doc1", results.get(0).getContent());
        assertEquals(0.9, results.get(0).getScore(), 0.0001, "Score should be approximately 0.9");
        assertEquals(RetrieverType.VECTOR, results.get(0).getSourceType());
    }

    @Test
    void testRetrieveAsync() {
        // 使用Builder模式创建VectorStoreRetriever
        vectorStoreRetriever = VectorStoreRetriever.builder()
                .vectorStore(vectorStore)
                .documentEmbedding(documentEmbedding)
                .build();

        // 执行异步检索
        CompletableFuture<List<RetrievalResult>> futureResults = vectorStoreRetriever.retrieveAsync("test query", 2);

        // 等待结果并验证
        List<RetrievalResult> results = futureResults.join();
        assertNotNull(results);
        assertEquals(2, results.size());
        assertEquals("doc1", results.get(0).getContent());
        assertEquals(0.9, results.get(0).getScore(), 0.0001, "Score should be approximately 0.9");
    }

    @Test
    void testBuilderValidation() {
        // 测试没有向量存储时的验证
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            VectorStoreRetriever.builder()
                    .documentEmbedding(documentEmbedding)
                    .build();
        });

        assertEquals("VectorStore must not be null", exception.getMessage());

        // 测试没有文档嵌入时的验证
        exception = assertThrows(IllegalStateException.class, () -> {
            VectorStoreRetriever.builder()
                    .vectorStore(vectorStore)
                    .build();
        });

        assertEquals("DocumentEmbedding must not be null", exception.getMessage());
    }
}