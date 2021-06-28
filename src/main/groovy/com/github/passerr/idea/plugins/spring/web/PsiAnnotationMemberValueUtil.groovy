package com.github.passerr.idea.plugins.spring.web

import com.intellij.psi.PsiAnnotation
import com.intellij.psi.PsiAnnotationMemberValue
import com.intellij.psi.PsiArrayInitializerMemberValue
import com.intellij.psi.PsiExpression
import com.intellij.psi.PsiField
import com.intellij.psi.PsiLiteralExpression
import com.intellij.psi.PsiReferenceExpression

/**
 * {@link com.intellij.psi.PsiAnnotationMemberValue}工具
 * @date 2021/06/25 19:30
 * @Copyright (c) wisewe co.,ltd
 * @author xiehai
 */
class PsiAnnotationMemberValueUtil {
    private PsiAnnotationMemberValueUtil() {

    }

    /**
     * 获取注解属性
     * @param annotation 注解
     * @param attribute 属性名
     * @return 属性值
     */
    static Object value(PsiAnnotation annotation, String attribute) {
        if (Objects.isNull(annotation)) {
            return null
        }

        return value(annotation.findAttributeValue(attribute))
    }

    static Object value(PsiAnnotationMemberValue v) {
        if (Objects.isNull(v)) {
            return null
        }

        if (v instanceof PsiArrayInitializerMemberValue) {
            return v.getInitializers().collect { it -> value(it) } as Object[]
        }

        if (v instanceof PsiExpression) {
            return value(v)
        }

        return v.text
    }

    static Object value(PsiExpression value) {
        if (value instanceof PsiLiteralExpression) {
            return value.value
        }

        if (value instanceof PsiReferenceExpression) {
            def resolve = value.resolve()
            if (resolve instanceof PsiField) {
                return resolve.computeConstantValue() ?: resolve.text
            }

            return value(resolve)
        }

        return value.text
    }

    static Object getArrayFirstValue(PsiAnnotation annotation, String attribute) {
        Object value = value(annotation, attribute)
        if (value.getClass().isArray()) {
            Object[] array = value as Object[]
            if (array.length > 0) {
                return array[0]
            }

            return null
        }

        return value
    }
}
