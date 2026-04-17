package com.ntu.quanlyctdtdb.exception;

/**
 * Exception cho vi pham business rule.
 * VD: GV khong trong doi ngu HP, workflow state khong hop le, UNIQUE constraint vi pham
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
