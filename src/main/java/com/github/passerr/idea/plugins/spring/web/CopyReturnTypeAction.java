package com.github.passerr.idea.plugins.spring.web;

import com.github.passerr.idea.plugins.spring.web.po.ApiDocSettingPo;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.psi.PsiMethod;
import com.intellij.util.ui.TextTransferable;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Objects;
import java.util.Optional;

/**
 * 复制方法返回类型为json5
 * @author xiehai
 * @date 2021/07/22 12:29
 * @Copyright(c) tellyes tech. inc. co.,ltd
 */
public class CopyReturnTypeAction extends BaseWebCopyAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        PsiMethod method = BaseWebCopyAction.method(e);
        ApiDocSettingPo state = ApiDocStateComponent.getInstance().getState();
        if (Objects.isNull(state)) {
            return;
        }

        Optional.ofNullable(
                Json5Generator.toJson5(
                    method.getReturnType(),
                    null,
                    Collections.unmodifiableList(state.getBodyIgnoreAnnotations()),
                    Collections.unmodifiableList(state.getObjects())
                )
            )
            .ifPresent(it -> CopyPasteManager.getInstance().setContents(new TextTransferable(it)));
    }

    @Override
    public void update(AnActionEvent e) {
        // 方法上才有效
        e.getPresentation().setEnabled(e.getData(CommonDataKeys.PSI_ELEMENT) instanceof PsiMethod);
    }
}
