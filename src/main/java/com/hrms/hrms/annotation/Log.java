package com.hrms.hrms.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Log {
    // 操作描述，如"新增部门"
    String action() default "";
}