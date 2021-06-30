package com.github.passerr.idea.plugins.spring.web;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.psi.PsiMethod;
import com.intellij.util.ui.TextTransferable;

/**
 * web路径copy
 * @author xiehai
 * @date 2021/06/30 19:14
 * @Copyright(c) tellyes tech. inc. co.,ltd
 */
public class CopyMethodPathAction extends BaseWebCopyAction {
    @Override
    public void actionPerformed(AnActionEvent e) {
        PsiMethod method = method(e);
        CopyPasteManager.getInstance()
            .setContents(
                new TextTransferable(
                    url(
                        classAnnotation(method),
                        methodAnnotation(method)
                    )
                )
            );
    }
}