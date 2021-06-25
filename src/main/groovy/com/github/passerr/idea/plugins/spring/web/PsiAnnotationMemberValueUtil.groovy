package com.github.passerr.idea.plugins.spring.web

import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiAnnotation
import com.intellij.psi.PsiAnnotationMemberValue
import com.intellij.psi.PsiExpression
import com.intellij.psi.impl.JavaConstantExpressionEvaluator

/**
 * {@link com.intellij.psi.PsiAnnotationMemberValue}工具
 * @date 2021/06/25 19:30
 * @Copyright (c) wisewe co.,ltd
 * @author xiehai
 */
class PsiAnnotationMemberValueUtil {
    static Object value(PsiAnnotation annotation, String attribute) {
        def v = annotation.findAttributeValue(attribute)

        return v ? value(v) : null
    }

    static Object value(PsiAnnotationMemberValue value) {
        if (value instanceof PsiExpression) {
            return JavaConstantExpressionEvaluator.computeConstantExpression(value, false)
        }

        return JavaPsiFacade.getInstance(value.getProject()).getConstantEvaluationHelper().computeConstantExpression(value)
    }
}
