package com.github.passerr.idea.plugins.spring.web

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.ide.CopyPasteManager
import com.intellij.psi.PsiMethod
import com.intellij.util.ui.TextTransferable

/**
 * web路径copy
 * @date 2021/06/25 16:03
 * @Copyright (c) wisewe co.,ltd
 * @author xiehai
 */
class CopyMethodPathAction extends BaseWebCopyAction {
    @Override
    void actionPerformed(AnActionEvent e) {
        PsiMethod method = method(e)
        CopyPasteManager.getInstance()
            .setContents(
                new TextTransferable(
                    url(
                        classAnnotation(e),
                        methodAnnotation(e)
                    )
                )
            )
    }
}
