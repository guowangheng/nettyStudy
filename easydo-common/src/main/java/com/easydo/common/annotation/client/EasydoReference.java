package com.easydo.common.annotation.client;

import org.springframework.beans.factory.annotation.Autowired;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.METHOD;

@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Autowired(required = false)
public @interface EasydoReference {

    int retry() default 0;              // 重试次数

    long timeMsec() default 100000;      // 超时时长

}
