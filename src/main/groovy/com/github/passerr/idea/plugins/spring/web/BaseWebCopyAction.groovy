package com.github.passerr.idea.plugins.spring.web


import com.intellij.codeInsight.AnnotationUtil
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.psi.PsiAnnotation
import com.intellij.psi.PsiMethod

/**
 * spring web 复制基类
 * @date 2021/06/28 11:40
 * @Copyright (c) wisewe co.,ltd
 * @author xiehai
 */
abstract class BaseWebCopyAction extends AnAction {
    protected static final String REQUEST_MAPPING = "org.springframework.web.bind.annotation.RequestMapping"
    protected static final Map<String, String> MAPPINGS = [
        "org.springframework.web.bind.annotation.GetMapping"   : "GET",
        "org.springframework.web.bind.annotation.PostMapping"  : "POST",
        "org.springframework.web.bind.annotation.PutMapping"   : "PUT",
        "org.springframework.web.bind.annotation.DeleteMapping": "DELETE",
        "org.springframework.web.bind.annotation.PatchMapping" : "PATCH"
    ]

    static {
        // requestMapping
        MAPPINGS.put(REQUEST_MAPPING, null)
    }

    @Override
    void update(AnActionEvent e) {
        DataContext dataContext = e.getDataContext()
        def data = CommonDataKeys.PSI_ELEMENT.getData(dataContext)
        if (data instanceof PsiMethod) {
            e.getPresentation()
                .setEnabled(
                    AnnotationUtil.findAnnotations(data, MAPPINGS.keySet()).length > 0
                )
        } else {
            // 方法上未找到注解
            e.getPresentation().setEnabled(false)
        }

    }

    protected static PsiMethod method(AnActionEvent e) {
        DataContext dataContext = e.getDataContext()
        // 肯定是PsiMethod
        CommonDataKeys.PSI_ELEMENT.getData(dataContext) as PsiMethod
    }

    protected static PsiAnnotation classAnnotation(PsiMethod method) {
        AnnotationUtil.findAnnotation(method.getContainingClass(), REQUEST_MAPPING)
    }

    protected static PsiAnnotation methodAnnotation(PsiMethod method) {
        AnnotationUtil.findAnnotations(method, MAPPINGS.keySet())[0]
    }

    protected static String url(PsiAnnotation classAnnotation, PsiAnnotation methodAnnotation) {
        // RequestMapping前缀
        String prefix = PsiAnnotationMemberValueUtil.getArrayFirstValue(classAnnotation, "value") ?: ""
        // 肯定存在一个注解满足条件
        String suffix = PsiAnnotationMemberValueUtil.getArrayFirstValue(methodAnnotation, "value") ?: ""

        prefix + suffix
    }
}
