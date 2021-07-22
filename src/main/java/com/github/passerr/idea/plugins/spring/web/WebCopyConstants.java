package com.github.passerr.idea.plugins.spring.web;

import com.github.passerr.idea.plugins.spring.web.po.ApiDocObjectSerialPo;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * web常量
 * @author xiehai
 * @date 2021/06/30 19:07
 * @Copyright(c) tellyes tech. inc. co.,ltd
 */
public interface WebCopyConstants {
    /**
     * 路径参数注解
     */
    String PATH_VARIABLE_ANNOTATION = "org.springframework.web.bind.annotation.PathVariable";
    /**
     * 查询参数注解
     */
    String QUERY_PARAM_ANNOTATION = "org.springframework.web.bind.annotation.RequestParam";
    /**
     * 报文体参数注解
     */
    String BODY_ANNOTATION = "org.springframework.web.bind.annotation.RequestBody";
    /**
     * 查询参数忽略类型
     */
    List<String> QUERY_PARAM_IGNORE_TYPES = Collections.unmodifiableList(
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
    List<String> QUERY_PARAM_IGNORE_ANNOTATIONS = Collections.unmodifiableList(
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
    List<String> FIELD_IGNORE_ANNOTATIONS = Collections.unmodifiableList(
        Arrays.asList(
            "com.fasterxml.jackson.annotation.JsonIgnore",
            "com.google.gson.annotations.Expose"
        )
    );
    /**
     * 原生类型序列化
     */
    List<ApiDocObjectSerialPo> PRIMITIVE_SERIALS = Collections.unmodifiableList(
        Arrays.asList(
            new ApiDocObjectSerialPo(boolean.class.getName(), AliasType.BOOLEAN.getType(), "true"),
            new ApiDocObjectSerialPo(byte.class.getName(), AliasType.INT.getType(), "128"),
            new ApiDocObjectSerialPo(short.class.getName(), AliasType.INT.getType(), "256"),
            new ApiDocObjectSerialPo(char.class.getName(), AliasType.INT.getType(), "512"),
            new ApiDocObjectSerialPo(int.class.getName(), AliasType.INT.getType(), "1024"),
            new ApiDocObjectSerialPo(float.class.getName(), AliasType.FLOAT.getType(), "102.4"),
            new ApiDocObjectSerialPo(double.class.getName(), AliasType.FLOAT.getType(), "204.8"),
            new ApiDocObjectSerialPo(long.class.getName(), AliasType.STRING.getType(), "2048")
        )
    );
    /**
     * 包装类型序列化
     */
    List<ApiDocObjectSerialPo> WRAPPED_SERIALS = Collections.unmodifiableList(
        Arrays.asList(
            new ApiDocObjectSerialPo(Boolean.class.getSimpleName(), AliasType.BOOLEAN.getType(), "true"),
            new ApiDocObjectSerialPo(Byte.class.getSimpleName(), AliasType.INT.getType(), "128"),
            new ApiDocObjectSerialPo(Short.class.getSimpleName(), AliasType.INT.getType(), "256"),
            new ApiDocObjectSerialPo(Character.class.getSimpleName(), AliasType.INT.getType(), "512"),
            new ApiDocObjectSerialPo(Integer.class.getSimpleName(), AliasType.INT.getType(), "1024"),
            new ApiDocObjectSerialPo(Float.class.getSimpleName(), AliasType.FLOAT.getType(), "102.4"),
            new ApiDocObjectSerialPo(Double.class.getSimpleName(), AliasType.FLOAT.getType(), "204.8"),
            new ApiDocObjectSerialPo(Long.class.getSimpleName(), AliasType.STRING.getType(), "2048"),
            new ApiDocObjectSerialPo(BigDecimal.class.getSimpleName(), AliasType.FLOAT.getType(), "409.2"),
            new ApiDocObjectSerialPo(BigInteger.class.getSimpleName(), AliasType.STRING.getType(), "4092"),
            new ApiDocObjectSerialPo(String.class.getSimpleName(), AliasType.STRING.getType(), "string"),
            new ApiDocObjectSerialPo(
                LocalTime.class.getSimpleName(),
                AliasType.STRING.getType(),
                LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
            ),
            new ApiDocObjectSerialPo(
                LocalDate.class.getSimpleName(),
                AliasType.STRING.getType(),
                LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            ),
            new ApiDocObjectSerialPo(
                Date.class.getSimpleName(),
                AliasType.STRING.getType(),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            ),
            new ApiDocObjectSerialPo(
                LocalDateTime.class.getSimpleName(),
                AliasType.STRING.getType(),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            )
        )
    );
}
