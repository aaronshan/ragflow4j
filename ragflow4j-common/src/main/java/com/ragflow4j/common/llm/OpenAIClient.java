package com.ragflow4j.common.llm;

import com.plexpt.chatgpt.ChatGPT;
import com.plexpt.chatgpt.entity.chat.ChatCompletion;
import com.plexpt.chatgpt.entity.chat.ChatCompletionResponse;
import com.plexpt.chatgpt.entity.chat.Message;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@Slf4j
public class OpenAIClient implements LLMClient {
    private LLMConfig config;
    private final ExecutorService executorService;
    private final RateLimiter rateLimiter;

    public OpenAIClient(LLMConfig config) {
        this.config = config;
        this.executorService = Executors.newFixedThreadPool(config.getMaxConcurrentRequests());
        this.rateLimiter = new RateLimiter(60, TimeUnit.MINUTES);
    }

    @Override
    public String complete(String prompt, Map<String, Object> options) {
        validateRequest(prompt);
        try {
            rateLimiter.acquire();
            return executeRequest(prompt, options);
        } catch (Exception e) {
            handleException(e);
            return null;
        }
    }

    @Override
    public CompletableFuture<String> completeAsync(String prompt, Map<String, Object> options) {
        validateRequest(prompt);
        return CompletableFuture.supplyAsync(() -> {
            try {
                rateLimiter.acquire();
                return executeRequest(prompt, options);
            } catch (Exception e) {
                handleException(e);
                return null;
            }
        }, executorService);
    }

    @Override
    public LLMConfig getConfig() {
        return config;
    }

    @Override
    public void updateConfig(LLMConfig config) {
        this.config = config;
    }

    private void validateRequest(String prompt) {
        if (prompt == null || prompt.trim().isEmpty()) {
            throw new LLMException("Prompt cannot be empty", LLMException.ErrorCode.INVALID_REQUEST);
        }
    }

    private String executeRequest(String prompt, Map<String, Object> options) {
        ChatGPT chatGPT = ChatGPT.builder()
                .apiKey(config.getApiKey())
                .apiHost(config.getApiHost())
                .timeout(config.getTimeout().getSeconds())
                .build();

        try {
            List<Message> messages = new ArrayList<>();
            messages.add(Message.of(prompt));

            ChatCompletion chatCompletion = ChatCompletion.builder()
                    .messages(messages)
                    .model(config.getModelName())
                    .temperature(options.containsKey("temperature") ?
                            ((Number) options.get("temperature")).doubleValue() : config.getTemperature())
                    .maxTokens(options.containsKey("maxTokens") ?
                            ((Number) options.get("maxTokens")).intValue() : config.getMaxTokens())
                    .build();

            int maxRetries = config.getMaxRetries();
            int retryCount = 0;
            Duration retryDelay = config.getRetryDelay();

            while (retryCount < maxRetries) {
                try {
                    ChatCompletionResponse response = chatGPT.chatCompletion(chatCompletion);
                    return response.getChoices().get(0).getMessage().getContent();
                } catch (Exception e) {
                    retryCount++;
                    if (retryCount >= maxRetries) {
                        if (e instanceof TimeoutException) {
                            throw new LLMException("Request timed out",
                                    LLMException.ErrorCode.TIMEOUT, null, e);
                        }
                        throw new LLMException("Failed to complete request after " + maxRetries + " retries",
                                LLMException.ErrorCode.API_ERROR, null, e);
                    }
                    try {
                        Thread.sleep(retryDelay.toMillis() * retryCount);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new LLMException("Request interrupted",
                                LLMException.ErrorCode.API_ERROR, null, ie);
                    }
                }
            }
            throw new LLMException("Unexpected error in request execution",
                    LLMException.ErrorCode.API_ERROR);
        } finally {
            // ChatGPT实例不需要显式关闭
        }
    }

    private void handleException(Exception e) {
        if (e instanceof LLMException) {
            throw (LLMException) e;
        }
        throw new LLMException("Failed to complete request", LLMException.ErrorCode.API_ERROR, null, e);
    }

    private static class RateLimiter {
        private final long rateLimit;
        private final TimeUnit timeUnit;
        private long lastRequestTime;

        public RateLimiter(long rateLimit, TimeUnit timeUnit) {
            this.rateLimit = rateLimit;
            this.timeUnit = timeUnit;
            this.lastRequestTime = System.currentTimeMillis();
        }

        public synchronized void acquire() throws InterruptedException {
            long currentTime = System.currentTimeMillis();
            long minInterval = timeUnit.toMillis(1) / rateLimit;
            long waitTime = minInterval - (currentTime - lastRequestTime);

            if (waitTime > 0) {
                Thread.sleep(waitTime);
            }

            lastRequestTime = System.currentTimeMillis();
        }
    }
}