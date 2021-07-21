package com.github.passerr.idea.plugins.spring.web;

import com.github.passerr.idea.plugins.spring.web.json5.Json5Generator;
import com.github.passerr.idea.plugins.spring.web.po.ApiDocSettingPo;
import com.intellij.codeInsight.AnnotationUtil;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.impl.source.javadoc.PsiDocParamRef;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.javadoc.PsiDocToken;
import com.intellij.psi.util.PsiTypesUtil;
import com.intellij.util.ui.TextTransferable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
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
    public void actionPerformed(@NotNull AnActionEvent e) {
        PsiMethod method = BaseWebCopyAction.method(e);
        PsiAnnotation classAnnotation = BaseWebCopyAction.classAnnotation(method);
        PsiAnnotation methodAnnotation = BaseWebCopyAction.methodAnnotation(method);
        // 方法参数注释缓存
        Map<String, String> comments = comments(method);

        Map<String, Object> map = new HashMap<>(4);
        ApiDocSettingPo state = ApiDocStateComponent.getInstance().getState();
        if (Objects.isNull(state)) {
            return;
        }

        String url = BaseWebCopyAction.url(classAnnotation, methodAnnotation);
        map.put("url", url);

        String httpMethod = getMethod(classAnnotation, methodAnnotation);
        map.put("method", httpMethod);

        // 路径参数列表
        List<Var> pathVariables = pathVariables(method, comments, state);
        map.put("hasPathVariables", !pathVariables.isEmpty());
        map.put("pathVariables", pathVariables);

        // 查询参数列表
        List<Var> queryParams = queryParams(method, comments, state);
        map.put("hasQueryParams", !queryParams.isEmpty());
        map.put("queryParams", queryParams);
        // body
        String body = body(method, comments, state);
        // body示例
        map.put("hasBody", Objects.nonNull(body));
        map.put("body", body);

        // 应答示例
        String response = response(method, state);
        map.put("hasResponse", Objects.nonNull(response));
        map.put("response", response);

        // 模版替换 发送api文档至剪贴板
        CopyPasteManager.getInstance()
            .setContents(new TextTransferable(VelocityUtil.format(state.getTemplate(), map)));
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
                    .filter(it -> "param".equals(it.getName()))
                    .forEach(it ->
                        Arrays.stream(it.getDataElements())
                            .filter(e -> e instanceof PsiDocParamRef)
                            .map(PsiDocParamRef.class::cast)
                            .findFirst()
                            .map(PsiDocParamRef::getText)
                            .ifPresent(p ->
                                comments.put(
                                    p,
                                    Arrays.stream(it.getDataElements())
                                        .filter(e -> e instanceof PsiDocToken)
                                        .map(PsiDocToken.class::cast)
                                        .filter(SpringWebPsiUtil::isDocCommentData)
                                        .map(e -> e.getText().trim())
                                        .collect(Collectors.joining(""))
                                )
                            )
                    )
            );

        return comments;
    }

    /**
     * 获取接口路径参数
     * @param method   {@link PsiMethod}
     * @param comments 方法注释
     * @param state    配置状态
     * @return 路径参数列表
     */
    private static List<Var> pathVariables(PsiMethod method, Map<String, String> comments, ApiDocSettingPo state) {
        return
            Arrays.stream(method.getParameterList().getParameters())
                .filter(SpringWebPsiUtil::isValidParamType)
                .map(it -> {
                    PsiClass psiClass = ((PsiClassType) it.getType()).resolve();
                    if (Objects.isNull(psiClass)) {
                        return null;
                    }
                    String type = psiClass.getQualifiedName();
                    PsiAnnotation annotation = AnnotationUtil.findAnnotation(
                        it, WebCopyConstants.PATH_VARIABLE_ANNOTATION);
                    String alias = state.alias(type);
                    // 必须要@PathVariable注解存在且是有效别名
                    if (Objects.nonNull(annotation) && !UNKNOWN_ALIAS.equals(alias)) {
                        return
                            new Var(
                                Optional.ofNullable(PsiAnnotationMemberValueUtil.value(annotation, "value"))
                                    .map(String::valueOf)
                                    .orElseGet(it::getName),
                                type,
                                alias,
                                comments.getOrDefault(it.getName(), it.getName())
                            );
                    }

                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * 查询参数注解
     * @param method   {@link PsiMethod}
     * @param comments 方法注释
     * @param state    配置状态
     * @return {@link List}
     */
    private static List<Var> queryParams(PsiMethod method, Map<String, String> comments, ApiDocSettingPo state) {
        return
            Arrays.stream(method.getParameterList().getParameters())
                .filter(it ->
                    // 排除接口类型参数、忽略类型、带忽略类型注解的参数
                    SpringWebPsiUtil.isValidParamType(
                        it,
                        state.getQueryParamIgnoreTypes(),
                        state.getQueryParamIgnoreAnnotations()
                    )
                )
                .flatMap(it -> {
                    PsiClass clazz = PsiTypesUtil.getPsiClass(it.getType());
                    // 未知类型
                    if (Objects.isNull(clazz)) {
                        return Stream.empty();
                    }
                    // 查询参数注解
                    PsiAnnotation annotation = AnnotationUtil.findAnnotation(
                        it, WebCopyConstants.QUERY_PARAM_ANNOTATION);
                    String type = clazz.getQualifiedName();
                    String alias = state.alias(type);
                    // 对象类型
                    if (UNKNOWN_ALIAS.equals(alias)) {
                        return
                            Arrays.stream(clazz.getAllFields())
                                .filter(SpringWebPsiUtil::isValidFiled)
                                // 查询参数只遍历一层 且忽略掉这层的未知类型
                                .filter(f -> !UNKNOWN_ALIAS.equals(state.alias(f.getType())))
                                .map(f ->
                                    new Var(
                                        f.getName(),
                                        Objects.requireNonNull(
                                            PsiTypesUtil.getPsiClass(f.getType())
                                        ).getQualifiedName(),
                                        state.alias(f.getType()),
                                        Optional.ofNullable(f.getDocComment())
                                            .map(PsiDocComment::getDescriptionElements)
                                            .map(els ->
                                                Arrays.stream(els)
                                                    .filter(e -> e instanceof PsiDocToken)
                                                    .map(PsiDocToken.class::cast)
                                                    .filter(SpringWebPsiUtil::isDocCommentData)
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
                                        .orElseGet(it::getName),
                                    type,
                                    alias,
                                    comments.getOrDefault(it.getName(), it.getName())
                                )
                            );
                    }
                })
                .collect(Collectors.toList());
    }

    /**
     * 获得方法的报文参数
     * @param method   {@link PsiMethod}
     * @param comments 方法注释
     * @param state    配置状态
     * @return json5
     */
    private static String body(PsiMethod method, Map<String, String> comments, ApiDocSettingPo state) {
        PsiParameter parameter =
            Arrays.stream(method.getParameterList().getParameters())
                // 排除body参数类型为排除类型 即无法序列化的类型 如Object、Map等
                .filter(it -> SpringWebPsiUtil.isValidParamType(it, state.getBodyIgnoreTypes()))
                // 方法参数中有@RequestBody注解的参数且只会取第一个
                .filter(it -> AnnotationUtil.findAnnotation(it, WebCopyConstants.BODY_ANNOTATION) != null)
                .findFirst()
                .orElse(null);
        if (Objects.isNull(parameter)) {
            return null;
        }

        return
            Json5Generator.toJson5(
                parameter.getType(),
                comments.get(parameter.getName()),
                Collections.unmodifiableList(state.getBodyIgnoreTypes()),
                Collections.unmodifiableList(state.getObjects())
            );
    }

    /**
     * 获得方法应答参数
     * @param method {@link PsiMethod}
     * @param state  配置状态
     * @return json5
     */
    private static String response(PsiMethod method, ApiDocSettingPo state) {
        return
            Json5Generator.toJson5(
                method.getReturnType(),
                SpringWebPsiUtil.returnComment(method),
                Collections.unmodifiableList(state.getBodyIgnoreTypes()),
                Collections.unmodifiableList(state.getObjects())
            );
    }

    /**
     * 参数实体
     */
    @AllArgsConstructor
    @Getter
    public static class Var {
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
