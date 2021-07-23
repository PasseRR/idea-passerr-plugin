package com.github.passerr.idea.plugins.spring.web;

import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiAnnotationMemberValue;
import com.intellij.psi.PsiArrayInitializerMemberValue;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiLiteralExpression;
import com.intellij.psi.PsiReferenceExpression;
import com.intellij.psi.impl.compiled.ClsEnumConstantImpl;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 * {@link com.intellij.psi.PsiAnnotationMemberValue}工具
 * @author xiehai
 * @date 2021/06/25 19:30
 * @Copyright (c) wisewe co.,ltd
 */
class PsiAnnotationMemberValueUtil {
    private PsiAnnotationMemberValueUtil() {

    }

    /**
     * 获取注解属性
     * @param annotation 注解
     * @param attribute  属性名
     * @return 属性值
     */
    static Object value(PsiAnnotation annotation, String attribute) {
        if (Objects.isNull(annotation)) {
            return null;
        }

        return value(annotation.findAttributeValue(attribute));
    }

    static Object value(PsiAnnotationMemberValue v) {
        if (Objects.isNull(v)) {
            return null;
        }

        if (v instanceof PsiArrayInitializerMemberValue) {
            return
                Arrays.stream(((PsiArrayInitializerMemberValue) v).getInitializers())
                    .map(PsiAnnotationMemberValueUtil::value)
                    .toArray();
        }

        if (v instanceof PsiExpression) {
            return value((PsiExpression) v);
        }

        return v.getText();
    }

    static Object value(PsiExpression value) {
        if (value instanceof PsiLiteralExpression) {
            return ((PsiLiteralExpression) value).getValue();
        }

        if (value instanceof PsiReferenceExpression) {
            PsiElement resolve = ((PsiReferenceExpression) value).resolve();
            if (resolve instanceof PsiField) {
                return Optional.ofNullable(((PsiField) resolve).computeConstantValue())
                    .orElseGet(resolve::getText);
            }

            return value((PsiExpression) resolve);
        }

        return value.getText();
    }

    static Object getArrayFirstValue(PsiAnnotation annotation, String attribute) {
        Object value = value(annotation, attribute);
        if (Objects.nonNull(value) && value.getClass().isArray()) {
            Object[] array = (Object[]) value;
            if (array.length > 0) {
                return format(array[0]);
            }

            return null;
        }

        return format(value);
    }

    static Object format(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof String || value.getClass().isPrimitive()) {
            return value;
        }

        if (value instanceof ClsEnumConstantImpl) {
            return ((ClsEnumConstantImpl) value).getName();
        }

        // 注解类型忽略
        return null;
    }
}
