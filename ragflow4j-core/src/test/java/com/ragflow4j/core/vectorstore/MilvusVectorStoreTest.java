package com.ragflow4j.core.vectorstore;

import io.milvus.client.MilvusServiceClient;
import io.milvus.grpc.MutationResult;
import io.milvus.grpc.SearchResults;
import io.milvus.param.R;
import io.milvus.param.dml.DeleteParam;
import io.milvus.param.dml.InsertParam;
import io.milvus.param.dml.SearchParam;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MilvusVectorStoreTest {
    @Mock
    private MilvusServiceClient milvusClient;

    private MilvusVectorStore vectorStore;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // 模拟集合存在的情况
        when(milvusClient.hasCollection(any())).thenReturn(R.success(true));
        // 使用新构造函数注入模拟的 milvusClient
        vectorStore = new MilvusVectorStore(milvusClient, "test_collection", 128);
    }

    @Test
    void testAddVectors() {
        // 准备测试数据
        List<float[]> vectors = Arrays.asList(
            new float[]{1.0f, 2.0f},
            new float[]{3.0f, 4.0f}
        );
        List<String> metadata = Arrays.asList("meta1", "meta2");

        // 模拟插入成功
        when(milvusClient.insert(any(InsertParam.class)))
            .thenReturn(R.success(MutationResult.newBuilder().build()));

        // 执行测试
        CompletableFuture<Boolean> result = vectorStore.addVectors(vectors, metadata);

        // 验证结果
        assertTrue(result.join());
        verify(milvusClient).insert(any(InsertParam.class));
    }

    @Test
    void testSearch() {
        // 准备测试数据
        float[] queryVector = new float[]{1.0f, 2.0f};
        int topK = 2;

        // 模拟搜索结果
        SearchResults searchResults = SearchResults.newBuilder().build();
        when(milvusClient.search(any(SearchParam.class)))
            .thenReturn(R.success(searchResults));

        // 执行测试
        CompletableFuture<List<SearchResult>> result = vectorStore.search(queryVector, topK);

        // 验证结果
        assertNotNull(result.join());
        verify(milvusClient).search(any(SearchParam.class));
    }

    @Test
    void testDeleteVectors() {
        // 准备测试数据
        List<String> ids = Arrays.asList("id1", "id2");

        // 模拟删除成功
        when(milvusClient.delete(any(DeleteParam.class)))
            .thenReturn(R.success(MutationResult.newBuilder().build()));

        // 执行测试
        CompletableFuture<Boolean> result = vectorStore.deleteVectors(ids);

        // 验证结果
        assertTrue(result.join());
        verify(milvusClient).delete(any(DeleteParam.class));
    }

    @Test
    void testUpdateVector() {
        // 准备测试数据
        String id = "test_id";
        float[] vector = new float[]{1.0f, 2.0f};
        String metadata = "updated_meta";

        // 模拟删除和插入都成功
        when(milvusClient.delete(any(DeleteParam.class)))
            .thenReturn(R.success(MutationResult.newBuilder().build()));
        when(milvusClient.insert(any(InsertParam.class)))
            .thenReturn(R.success(MutationResult.newBuilder().build()));

        // 执行测试
        CompletableFuture<Boolean> result = vectorStore.updateVector(id, vector, metadata);

        // 验证结果
        assertTrue(result.join());
        verify(milvusClient).delete(any(DeleteParam.class));
        verify(milvusClient).insert(any(InsertParam.class));
    }

    @Test
    void testAddVectorsBatch() {
        // 准备测试数据
        List<float[]> vectors = new ArrayList<>();
        List<String> metadata = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            vectors.add(new float[]{i * 1.0f, i * 2.0f});
            metadata.add("meta" + i);
        }

        // 模拟插入成功
        when(milvusClient.insert(any(InsertParam.class)))
            .thenReturn(R.success(MutationResult.newBuilder().build()));

        // 执行测试
        CompletableFuture<Boolean> result = vectorStore.addVectorsBatch(vectors, metadata, 2);

        // 验证结果
        assertTrue(result.join());
        // 验证是否进行了3次插入操作（5个元素，每批2个，向上取整为3批）
        verify(milvusClient, times(3)).insert(any(InsertParam.class));
    }
}