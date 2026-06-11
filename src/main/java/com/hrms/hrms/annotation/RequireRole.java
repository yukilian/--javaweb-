package com.hrms.hrms.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequireRole {
    // 允许访问的角色，默认管理员
    String[] value() default {"管理员"};
}