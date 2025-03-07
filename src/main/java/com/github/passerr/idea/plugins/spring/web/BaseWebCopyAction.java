package com.github.passerr.idea.plugins.spring.web;

import com.intellij.codeInsight.AnnotationUtil;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifierListOwner;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * spring web基础action
 * @author xiehai
 * @date 2021/06/30 18:57
 * @Copyright(c) tellyes tech. inc. co.,ltd
 */
abstract class BaseWebCopyAction extends DumbAwareAction {
    protected static final String REQUEST_MAPPING = "org.springframework.web.bind.annotation.RequestMapping";
    protected static final Map<String, String> MAPPINGS = new HashMap<>();

    static {
        MAPPINGS.put("org.springframework.web.bind.annotation.GetMapping", "GET");
        MAPPINGS.put("org.springframework.web.bind.annotation.PostMapping", "POST");
        MAPPINGS.put("org.springframework.web.bind.annotation.PutMapping", "PUT");
        MAPPINGS.put("org.springframework.web.bind.annotation.DeleteMapping", "DELETE");
        MAPPINGS.put("org.springframework.web.bind.annotation.PatchMapping", "PATCH");

        // requestMapping
        MAPPINGS.put(REQUEST_MAPPING, null);
    }

    @Override
    public void update(AnActionEvent e) {
        PsiElement data = e.getData(CommonDataKeys.PSI_ELEMENT);

        // 方法上存在注解
        e.getPresentation().setEnabled(
            data instanceof PsiMethod
                && AnnotationUtil.findAnnotations((PsiModifierListOwner) data, MAPPINGS.keySet()).length > 0
        );
    }

    protected static PsiMethod method(AnActionEvent e) {
        // 肯定是PsiMethod
        return (PsiMethod) e.getData(CommonDataKeys.PSI_ELEMENT);
    }

    protected static PsiAnnotation classAnnotation(PsiMethod method) {
        return AnnotationUtil.findAnnotation(method.getContainingClass(), REQUEST_MAPPING);
    }

    protected static PsiAnnotation methodAnnotation(PsiMethod method) {
        return AnnotationUtil.findAnnotations(method, MAPPINGS.keySet())[0];
    }

    protected static String url(PsiAnnotation classAnnotation, PsiAnnotation methodAnnotation) {
        // RequestMapping前缀
        String prefix = Optional.ofNullable(PsiAnnotationMemberValueUtil.getArrayFirstValue(classAnnotation, "value"))
            .map(String::valueOf).orElse("");
        // 肯定存在一个注解满足条件
        String suffix = Optional.ofNullable(PsiAnnotationMemberValueUtil.getArrayFirstValue(methodAnnotation, "value"))
            .map(String::valueOf).orElse("");
        return prefix + suffix;
    }
}
