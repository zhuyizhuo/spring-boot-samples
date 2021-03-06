package com.github.zhuyizhuo.jackson.sample.model;

import com.github.zhuyizhuo.jackson.sample.constants.BaseConstant;

/**
 * @author zhuo
 */
public class BaseResponse<T> {
    private String code;
    private String message;
    private T data;

    public BaseResponse() {
    }

    public BaseResponse(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public BaseResponse(String code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> BaseResponse<T> success(){
        return success(null);
    }

    public static <T> BaseResponse<T> success(T data){
        BaseResponse response = new BaseResponse(BaseConstant.SUCCESS_CODE,"成功");
        response.setData(data);
        return response;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
