package com.github.zhuyizhuo.ai.demo.exception;

/**
 * API异常类
 * 用于表示API调用过程中的错误
 */
public class ApiException extends RuntimeException {

    private String errorCode;
    private int statusCode;

    public ApiException(String message) {
        super(message);
        this.errorCode = "API_ERROR";
        this.statusCode = 500;
    }

    public ApiException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.statusCode = 400;
    }

    public ApiException(String errorCode, String message, int statusCode) {
        super(message);
        this.errorCode = errorCode;
        this.statusCode = statusCode;
    }

    public ApiException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "API_ERROR";
        this.statusCode = 500;
    }

    public ApiException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.statusCode = 400;
    }

    public ApiException(String errorCode, String message, int statusCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.statusCode = statusCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
}