package com.ragflow4j.core.scoring;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * 抽象评分服务基类
 * 提供缓存机制和异步处理框架
 */
public abstract class AbstractScoringService implements ScoringService {
    protected final Map<String, CompletableFuture<List<ScoringResult>>> cache;
    protected final ExecutorService executor;

    protected AbstractScoringService() {
        this.cache = new ConcurrentHashMap<>();
        this.executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
    }

    @Override
    public CompletableFuture<List<ScoringResult>> score(String query, List<String> documents) {
        String cacheKey = generateCacheKey(query, documents);
        return cache.computeIfAbsent(cacheKey, k ->
            CompletableFuture.supplyAsync(() -> computeScores(query, documents), executor)
                .exceptionally(throwable -> {
                    cache.remove(cacheKey);
                    throw new RuntimeException("评分计算失败", throwable);
                })
        );
    }

    @Override
    public CompletableFuture<List<List<ScoringResult>>> batchScore(List<String> queries, List<String> documents) {
        List<CompletableFuture<List<ScoringResult>>> futures = queries.stream()
                .map(query -> score(query, documents))
                .collect(Collectors.toList());

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> futures.stream()
                        .map(CompletableFuture::join)
                        .collect(Collectors.toList()));
    }

    @Override
    public void clearCache() {
        cache.clear();
    }

    /**
     * 关闭评分服务，清理资源
     */
    public void shutdown() {
        clearCache();
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
        }
    }

    /**
     * 生成缓存键
     *
     * @param query 查询文本
     * @param documents 文档列表
     * @return 缓存键
     */
    protected abstract String generateCacheKey(String query, List<String> documents);

    /**
     * 计算文档评分
     *
     * @param query 查询文本
     * @param documents 文档列表
     * @return 评分结果列表
     */
    protected abstract List<ScoringResult> computeScores(String query, List<String> documents);
}