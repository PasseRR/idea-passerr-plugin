package com.github.passerr.idea.plugins.database.generator.action;

import com.intellij.database.psi.DbTable;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 代码生成
 * @author xiehai
 * @date 2022/06/24 13:49
 */
public class CodeGenerateAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (Objects.isNull(project)) {
            return;
        }

        PsiElement[] data = e.getData(LangDataKeys.PSI_ELEMENT_ARRAY);
        if (Objects.isNull(data)) {
            return;
        }

        new GenerateDialog(
            project,
            Arrays.stream(data)
                .filter(it -> it instanceof DbTable)
                .map(DbTable.class::cast)
                .collect(Collectors.toList())
        ).show();
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        e.getPresentation().setVisible(
            Optional.ofNullable(e.getData(LangDataKeys.PSI_ELEMENT_ARRAY))
                .map(Arrays::stream)
                .map(s -> s.anyMatch(it -> it instanceof DbTable))
                .orElse(false)
        );
    }
}
