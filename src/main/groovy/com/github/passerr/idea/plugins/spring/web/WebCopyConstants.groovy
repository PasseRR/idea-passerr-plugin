package com.github.passerr.idea.plugins.spring.web

/**
 * 复制相关常量
 * @date 2021/06/28 16:41
 * @Copyright (c) wisewe co.,ltd
 * @author xiehai
 */
class WebCopyConstants {
    /**
     * 均不支持的类型
     */
    protected static final Set<String> ALL_IGNORE_TYPES = [
        "java.lang.Object",
        "java.util.Map"
    ]
    /**
     * 查询参数忽略类型
     */
    protected static final Set<String> QUERY_PARAM_IGNORE_TYPES = [
        "org.springframework.ui.Model",
        "javax.servlet.http.HttpServletRequest",
        "javax.servlet.http.HttpServletResponse",
        "javax.servlet.http.HttpSession",

    ]
    static {
        QUERY_PARAM_IGNORE_TYPES.addAll(ALL_IGNORE_TYPES)
    }

    /**
     * 路径参数注解
     */
    protected static final String PATH_VARIABLE_ANNOTATION = "org.springframework.web.bind.annotation.PathVariable"
    /**
     * 查询参数注解
     */
    protected static final Set<String> QUERY_PARAM_ANNOTATIONS = [
        "org.springframework.web.bind.annotation.RequestParam",
        "org.springframework.web.bind.annotation.RequestAttribute"
    ]
    /**
     * 查询参数忽略注解
     */
    protected static final Set<String> QUERY_PARAM_IGNORE_ANNOTATIONS = [
        "org.springframework.web.bind.annotation.CookieValue",
        "org.springframework.web.bind.annotation.RequestHeader",
        "org.springframework.web.bind.annotation.ResponseBody",
        "org.springframework.web.bind.annotation.SessionAttribute",
        "org.springframework.web.bind.annotation.SessionAttributes"
    ]
    static {
        QUERY_PARAM_IGNORE_ANNOTATIONS.add(PATH_VARIABLE_ANNOTATION)
    }

    /**
     * 默认数据类型别名
     */
    protected static final String DEFAULT_ALIAS = "object"
    /**
     * 布尔类型
     */
    protected static final String BOOLEAN_ALIAS = "boolean"
    /**
     * 整型
     */
    protected static final String INT_ALIAS = "int"
    /**
     * 浮点型
     */
    protected static final String FLOAT_ALIAS = "float"
    /**
     * 字符串
     */
    protected static final String STRING_ALIAS = "string"
    /**
     * 所有类型别名
     */
    protected static final List<String> TYPE_ALIASES = [
        BOOLEAN_ALIAS,
        INT_ALIAS,
        FLOAT_ALIAS,
        STRING_ALIAS,
        DEFAULT_ALIAS
    ]
    /**
     * 默认类型映射
     */
    protected static final Map<String, String> DEFAULT_ALIAS_MAPPINGS = [:]
    static {
        ["boolean", "java.lang.Boolean"].each { it -> DEFAULT_ALIAS_MAPPINGS.put(it, BOOLEAN_ALIAS) }

        [
            "byte", "java.lang.Byte",
            "short", "java.lang.Short",
            "char", "java.lang.Character",
            "int", "java.lang.Integer"
        ].each { it -> DEFAULT_ALIAS_MAPPINGS.put(it, INT_ALIAS) }

        [
            "float", "java.lang.Float",
            "double", "java.lang.Double"
        ].each { it -> DEFAULT_ALIAS_MAPPINGS.put(it, FLOAT_ALIAS) }

        [
            "long",
            "java.lang.Long",
            "java.lang.String",
            "java.math.BigInteger",
            "java.math.BigDecimal",
            "java.util.Date",
            "java.util.LocalDate",
            "java.util.LocalTime",
            "java.util.LocalDateTime"
        ].each { it -> DEFAULT_ALIAS_MAPPINGS.put(it, STRING_ALIAS) }
    }

    private WebCopyConstants() {

    }
}
