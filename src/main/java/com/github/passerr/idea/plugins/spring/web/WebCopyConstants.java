package com.github.passerr.idea.plugins.spring.web;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * web常量
 * @author xiehai
 * @date 2021/06/30 19:07
 * @Copyright(c) tellyes tech. inc. co.,ltd
 */
public abstract class WebCopyConstants {
    /**
     * 路径参数注解
     */
    public static final String PATH_VARIABLE_ANNOTATION = "org.springframework.web.bind.annotation.PathVariable";
    /**
     * 查询参数注解
     */
    public static final String QUERY_PARAM_ANNOTATION = "org.springframework.web.bind.annotation.RequestParam";
    /**
     * 报文体参数注解
     */
    public static final String BODY_ANNOTATION = "org.springframework.web.bind.annotation.RequestBody";
    /**
     * 查询参数忽略类型
     */
    public static final List<String> QUERY_PARAM_IGNORE_TYPES = Collections.unmodifiableList(
        Arrays.asList(
            "org.springframework.ui.Model",
            "javax.servlet.http.HttpServletRequest",
            "javax.servlet.http.HttpServletResponse",
            "javax.servlet.http.HttpSession",
            "java.util.Map"
        )
    );
    /**
     * 查询参数忽略注解
     */
    public static final List<String> QUERY_PARAM_IGNORE_ANNOTATIONS = Collections.unmodifiableList(
        Arrays.asList(
            "org.springframework.web.bind.annotation.CookieValue",
            "org.springframework.web.bind.annotation.RequestHeader",
            BODY_ANNOTATION,
            "org.springframework.web.bind.annotation.RequestAttribute",
            "org.springframework.web.bind.annotation.SessionAttribute",
            "org.springframework.web.bind.annotation.SessionAttributes",
            PATH_VARIABLE_ANNOTATION
        )
    );
    /**
     * json字段忽略注解
     */
    public static final List<String> FIELD_IGNORE_ANNOTATIONS = Collections.unmodifiableList(
        Arrays.asList(
            "com.fasterxml.jackson.annotation.JsonIgnore",
            "com.google.gson.annotations.Expose"
        )
    );
}
