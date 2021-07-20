package com.github.passerr.idea.plugins.spring.web;

import com.intellij.codeInsight.AnnotationUtil;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifier;
import com.intellij.psi.PsiModifierList;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.javadoc.PsiDocToken;
import com.intellij.psi.util.PsiTypesUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * psi类型工具
 * @author xiehai
 * @date 2021/07/20 14:27
 * @Copyright(c) tellyes tech. inc. co.,ltd
 */
public interface PsiUtil {
    /**
     * 是否是合法的参数类型
     * 非接口类型且满足排除类型
     * @param parameter {@link PsiParameter}
     * @return true/false
     */
    static boolean isValidParamType(PsiParameter parameter) {
        return isValidParamType(parameter, new ArrayList<>());
    }

    /**
     * 是否是合法参数类型
     * @param parameter    {@link PsiParameter}
     * @param excludeTypes 排除类型
     * @return true/false
     */
    static boolean isValidParamType(PsiParameter parameter, List<String> excludeTypes) {
        return isValidParamType(parameter, excludeTypes, new ArrayList<>());
    }

    /**
     * 是否是合法的参数类型
     * @param parameter          {@link PsiParameter}
     * @param excludeTypes       排除类型
     * @param excludeAnnotations 排除注解
     * @return true/false
     */
    static boolean isValidParamType(PsiParameter parameter, List<String> excludeTypes,
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
    static boolean isValidFiled(PsiField field) {
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
    static boolean isDocCommentData(PsiDocToken token) {
        return "DOC_COMMENT_DATA".equals(token.getTokenType().toString());
    }

    /**
     * 返回类型注释
     * @param method {@link PsiMethod}
     * @return 返回注释
     */
    static String returnComment(PsiMethod method) {
        PsiDocComment docComment = method.getDocComment();
        if (Objects.isNull(docComment)) {
            return null;
        }

        return
            Arrays.stream(docComment.getTags())
                .filter(it -> "return".equals(it.getName()))
                .findFirst()
                .map(it ->
                    Arrays.stream(it.getDataElements())
                        .filter(e -> e instanceof PsiDocToken)
                        .map(PsiDocToken.class::cast)
                        .filter(PsiUtil::isDocCommentData)
                        .map(e -> e.getText().trim())
                        .collect(Collectors.joining(""))
                )
                .orElse(null);
    }
}
