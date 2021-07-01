package com.github.passerr.idea.plugins.spring.web;

import com.intellij.codeInsight.AnnotationUtil;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.javadoc.PsiDocToken;
import lombok.AllArgsConstructor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * web方法接口文档复制动作
 * @author xiehai
 * @date 2021/07/01 15:21
 * @Copyright(c) tellyes tech. inc. co.,ltd
 */
public class CopyMethodApiDocAction extends BaseWebCopyAction {
    @Override
    public void actionPerformed(AnActionEvent e) {
        PsiMethod method = method(e);
        PsiAnnotation classAnnotation = classAnnotation(method);
        PsiAnnotation methodAnnotation = methodAnnotation(method);
        String url = url(classAnnotation, methodAnnotation);
        String httpMethod = getMethod(classAnnotation, methodAnnotation);
        List<Var> pathVariables = pathVariables(method);
        // 路径参数列表
        // 查询参数列表
        // body
        // 请求示例
        // 应答示例
    }

    /**
     * 获取http方法
     * @param classAnnotation  类上的注解
     * @param methodAnnotation 方法上的注解
     * @return http方法类型
     */
    private static String getMethod(PsiAnnotation classAnnotation, PsiAnnotation methodAnnotation) {
        if (!REQUEST_MAPPING.equals(methodAnnotation.getQualifiedName())) {
            return MAPPINGS.get(methodAnnotation.getQualifiedName());
        }

        Object methodOnMethod = PsiAnnotationMemberValueUtil.getArrayFirstValue(methodAnnotation, "method");
        if (Objects.nonNull(methodOnMethod)) {
            return String.valueOf(methodOnMethod);
        }

        Object methodOnClass = PsiAnnotationMemberValueUtil.getArrayFirstValue(classAnnotation, "method");
        if (Objects.nonNull(methodOnClass)) {
            return String.valueOf(methodOnClass);
        }

        return "UNKNOWN";
    }

    /**
     * 获取接口路径参数
     * @param method {@link PsiMethod}
     * @return 路径参数列表
     */
    private static List<Var> pathVariables(PsiMethod method) {
        Map<String, String> comments = new HashMap<>(4);
        Optional.ofNullable(method.getDocComment())
            .map(PsiDocComment::getTags)
            .filter(it -> it.length > 0)
            .map(Arrays::asList)
            // 方法注释tag列表
            .ifPresent(tags ->
                tags.stream()
                    .filter(Objects::nonNull)
                    .filter(it -> "param".equals(it.getName()))
                    .forEach(it ->
                        comments.put(
                            it.getName(),
                            Arrays.stream(it.getDataElements())
                                .filter(e -> e instanceof PsiDocToken)
                                .map(PsiDocToken.class::cast)
                                .filter(e -> "DOC_COMMENT_DATA".equals(e.getTokenType().toString()))
                                .map(e -> e.getText().trim())
                                .collect(Collectors.joining(""))
                        )
                    )
            );

        return
            Arrays.stream(method.getParameterList().getParameters())
                .map(it -> {
                    PsiAnnotation annotation = AnnotationUtil.findAnnotation(
                        it, WebCopyConstants.PATH_VARIABLE_ANNOTATION);
                    if (Objects.nonNull(annotation)) {
                        String type = it.getType().getCanonicalText();
                        return
                            new Var(
                                Optional.ofNullable(PsiAnnotationMemberValueUtil.value(annotation, "value"))
                                    .map(String::valueOf)
                                    .orElseGet(it::getText),
                                type,
                                ApiDocStateComponent.getInstance().alias(type),
                                comments.getOrDefault(it.getText(), it.getText())
                            );
                    }

                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private static List<Var> queryParams(PsiMethod method) {
        return null;
    }

    /**
     * 参数实体
     */
    @AllArgsConstructor
    private static class Var {
        /**
         * 参数名
         */
        String name;
        /**
         * 类型
         */
        String type;
        /**
         * 类型别名
         */
        String alias;
        /**
         * 参数描述
         */
        String desc;
    }
}
