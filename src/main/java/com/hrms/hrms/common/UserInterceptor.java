package com.hrms.hrms.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;

public class UserInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        // 从请求头获取用户信息（前端登录后每次请求都带上）
        String userJson = request.getHeader("X-User-Info");
        if (userJson != null && !userJson.isEmpty()) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                Map<?, ?> map = mapper.readValue(userJson, Map.class);
                UserContext.setUsername(String.valueOf(map.get("username")));
                UserContext.setRole(String.valueOf(map.get("role")));
            } catch (Exception ignored) {}
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response,
                                Object handler, Exception ex) {
        // 请求结束后清除 ThreadLocal
        UserContext.clear();
    }
}