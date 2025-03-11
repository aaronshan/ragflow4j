package com.ragflow4j.common.llm;

import java.time.Duration;

/**
 * LLM模型配置类
 * 包含模型调用的基本配置信息
 */
public class LLMConfig {
    private String apiKey;
    private String apiHost;
    private String modelName;
    private Duration timeout;
    private int maxRetries;
    private Duration retryDelay;
    private int maxTokens;
    private double temperature;
    private boolean streamResponse;
    private int maxConcurrentRequests;

    private LLMConfig(Builder builder) {
        this.apiKey = builder.apiKey;
        this.apiHost = builder.apiHost;
        this.modelName = builder.modelName;
        this.timeout = builder.timeout;
        this.maxRetries = builder.maxRetries;
        this.retryDelay = builder.retryDelay;
        this.maxTokens = builder.maxTokens;
        this.temperature = builder.temperature;
        this.streamResponse = builder.streamResponse;
        this.maxConcurrentRequests = builder.maxConcurrentRequests;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getApiHost() {
        return apiHost;
    }

    public String getModelName() {
        return modelName;
    }

    public Duration getTimeout() {
        return timeout;
    }

    public int getMaxRetries() {
        return maxRetries;
    }

    public Duration getRetryDelay() {
        return retryDelay;
    }

    public int getMaxTokens() {
        return maxTokens;
    }

    public double getTemperature() {
        return temperature;
    }

    public boolean isStreamResponse() {
        return streamResponse;
    }

    public int getMaxConcurrentRequests() {
        return maxConcurrentRequests;
    }

    public static class Builder {
        private String apiKey;
        private String apiHost = "https://api.openai.com/v1";
        private String modelName = "gpt-3.5-turbo";
        private Duration timeout = Duration.ofSeconds(30);
        private int maxRetries = 3;
        private Duration retryDelay = Duration.ofSeconds(1);
        private int maxTokens = 2048;
        private double temperature = 0.7;
        private boolean streamResponse = false;
        private int maxConcurrentRequests = 10;

        public Builder apiKey(String apiKey) {
            this.apiKey = apiKey;
            return this;
        }

        public Builder apiHost(String apiHost) {
            this.apiHost = apiHost;
            return this;
        }

        public Builder modelName(String modelName) {
            this.modelName = modelName;
            return this;
        }

        public Builder timeout(Duration timeout) {
            this.timeout = timeout;
            return this;
        }

        public Builder maxRetries(int maxRetries) {
            this.maxRetries = maxRetries;
            return this;
        }

        public Builder retryDelay(Duration retryDelay) {
            this.retryDelay = retryDelay;
            return this;
        }

        public Builder maxTokens(int maxTokens) {
            this.maxTokens = maxTokens;
            return this;
        }

        public Builder temperature(double temperature) {
            this.temperature = temperature;
            return this;
        }

        public Builder streamResponse(boolean streamResponse) {
            this.streamResponse = streamResponse;
            return this;
        }

        public Builder maxConcurrentRequests(int maxConcurrentRequests) {
            this.maxConcurrentRequests = maxConcurrentRequests;
            return this;
        }

        public LLMConfig build() {
            if (apiKey == null || apiKey.trim().isEmpty()) {
                throw new IllegalStateException("API key must be provided");
            }
            return new LLMConfig(this);
        }
    }
}