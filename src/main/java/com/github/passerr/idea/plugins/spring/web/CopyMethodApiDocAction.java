package com.github.passerr.idea.plugins.spring.web;

import com.github.passerr.idea.plugins.spring.web.po.ApiDocSettingPo;
import com.intellij.codeInsight.AnnotationUtil;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifier;
import com.intellij.psi.PsiModifierList;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.javadoc.PsiDocToken;
import com.intellij.psi.util.PsiTypesUtil;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.passerr.idea.plugins.spring.web.AliasType.UNKNOWN_ALIAS;

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
        ApiDocSettingPo state = ApiDocStateComponent.getInstance().getState();
        String url = url(classAnnotation, methodAnnotation);
        String httpMethod = getMethod(classAnnotation, methodAnnotation);
        // 方法参数注释缓存
        Map<String, String> comments = comments(method);
        // 路径参数列表
        List<Var> pathVariables = pathVariables(comments, method, state);
        // 查询参数列表
        List<Var> queryVariables = queryParams(comments, method, state);
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
     * 获得方法注释
     * @param method {@link PsiMethod}
     * @return {@link Map}
     */
    private static Map<String, String> comments(PsiMethod method) {
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
                                .filter(CopyMethodApiDocAction::isDocCommentData)
                                .map(e -> e.getText().trim())
                                .collect(Collectors.joining(""))
                        )
                    )
            );

        return comments;
    }

    /**
     * 获取接口路径参数
     * @param comments 方法注释
     * @param method   {@link PsiMethod}
     * @return 路径参数列表
     */
    private static List<Var> pathVariables(Map<String, String> comments, PsiMethod method, ApiDocSettingPo state) {
        return
            Arrays.stream(method.getParameterList().getParameters())
                .filter(CopyMethodApiDocAction::isValidParamType)
                .map(it -> {
                    PsiAnnotation annotation = AnnotationUtil.findAnnotation(
                        it, WebCopyConstants.PATH_VARIABLE_ANNOTATION);
                    String type = it.getType().getCanonicalText();
                    String alias = state.alias(type);
                    // 必须要@PathVariable注解存在且是有效别名
                    if (Objects.nonNull(annotation) && !UNKNOWN_ALIAS.equals(alias)) {
                        return
                            new Var(
                                Optional.ofNullable(PsiAnnotationMemberValueUtil.value(annotation, "value"))
                                    .map(String::valueOf)
                                    .orElseGet(it::getText),
                                type,
                                alias,
                                comments.getOrDefault(it.getText(), it.getText())
                            );
                    }

                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * 查询参数注解
     * @param comments 方法注释
     * @param method   {@link PsiMethod}
     * @param state    配置状态
     * @return {@link List}
     */
    private static List<Var> queryParams(Map<String, String> comments, PsiMethod method, ApiDocSettingPo state) {
        return
            Arrays.stream(method.getParameterList().getParameters())
                .filter(it ->
                    // 排除接口类型参数、忽略类型、带忽略类型注解的参数
                    isValidParamType(
                        it,
                        state.getQueryParamIgnoreTypes(),
                        state.getQueryParamIgnoreAnnotations()
                    )
                )
                .flatMap(it -> {
                    // 查询参数注解
                    PsiAnnotation annotation = AnnotationUtil.findAnnotation(
                        it, WebCopyConstants.QUERY_PARAM_ANNOTATION);
                    String type = it.getType().getCanonicalText();
                    String alias = state.alias(type);
                    // 对象类型
                    if (UNKNOWN_ALIAS.equals(alias)) {
                        PsiClass clazz = PsiTypesUtil.getPsiClass(it.getType());
                        // 未知类型
                        if (Objects.isNull(clazz)) {
                            return Stream.empty();
                        }

                        return
                            Arrays.stream(clazz.getAllFields())
                                .filter(CopyMethodApiDocAction::isValidFiled)
                                // 查询参数只遍历一层 且忽略掉这层的未知类型
                                .filter(f -> !UNKNOWN_ALIAS.equals(state.alias(f.getType().getCanonicalText())))
                                .map(f ->
                                    new Var(
                                        f.getName(),
                                        f.getType().getCanonicalText(),
                                        state.alias(f.getType().getCanonicalText()),
                                        Optional.ofNullable(f.getDocComment())
                                            .map(PsiDocComment::getDescriptionElements)
                                            .map(els ->
                                                Arrays.stream(els)
                                                    .filter(e -> e instanceof PsiDocToken)
                                                    .map(PsiDocToken.class::cast)
                                                    .filter(CopyMethodApiDocAction::isDocCommentData)
                                                    .map(e -> e.getText().trim())
                                                    .collect(Collectors.joining(""))
                                            )
                                            .orElseGet(f::getName)
                                    )
                                );
                    } else {
                        // 基础类型
                        return
                            Stream.of(
                                new Var(
                                    Optional.ofNullable(PsiAnnotationMemberValueUtil.value(annotation, "value"))
                                        .map(String::valueOf)
                                        .orElseGet(it::getText),
                                    type,
                                    alias,
                                    comments.getOrDefault(it.getText(), it.getText())
                                )
                            );
                    }
                })
                .collect(Collectors.toList());
    }

    /**
     * 是否是合法的参数类型
     * 非接口类型且满足排除类型
     * @param parameter {@link PsiParameter}
     * @return true/false
     */
    private static boolean isValidParamType(PsiParameter parameter) {
        return isValidParamType(parameter, new ArrayList<>(), new ArrayList<>());
    }

    /**
     * 是否是合法的参数类型
     * @param parameter          {@link PsiParameter}
     * @param excludeTypes       排除类型
     * @param excludeAnnotations 排除注解
     * @return true/false
     */
    private static boolean isValidParamType(PsiParameter parameter, List<String> excludeTypes,
                                            List<String> excludeAnnotations) {
        PsiClass clazz = PsiTypesUtil.getPsiClass(parameter.getType());

        return
            Objects.nonNull(clazz)
                && !clazz.isInterface()
                && !excludeTypes.contains(clazz.getQualifiedName())
                && AnnotationUtil.findAnnotations(parameter, excludeAnnotations).length == 0;
    }

    /**
     * 是否是合法字段
     * @param field {@link PsiField}
     * @return 非static、transient字段
     */
    private static boolean isValidFiled(PsiField field) {
        PsiModifierList modifierList = field.getModifierList();
        if (Objects.isNull(modifierList)) {
            return true;
        }

        return !modifierList.hasExplicitModifier(PsiModifier.STATIC)
            && !modifierList.hasExplicitModifier(PsiModifier.TRANSIENT);
    }

    /**
     * 是否是注释文本部分
     * @param token {@link PsiDocToken}
     * @return true/false
     */
    private static boolean isDocCommentData(PsiDocToken token) {
        return "DOC_COMMENT_DATA".equals(token.getTokenType().toString());
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
