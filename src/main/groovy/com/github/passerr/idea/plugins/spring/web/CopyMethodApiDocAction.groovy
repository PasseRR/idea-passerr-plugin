package com.github.passerr.idea.plugins.spring.web


import com.intellij.codeInsight.AnnotationUtil
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.psi.PsiAnnotation
import com.intellij.psi.PsiMethod
import com.intellij.psi.javadoc.PsiDocTag
import com.intellij.psi.javadoc.PsiDocToken

/**
 * web方法接口文档复制
 * @date 2021/06/28 13:34
 * @Copyright (c) wisewe co.,ltd
 * @author xiehai
 */
class CopyMethodApiDocAction extends BaseWebCopyAction {
    @Override
    void actionPerformed(AnActionEvent e) {
        PsiMethod method = method(e)
        PsiAnnotation classAnnotation = classAnnotation(method)
        PsiAnnotation methodAnnotation = methodAnnotation(method)
        String url = url(classAnnotation, methodAnnotation)
        String httpMethod = getMethod(classAnnotation, methodAnnotation)
        List<Var> pathVariables = pathVariables(method)
        // 路径参数列表
        // 查询参数列表
        // body
        // 请求示例
        // 应答示例
    }

    /**
     * 获取http方法
     * @param classAnnotation 类上的注解
     * @param methodAnnotation 方法上的注解
     * @return http方法类型
     */
    private static String getMethod(PsiAnnotation classAnnotation, PsiAnnotation methodAnnotation) {
        if (REQUEST_MAPPING != methodAnnotation.getQualifiedName()) {
            return MAPPINGS[methodAnnotation.qualifiedName]
        }

        Object methodOnMethod = PsiAnnotationMemberValueUtil.getArrayFirstValue(methodAnnotation, "method")
        if (methodOnMethod) {
            return methodOnMethod as String
        }

        Object methodOnClass = PsiAnnotationMemberValueUtil.getArrayFirstValue(classAnnotation, "method")
        if (methodOnClass) {
            return methodOnClass as String
        }

        return "UNKNOWN"
    }

    /**
     * 获取接口路径参数
     * @param method {@link PsiMethod}
     * @return 路径参数列表
     */
    private static List<Var> pathVariables(PsiMethod method) {
        Map<String, String> comments = [:]
        if (method.getDocComment()) {
            // 方法注释tag列表
            method.getDocComment().getTags()
                .findAll { it -> it instanceof PsiDocTag && it.getName() == "param" }
                .collect { it -> it as PsiDocTag }
                .each { it ->
                    comments.put(
                        it.getName(),
                        it.getDataElements()
                            .findAll { e ->
                                e instanceof PsiDocToken && e.getTokenType().toString() == "DOC_COMMENT_DATA"
                            }
                            .collect { e -> (e as PsiDocToken).getText().trim() }
                            .join(""))
                }
        }

        method.getParameterList()
            .getParameters()
            .collect { it ->
                PsiAnnotation annotation = AnnotationUtil.findAnnotation(it, WebCopyConstants.PATH_VARIABLE_ANNOTATION)
                if (annotation) {
                    String type = it.getType().getCanonicalText()
                    return new Var(
                        name: PsiAnnotationMemberValueUtil.value(annotation, "value") ?: it.getText(),
                        type: type,
                        alias: ApiDocStateComponent.getInstance().alias(type),
                        desc: comments.getOrDefault(it.getText(), it.getText())
                    )
                }

                return null
            }.findAll { it -> it != null }
    }

    private static List<Var> queryParams(PsiMethod method) {
        null
    }

    /**
     * 参数实体
     */
    private static class Var {
        /**
         * 参数名
         */
        String name
        /**
         * 类型
         */
        String type
        /**
         * 类型别名
         */
        String alias
        /**
         * 参数描述
         */
        String desc
    }
}
