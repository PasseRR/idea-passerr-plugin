package com.github.passerr.idea.plugins.spring.web.action

import com.github.passerr.idea.plugins.spring.web.MappingAnnotation
import com.github.passerr.idea.plugins.spring.web.PsiAnnotationMemberValueUtil
import com.intellij.codeInsight.AnnotationUtil
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.ide.CopyPasteManager
import com.intellij.psi.PsiAnnotation
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiMethod
import com.intellij.util.ui.TextTransferable

/**
 * web路径copy
 * @date 2021/06/25 16:03
 * @Copyright (c) wisewe co.,ltd
 * @author xiehai
 */
class CopyControllerPathAction extends BaseControllerCopyAction {
    @Override
    void actionPerformed(AnActionEvent e) {
        DataContext dataContext = e.getDataContext()
        // 肯定是PsiMethod
        PsiMethod data = CommonDataKeys.PSI_ELEMENT.getData(dataContext) as PsiMethod
        PsiClass clazz = data.getContainingClass()
        PsiAnnotation classAnnotation = AnnotationUtil.findAnnotation(clazz, MappingAnnotation.REQUEST_MAPPING.name)
        String prefix = PsiAnnotationMemberValueUtil.value(classAnnotation, "value") ?: ""
        // 肯定存在一个注解满足条件
        PsiAnnotation methodAnnotation = AnnotationUtil.findAnnotations(data, MappingAnnotation.values().collect { it -> it.name })[0]
        String suffix = PsiAnnotationMemberValueUtil.value(methodAnnotation, "value") ?: ""

        CopyPasteManager.getInstance().setContents(new TextTransferable(prefix + suffix))
    }
}
