package com.hrms.hrms.config;

import com.hrms.hrms.common.UserInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new UserInterceptor())
                .addPathPatterns("/api/**")         // 拦截所有api请求
                .excludePathPatterns("/api/auth/**"); // 登录相关不拦截
    }
}