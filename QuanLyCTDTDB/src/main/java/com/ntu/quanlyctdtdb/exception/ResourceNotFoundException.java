package com.ntu.quanlyctdtdb.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
    public ResourceNotFoundException(String resourceName, String field, Object value) {
        super(resourceName + " khong ton tai voi " + field + " = " + value);
    }
}
