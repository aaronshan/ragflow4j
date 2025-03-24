package com.ragflow4j.core.scoring;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 基于Cohere API的评分服务实现
 */
public class CohereScoringService extends AbstractScoringService {
    private static final String DEFAULT_API_URL = "https://api.cohere.ai/v1/";
    private final String apiUrl;
    private final String apiKey;
    private final OkHttpClient client;
    private final ObjectMapper objectMapper;

    private CohereScoringService(Builder builder) {
        super();
        this.apiKey = builder.apiKey;
        this.apiUrl = builder.apiUrl;
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder()
                .connectTimeout(builder.connectTimeout, builder.timeoutUnit)
                .readTimeout(builder.readTimeout, builder.timeoutUnit)
                .writeTimeout(builder.writeTimeout, builder.timeoutUnit);
        
        if (builder.maxRetries > 0) {
            clientBuilder.addInterceptor(new RetryInterceptor(builder.maxRetries));
        }
        
        this.client = clientBuilder.build();
        this.objectMapper = new ObjectMapper();
    }

    public static Builder builder(String apiKey) {
        return new Builder(apiKey, DEFAULT_API_URL);
    }

    public static Builder builder(String apiKey, String apiUrl) {
        return new Builder(apiKey, apiUrl);
    }

    public static class Builder {
        private final String apiKey;
        private String apiUrl;
        private int connectTimeout = 10;
        private int readTimeout = 30;
        private int writeTimeout = 30;
        private int maxRetries = 3;
        private java.util.concurrent.TimeUnit timeoutUnit = java.util.concurrent.TimeUnit.SECONDS;

        private Builder(String apiKey, String apiUrl) {
            if (apiKey == null || apiKey.trim().isEmpty()) {
                throw new IllegalArgumentException("API Key不能为空");
            }
            this.apiKey = apiKey;
            this.apiUrl = apiUrl;
        }

        public Builder connectTimeout(int timeout, java.util.concurrent.TimeUnit unit) {
            this.connectTimeout = timeout;
            this.timeoutUnit = unit;
            return this;
        }

        public Builder readTimeout(int timeout, java.util.concurrent.TimeUnit unit) {
            this.readTimeout = timeout;
            this.timeoutUnit = unit;
            return this;
        }

        public Builder writeTimeout(int timeout, java.util.concurrent.TimeUnit unit) {
            this.writeTimeout = timeout;
            this.timeoutUnit = unit;
            return this;
        }

        public Builder maxRetries(int maxRetries) {
            if (maxRetries < 0) {
                throw new IllegalArgumentException("重试次数不能小于0");
            }
            this.maxRetries = maxRetries;
            return this;
        }

        public CohereScoringService build() {
            return new CohereScoringService(this);
        }
    }

    @Override
    protected String generateCacheKey(String query, List<String> documents) {
        return String.format("%s_%d", query.hashCode(), documents.hashCode());
    }

    @Override
    protected List<ScoringResult> computeScores(String query, List<String> documents) {
        if (query == null || query.trim().isEmpty()) {
            throw new IllegalArgumentException("查询内容不能为空");
        }
        if (documents == null || documents.isEmpty()) {
            throw new IllegalArgumentException("文档列表不能为空");
        }

        try {
            MediaType mediaType = MediaType.parse("application/json");
            Map<String, Object> requestBody = new java.util.HashMap<>();
            requestBody.put("query", query);
            requestBody.put("documents", documents);
            requestBody.put("top_n", documents.size());
            requestBody.put("return_documents", true);

            String jsonBody = objectMapper.writeValueAsString(requestBody);
            RequestBody body = RequestBody.create(jsonBody, mediaType);

            Request request = new Request.Builder()
                    .url(apiUrl)
                    .post(body)
                    .addHeader("Authorization", "Bearer " + apiKey)
                    .addHeader("Content-Type", "application/json")
                    .build();

            Response response = null;
            try {
                response = client.newCall(request).execute();
                if (!response.isSuccessful()) {
                    throw new RuntimeException(String.format("Cohere API调用失败: %d", response.code()));
                }
                if (response.body() == null) {
                    throw new RuntimeException("API响应格式错误: 响应体为空");
                }

                String responseBody = response.body().string();
                Map<String, Object> result = objectMapper.readValue(responseBody, Map.class);

                List<Map<String, Object>> results = (List<Map<String, Object>>) result.get("results");
                if (results == null) {
                    throw new RuntimeException("API响应格式错误: 缺少results字段");
                }

                List<ScoringResult> scoringResults = new ArrayList<>();
                for (Map<String, Object> item : results) {
                    String document = (String) item.get("document");
                    Number relevanceScore = (Number) item.get("relevance_score");
                    if (document == null || relevanceScore == null) {
                        continue;
                    }
                    scoringResults.add(new ScoringResult(document, relevanceScore.doubleValue()));
                }

                return scoringResults;
            } finally {
                if (response != null && response.body() != null) {
                    response.body().close();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Cohere API调用失败", e);
        }
    }

    /**
     * 重试拦截器
     * 处理临时性API调用失败
     */
    private static class RetryInterceptor implements Interceptor {
        private final int maxRetries;

        public RetryInterceptor(int maxRetries) {
            this.maxRetries = maxRetries;
        }

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            Response response = null;
            IOException exception = null;

            for (int retryCount = 0; retryCount <= maxRetries; retryCount++) {
                try {
                    response = chain.proceed(request);
                    if (response.isSuccessful()) {
                        return response;
                    }
                    response.close();
                } catch (IOException e) {
                    exception = e;
                    if (response != null) {
                        response.close();
                    }
                }

                if (retryCount == maxRetries) {
                    break;
                }

                try {
                    Thread.sleep((long) (Math.pow(2, retryCount) * 1000));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new IOException("重试被中断", e);
                }
            }

            if (exception != null) {
                throw exception;
            }

            return response;
        }
    }
}