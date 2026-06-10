package com.hrms.hrms.common;

import lombok.Data;

@Data
public class Result<T> {

    private Integer code;     // 状态码：200成功，500失败
    private String message;   // 提示信息
    private T data;           // 返回的数据

    // 成功，带数据
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage("success");
        result.setData(data);
        return result;
    }

    // 成功，不带数据
    public static <T> Result<T> success() {
        return success(null);
    }

    // 失败
    public static <T> Result<T> error(String message) {
        Result<T> result = new Result<>();
        result.setCode(500);
        result.setMessage(message);
        result.setData(null);
        return result;
    }
}