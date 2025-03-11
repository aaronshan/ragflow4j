package com.ragflow4j.common.llm;

/**
 * LLM调用异常类
 * 用于封装模型调用过程中的各种异常
 */
public class LLMException extends RuntimeException {
    private final ErrorCode errorCode;
    private final String requestId;

    public LLMException(String message, ErrorCode errorCode) {
        this(message, errorCode, null, null);
    }

    public LLMException(String message, ErrorCode errorCode, String requestId) {
        this(message, errorCode, requestId, null);
    }

    public LLMException(String message, ErrorCode errorCode, String requestId, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.requestId = requestId;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public String getRequestId() {
        return requestId;
    }

    public enum ErrorCode {
        API_ERROR(1001, "API调用错误"),
        RATE_LIMIT_EXCEEDED(1002, "超出请求限制"),
        INVALID_API_KEY(1003, "无效的API密钥"),
        MODEL_OVERLOADED(1004, "模型负载过高"),
        CONTEXT_LENGTH_EXCEEDED(1005, "超出上下文长度限制"),
        INVALID_REQUEST(1006, "无效的请求参数"),
        TIMEOUT(1007, "请求超时"),
        CONNECTION_ERROR(1008, "网络连接错误"),
        UNKNOWN_ERROR(9999, "未知错误");

        private final int code;
        private final String description;

        ErrorCode(int code, String description) {
            this.code = code;
            this.description = description;
        }

        public int getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }
    }
}