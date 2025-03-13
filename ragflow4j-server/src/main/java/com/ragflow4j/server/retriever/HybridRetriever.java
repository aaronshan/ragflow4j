package com.ragflow4j.server.retriever;

import com.ragflow4j.core.retriever.ContentRetriever;
import com.ragflow4j.core.retriever.RetrievalResult;
import com.ragflow4j.core.retriever.RetrieverType;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * 混合检索器实现
 * 支持多种检索策略的组合和结果合并
 */
public class HybridRetriever implements ContentRetriever {
    private final Map<RetrieverType, ContentRetriever> retrievers;
    private final Map<RetrieverType, Double> weights;
    private final ExecutorService executorService;
    private final int threadPoolSize;
    
    /**
     * 使用Builder模式创建HybridRetriever实例
     * 
     * @param builder 构建器实例
     */
    private HybridRetriever(Builder builder) {
        this.retrievers = new ConcurrentHashMap<>(builder.retrievers);
        this.weights = new ConcurrentHashMap<>(builder.weights);
        this.threadPoolSize = builder.threadPoolSize;
        this.executorService = Executors.newFixedThreadPool(this.threadPoolSize);
        validateWeights();
    }
    
    /**
     * 兼容旧版本的构造函数
     * 
     * @param retrievers 检索器映射
     * @param weights 权重映射
     */
    public HybridRetriever(Map<RetrieverType, ContentRetriever> retrievers, Map<RetrieverType, Double> weights) {
        this.retrievers = new ConcurrentHashMap<>(retrievers);
        this.weights = new ConcurrentHashMap<>(weights);
        this.threadPoolSize = Runtime.getRuntime().availableProcessors();
        this.executorService = Executors.newFixedThreadPool(this.threadPoolSize);
        validateWeights();
    }
    
    /**
     * 创建一个新的Builder实例
     * 
     * @return Builder实例
     */
    public static Builder builder() {
        return new Builder();
    }

    private void validateWeights() {
        if (retrievers.isEmpty()) {
            return;
        }
        
        double totalWeight = weights.values().stream().mapToDouble(Double::doubleValue).sum();
        if (Math.abs(totalWeight - 1.0) > 0.0001) {
            // 如果权重总和不为1.0，自动调整权重
            double factor = 1.0 / totalWeight;
            weights.forEach((key, value) -> weights.put(key, value * factor));
        }
    }

    @Override
    public List<RetrievalResult> retrieve(String query, int topK) {
        // 从每个检索器获取结果
        List<RetrievalResult> allResults = new ArrayList<>();
        for (Map.Entry<RetrieverType, ContentRetriever> entry : retrievers.entrySet()) {
            RetrieverType type = entry.getKey();
            ContentRetriever retriever = entry.getValue();
            List<RetrievalResult> results = retriever.retrieve(query, topK);
            // 应用权重
            results.forEach(result -> result.setScore(result.getScore() * weights.get(type)));
            allResults.addAll(results);
        }

        // 合并和排序结果
        return mergeAndRankResults(allResults, topK);
    }

    @Override
    public CompletableFuture<List<RetrievalResult>> retrieveAsync(String query, int topK) {
        // 异步获取所有检索器的结果
        List<CompletableFuture<List<RetrievalResult>>> futures = retrievers.entrySet().stream()
                .map(entry -> {
                    RetrieverType type = entry.getKey();
                    ContentRetriever retriever = entry.getValue();
                    return retriever.retrieveAsync(query, topK)
                            .thenApply(results -> {
                                // 应用权重
                                results.forEach(result -> result.setScore(result.getScore() * weights.get(type)));
                                return results;
                            });
                })
                .collect(Collectors.toList());

        // 等待所有异步操作完成并合并结果
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> futures.stream()
                        .map(CompletableFuture::join)
                        .flatMap(List::stream)
                        .collect(Collectors.toList()))
                .thenApply(results -> mergeAndRankResults(results, topK));
    }

    private List<RetrievalResult> mergeAndRankResults(List<RetrievalResult> results, int topK) {
        // 根据分数排序并限制返回数量
        return results.stream()
                .sorted(Comparator.comparingDouble(RetrievalResult::getScore).reversed())
                .limit(topK)
                .collect(Collectors.toList());
    }

    @Override
    public RetrieverType getType() {
        return RetrieverType.HYBRID;
    }

    public void close() {
        executorService.shutdown();
        retrievers.values().forEach(retriever -> {
            if (retriever instanceof AutoCloseable) {
                try {
                    ((AutoCloseable) retriever).close();
                } catch (Exception e) {
                    // 忽略关闭异常
                }
            }
        });
    }

    /**
     * 更新检索器权重
     *
     * @param type 检索器类型
     * @param weight 新的权重值
     */
    public void updateWeight(RetrieverType type, double weight) {
        weights.put(type, weight);
        // 不在这里立即验证权重，允许临时的不平衡状态
    }

    /**
     * 添加新的检索器
     *
     * @param type 检索器类型
     * @param retriever 检索器实例
     * @param weight 权重值
     */
    public void addRetriever(RetrieverType type, ContentRetriever retriever, double weight) {
        retrievers.put(type, retriever);
        weights.put(type, weight);
        // 不在这里立即验证权重，允许临时的不平衡状态
    }

    /**
     * 移除检索器
     *
     * @param type 检索器类型
     */
    public void removeRetriever(RetrieverType type) {
        retrievers.remove(type);
        weights.remove(type);
        // 不在这里立即验证权重，允许临时的不平衡状态
    }
    
    /**
     * 重新平衡所有检索器的权重，使总和为1.0
     */
    public void rebalanceWeights() {
        if (retrievers.isEmpty()) {
            return;
        }
        
        validateWeights();
    }
    
    /**
     * HybridRetriever的Builder类
     */
    public static class Builder {
        private Map<RetrieverType, ContentRetriever> retrievers = new ConcurrentHashMap<>();
        private Map<RetrieverType, Double> weights = new ConcurrentHashMap<>();
        private int threadPoolSize = Runtime.getRuntime().availableProcessors();
        
        /**
         * 添加检索器及其权重
         * 
         * @param type 检索器类型
         * @param retriever 检索器实例
         * @param weight 权重值
         * @return Builder实例
         */
        public Builder addRetriever(RetrieverType type, ContentRetriever retriever, double weight) {
            this.retrievers.put(type, retriever);
            this.weights.put(type, weight);
            return this;
        }
        
        /**
         * 设置线程池大小
         * 
         * @param threadPoolSize 线程池大小
         * @return Builder实例
         */
        public Builder threadPoolSize(int threadPoolSize) {
            this.threadPoolSize = threadPoolSize;
            return this;
        }
        
        /**
         * 构建HybridRetriever实例
         * 
         * @return HybridRetriever实例
         */
        public HybridRetriever build() {
            if (retrievers.isEmpty()) {
                throw new IllegalStateException("At least one retriever must be added");
            }
            
            double totalWeight = weights.values().stream().mapToDouble(Double::doubleValue).sum();
            if (Math.abs(totalWeight - 1.0) > 0.0001) {
                throw new IllegalArgumentException("Weights must sum to 1.0, current sum: " + totalWeight);
            }
            
            return new HybridRetriever(this);
        }
    }
}