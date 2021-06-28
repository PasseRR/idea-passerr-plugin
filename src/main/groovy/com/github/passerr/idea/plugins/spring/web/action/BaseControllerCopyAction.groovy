package com.github.passerr.idea.plugins.spring.web.action

import com.github.passerr.idea.plugins.spring.web.MappingAnnotation
import com.intellij.codeInsight.AnnotationUtil
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.psi.PsiMethod

/**
 * spring web 复制基类
 * @date 2021/06/28 11:40
 * @Copyright (c) wisewe co.,ltd
 * @author xiehai
 */
abstract class BaseControllerCopyAction extends AnAction {
    @Override
    void update(AnActionEvent e) {
        DataContext dataContext = e.getDataContext()
        def data = CommonDataKeys.PSI_ELEMENT.getData(dataContext)
        if (data instanceof PsiMethod) {
            e.getPresentation()
                .setEnabled(
                    AnnotationUtil.findAnnotations(data, MappingAnnotation.values().collect { it -> it.name }).length > 0
                )
        } else {
            // 方法上未找到注解
            e.getPresentation().setEnabled(false)
        }

    }
}
