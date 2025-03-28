package com.ragflow4j.server.exception;

import lombok.Getter;

/**
 * 资源未找到异常
 */
@Getter
public class ResourceNotFoundException extends RuntimeException {
    private final Object resourceId;

    public ResourceNotFoundException(String message) {
        super(message);
        this.resourceId = null;
    }

    public ResourceNotFoundException(String message, Object resourceId) {
        super(message);
        this.resourceId = resourceId;
    }
}