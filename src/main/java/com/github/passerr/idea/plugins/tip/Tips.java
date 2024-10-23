package com.github.passerr.idea.plugins.tip;

import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.javadoc.PsiDocToken;

import java.util.Arrays;
import java.util.Objects;

/**
 * 提示设置工具
 * @author xiehai
 * @date 2024/10/23 14:25
 */
interface Tips {
    /**
     * 设置提示
     * @param project 项目
     * @param node    节点
     * @param file    文件
     */
    static void tip(Project project, AbstractTreeNode<?> node, VirtualFile file) {
        if (Objects.isNull(file) || !file.isDirectory()) {
            return;
        }

        PresentationData presentation = node.getPresentation();
        VirtualFile info = file.findChild("package-info.java");
        if (Objects.isNull(info)) {
            presentation.setLocationString("");
            return;
        }

        PsiFile psifile = PsiManager.getInstance(project).findFile(info);
        if (!(psifile instanceof PsiJavaFile)) {
            presentation.setLocationString("");
            return;
        }

        Arrays.stream(psifile.getChildren())
            // 找到package中的doc注释 只找第一个doc
            .filter(it -> it instanceof PsiDocComment)
            .map(PsiDocComment.class::cast)
            .findFirst()
            .flatMap(it ->
                Arrays.stream(it.getDescriptionElements())
                    // 找到doc注释中内容
                    .filter(c -> c instanceof PsiDocToken)
                    .map(PsiDocToken.class::cast)
                    .findFirst()
            )
            .ifPresent(it -> presentation.setLocationString(it.getText()));
    }
}
