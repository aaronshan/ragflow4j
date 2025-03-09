package com.ragflow4j.core.vectorstore;

import io.milvus.client.MilvusServiceClient;
import io.milvus.param.ConnectParam;
import io.milvus.param.R;
import io.milvus.param.MetricType; // 新增导入
import io.milvus.param.collection.CreateCollectionParam;
import io.milvus.param.collection.FieldType;
import io.milvus.param.dml.DeleteParam;
import io.milvus.param.dml.InsertParam;
import io.milvus.param.dml.SearchParam;
import io.milvus.grpc.DataType;
import io.milvus.response.SearchResultsWrapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Milvus 实现的 VectorStore 接口
 */
public class MilvusVectorStore implements VectorStore {
    private final MilvusServiceClient milvusClient;
    private final String collectionName;
    private final int dimension;
    private final ExecutorService executor = Executors.newFixedThreadPool(4); // 用于异步操作的线程池

    // 构造函数
    public MilvusVectorStore(String host, int port, String collectionName, int dimension) {
        ConnectParam connectParam = ConnectParam.newBuilder()
            .withHost(host)
            .withPort(port)
            .build();
        milvusClient = new MilvusServiceClient(connectParam);
        this.collectionName = collectionName;
        this.dimension = dimension;

        if (!collectionExists()) {
            createCollection();
        }
    }

    // 新增构造函数，用于测试注入
    public MilvusVectorStore(MilvusServiceClient milvusClient, String collectionName, int dimension) {
        this.milvusClient = milvusClient;
        this.collectionName = collectionName;
        this.dimension = dimension;

        if (!collectionExists()) {
            createCollection();
        }
    }

    // 检查集合是否存在
    private boolean collectionExists() {
        try {
            R<Boolean> response = milvusClient.hasCollection(
                io.milvus.param.collection.HasCollectionParam.newBuilder()
                    .withCollectionName(collectionName)
                    .build()
            );
            return response.getData();
        } catch (Exception e) {
            return false;
        }
    }

    // 创建集合
    private void createCollection() {
        FieldType idField = FieldType.newBuilder()
            .withName("id")
            .withDataType(DataType.VarChar)
            .withMaxLength(256)
            .withPrimaryKey(true)
            .build();

        FieldType vectorField = FieldType.newBuilder()
            .withName("vector")
            .withDataType(DataType.FloatVector)
            .withDimension(dimension)
            .build();

        FieldType metadataField = FieldType.newBuilder()
            .withName("metadata")
            .withDataType(DataType.VarChar)
            .withMaxLength(65535)
            .build();

        CreateCollectionParam createParam = CreateCollectionParam.newBuilder()
            .withCollectionName(collectionName)
            .addFieldType(idField)
            .addFieldType(vectorField)
            .addFieldType(metadataField)
            .build();

        milvusClient.createCollection(createParam);
    }

    // 生成唯一 ID
    private String generateUniqueId() {
        return java.util.UUID.randomUUID().toString();
    }

    @Override
    public CompletableFuture<Boolean> addVectors(List<float[]> vectors, List<String> metadata) {
        List<String> ids = new ArrayList<>();
        List<List<Float>> vectorList = new ArrayList<>();

        for (int i = 0; i < vectors.size(); i++) {
            String generatedId = generateUniqueId();
            ids.add(generatedId);
            List<Float> vector = new ArrayList<>();
            for (float v : vectors.get(i)) {
                vector.add(v);
            }
            vectorList.add(vector);
        }

        List<InsertParam.Field> fields = new ArrayList<>();
        fields.add(new InsertParam.Field("id", ids));
        fields.add(new InsertParam.Field("vector", vectorList));
        fields.add(new InsertParam.Field("metadata", metadata));

        InsertParam insertParam = InsertParam.newBuilder()
            .withCollectionName(collectionName)
            .withFields(fields)
            .build();

        CompletableFuture<R<io.milvus.grpc.MutationResult>> future = CompletableFuture.supplyAsync(() -> {
            return milvusClient.insert(insertParam);
        }, executor);

        return future.thenApply(response -> response.getStatus() == R.Status.Success.getCode())
            .exceptionally(ex -> false);
    }

    @Override
    public CompletableFuture<Boolean> addVectorsBatch(List<float[]> vectors, List<String> metadata, int batchSize) {
        List<CompletableFuture<Boolean>> batchFutures = new ArrayList<>();

        for (int i = 0; i < vectors.size(); i += batchSize) {
            int end = Math.min(i + batchSize, vectors.size());
            List<float[]> batchVectors = vectors.subList(i, end);
            List<String> batchMetadata = metadata.subList(i, end);
            CompletableFuture<Boolean> batchFuture = addVectors(batchVectors, batchMetadata);
            batchFutures.add(batchFuture);
        }

        CompletableFuture<Void> allFutures = CompletableFuture.allOf(batchFutures.toArray(new CompletableFuture[0]));
        return allFutures.thenApply(v -> {
            return batchFutures.stream().map(CompletableFuture::join).allMatch(Boolean::booleanValue);
        });
    }

    @Override
    public CompletableFuture<List<SearchResult>> search(float[] queryVector, int topK) {
        List<Float> queryVectorList = new ArrayList<>();
        for (float v : queryVector) {
            queryVectorList.add(v);
        }

        SearchParam searchParam = SearchParam.newBuilder()
            .withCollectionName(collectionName)
            .withMetricType(MetricType.L2)
            .withTopK(topK)
            .withVectors(Collections.singletonList(queryVectorList))
            .withVectorFieldName("vector")
            .build();

        CompletableFuture<R<io.milvus.grpc.SearchResults>> future = CompletableFuture.supplyAsync(() -> {
            return milvusClient.search(searchParam);
        }, executor);

        return future.thenApply(response -> {
            if (response.getStatus() != R.Status.Success.getCode()) {
                return new ArrayList<SearchResult>(); // 返回空的 SearchResult 列表
            }
            SearchResultsWrapper wrapper = new SearchResultsWrapper(response.getData().getResults());
            List<SearchResult> results = new ArrayList<>();
            List<SearchResultsWrapper.IDScore> scores = wrapper.getIDScore(0);
            for (SearchResultsWrapper.IDScore score : scores) {
                results.add(new SearchResult(score.getStrID(), (double) score.getScore()));
            }
            return results;
        }).exceptionally(ex -> {
            return new ArrayList<SearchResult>(); // 异常时返回空的 SearchResult 列表
        });
    }


    @Override
    public CompletableFuture<Boolean> deleteVectors(List<String> ids) {
        DeleteParam deleteParam = DeleteParam.newBuilder()
            .withCollectionName(collectionName)
            .withExpr("id in [" + String.join(",", ids.stream().map(id -> "\"" + id + "\"").toArray(String[]::new)) + "]")
            .build();

        CompletableFuture<R<io.milvus.grpc.MutationResult>> future = CompletableFuture.supplyAsync(() -> {
            return milvusClient.delete(deleteParam);
        }, executor);

        return future.thenApply(response -> response.getStatus() == R.Status.Success.getCode())
            .exceptionally(ex -> false);
    }

    @Override
    public CompletableFuture<Boolean> updateVector(String id, float[] vector, String metadata) {
        DeleteParam deleteParam = DeleteParam.newBuilder()
            .withCollectionName(collectionName)
            .withExpr("id in [\"" + id + "\"]")
            .build();

        CompletableFuture<R<io.milvus.grpc.MutationResult>> deleteFuture = CompletableFuture.supplyAsync(() -> {
            return milvusClient.delete(deleteParam);
        }, executor);

        List<Float> vectorList = new ArrayList<>();
        for (float v : vector) {
            vectorList.add(v);
        }

        List<InsertParam.Field> fields = new ArrayList<>();
        fields.add(new InsertParam.Field("id", Collections.singletonList(id)));
        fields.add(new InsertParam.Field("vector", Collections.singletonList(vectorList)));
        fields.add(new InsertParam.Field("metadata", Collections.singletonList(metadata)));

        InsertParam insertParam = InsertParam.newBuilder()
            .withCollectionName(collectionName)
            .withFields(fields)
            .build();

        CompletableFuture<R<io.milvus.grpc.MutationResult>> insertFuture = CompletableFuture.supplyAsync(() -> {
            return milvusClient.insert(insertParam);
        }, executor);

        return deleteFuture.thenCompose(deleteResponse -> {
            if (deleteResponse.getStatus() == R.Status.Success.getCode()) {
                return insertFuture.thenApply(insertResponse ->
                    insertResponse.getStatus() == R.Status.Success.getCode());
            } else {
                return CompletableFuture.completedFuture(false);
            }
        }).exceptionally(ex -> false);
    }
}