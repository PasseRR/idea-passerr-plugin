package com.github.passerr.idea.plugins.spring.web;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * web常量
 * @author xiehai
 * @date 2021/06/30 19:07
 * @Copyright(c) tellyes tech. inc. co.,ltd
 */
public abstract class WebCopyConstants {
    /**
     * 均不支持的类型
     */
    public static final List<String> ALL_IGNORE_TYPES = Arrays.asList("java.lang.Object", "java.util.Map");
    /**
     * 查询参数忽略类型
     */
    public static final List<String> QUERY_PARAM_IGNORE_TYPES = new ArrayList<>(Arrays.asList(
        "org.springframework.ui.Model",
        "javax.servlet.http.HttpServletRequest",
        "javax.servlet.http.HttpServletResponse",
        "javax.servlet.http.HttpSession"
    ));

    static {
        QUERY_PARAM_IGNORE_TYPES.addAll(ALL_IGNORE_TYPES);
    }

    /**
     * 路径参数注解
     */
    public static final String PATH_VARIABLE_ANNOTATION = "org.springframework.web.bind.annotation.PathVariable";
    /**
     * 查询参数注解
     */
    public static final List<String> QUERY_PARAM_ANNOTATIONS = new ArrayList<>(Arrays.asList(
        "org.springframework.web.bind.annotation.RequestParam",
        "org.springframework.web.bind.annotation.RequestAttribute"
    ));
    /**
     * 查询参数忽略注解
     */
    public static final List<String> QUERY_PARAM_IGNORE_ANNOTATIONS = Arrays.asList(
        "org.springframework.web.bind.annotation.CookieValue",
        "org.springframework.web.bind.annotation.RequestHeader",
        "org.springframework.web.bind.annotation.ResponseBody",
        "org.springframework.web.bind.annotation.SessionAttribute",
        "org.springframework.web.bind.annotation.SessionAttributes"
    );

    static {
        QUERY_PARAM_IGNORE_ANNOTATIONS.add(PATH_VARIABLE_ANNOTATION);
    }

    /**
     * 默认数据类型别名
     */
    public static final String DEFAULT_ALIAS = "object";
    /**
     * 布尔类型
     */
    public static final String BOOLEAN_ALIAS = "boolean";
    /**
     * 整型
     */
    public static final String INT_ALIAS = "int";
    /**
     * 浮点型
     */
    public static final String FLOAT_ALIAS = "float";
    /**
     * 字符串
     */
    public static final String STRING_ALIAS = "string";
    /**
     * 所有类型别名
     */
    public static final List<String> TYPE_ALIASES = Arrays.asList(
        BOOLEAN_ALIAS,
        INT_ALIAS,
        FLOAT_ALIAS,
        STRING_ALIAS,
        DEFAULT_ALIAS
    );
    /**
     * 默认类型映射
     */
    public static final Map<String, String> DEFAULT_ALIAS_MAPPINGS = new HashMap<>();

    static {
        Arrays.asList("boolean", "java.lang.Boolean").forEach(it -> DEFAULT_ALIAS_MAPPINGS.put(it, BOOLEAN_ALIAS));

        Arrays.asList(
            "byte", "java.lang.Byte",
            "short", "java.lang.Short",
            "char", "java.lang.Character",
            "int", "java.lang.Integer"
        ).forEach(it -> DEFAULT_ALIAS_MAPPINGS.put(it, INT_ALIAS));

        Arrays.asList(
            "float", "java.lang.Float",
            "double", "java.lang.Double"
        ).forEach(it -> DEFAULT_ALIAS_MAPPINGS.put(it, FLOAT_ALIAS));

        Arrays.asList(
            "long",
            "java.lang.Long",
            "java.lang.String",
            "java.math.BigInteger",
            "java.math.BigDecimal",
            "java.util.Date",
            "java.util.LocalDate",
            "java.util.LocalTime",
            "java.util.LocalDateTime"
        ).forEach(it -> DEFAULT_ALIAS_MAPPINGS.put(it, STRING_ALIAS));
    }

    /**
     * 默认文档模版
     */
    public static final String DEFAULT_TEMPLATE = "Hello World";
}
