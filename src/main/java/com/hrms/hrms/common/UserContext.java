package com.hrms.hrms.common;

// 用 ThreadLocal 存储当前请求的用户信息
// ThreadLocal 保证每个请求线程有自己独立的数据，互不干扰
public class UserContext {

    private static final ThreadLocal<String> USERNAME = new ThreadLocal<>();
    private static final ThreadLocal<String> ROLE     = new ThreadLocal<>();

    public static void setUsername(String username) { USERNAME.set(username); }
    public static void setRole(String role)         { ROLE.set(role); }

    public static String getUsername() { return USERNAME.get(); }
    public static String getRole()     { return ROLE.get(); }

    // 请求结束后必须清除，防止内存泄漏
    public static void clear() {
        USERNAME.remove();
        ROLE.remove();
    }
}