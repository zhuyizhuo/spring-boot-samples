package com.github.zhuyizhuo.ai.demo.exception;

/**
 * 业务逻辑异常类
 * 用于表示业务操作中的错误
 */
public class BusinessException extends RuntimeException {

    private String errorCode;
    private String errorMessage;

    public BusinessException(String message) {
        super(message);
        this.errorMessage = message;
    }

    public BusinessException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.errorMessage = message;
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        this.errorMessage = message;
    }

    public BusinessException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.errorMessage = message;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}