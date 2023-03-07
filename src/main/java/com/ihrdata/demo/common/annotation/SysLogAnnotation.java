package com.ihrdata.demo.common.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 系统日志注解
 *
 * @author wangwz
 * @date 2020/11/25
 */
@Target(METHOD)
@Retention(RUNTIME)
@Documented
public @interface SysLogAnnotation {
    // 操作信息
    String value() default "";

    // 是否记录请求参数信息
    boolean flag() default true;

    // 操作类别
    String type() default "";
}
