package com.ntu.quanlyctdtdb.exception;

/**
 * Nem khi tim khong thay entity theo ID.
 * Duoc xu ly trong GlobalExceptionHandler -> HTTP 404.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String entityName, String id) {
        super("Khong tim thay " + entityName + " voi ma: " + id);
    }
}
