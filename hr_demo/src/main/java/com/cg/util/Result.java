package com.cg.util;

/**
 * 通用响应结果包装类
 * @param <T> 数据类型泛型
 */
public class Result<T> {
    private Integer code; // 状态码
    private String message; // 提示信息
    private T data; // 数据

    // 成功状态码
    private static final Integer SUCCESS_CODE = 200;
    // 错误状态码
    private static final Integer ERROR_CODE = 500;
    // 默认成功信息
    private static final String SUCCESS_MESSAGE = "操作成功";
    // 默认错误信息
    private static final String ERROR_MESSAGE = "操作失败";

    // 私有构造方法
    private Result(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 成功返回结果
     */
    public static <T> Result<T> success() {
        return new Result<>(SUCCESS_CODE, SUCCESS_MESSAGE, null);
    }

    /**
     * 成功返回结果
     * @param data 获取的数据
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(SUCCESS_CODE, SUCCESS_MESSAGE, data);
    }

    /**
     * 成功返回结果
     * @param message 提示信息
     * @param data 获取的数据
     */
    public static <T> Result<T> success(String message, T data) {
        return new Result<>(SUCCESS_CODE, message, data);
    }

    /**
     * 失败返回结果
     */
    public static <T> Result<T> error() {
        return new Result<>(ERROR_CODE, ERROR_MESSAGE, null);
    }

    /**
     * 失败返回结果
     * @param message 提示信息
     */
    public static <T> Result<T> error(String message) {
        return new Result<>(ERROR_CODE, message, null);
    }

    /**
     * 失败返回结果
     * @param code 状态码
     * @param message 提示信息
     */
    public static <T> Result<T> error(Integer code, String message) {
        return new Result<>(code, message, null);
    }

    // Getter和Setter方法
    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
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
