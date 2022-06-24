package com.github.passerr.idea.plugins.database.generator.action;

import com.intellij.database.model.DasTable;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.pom.Navigatable;
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

        Navigatable[] data = e.getData(PlatformDataKeys.NAVIGATABLE_ARRAY);
        if (Objects.isNull(data)) {
            return;
        }

        new GenerateDialog(
            project,
            Arrays.stream(data)
                .filter(it -> it instanceof DasTable)
                .map(DasTable.class::cast)
                .collect(Collectors.toList())
        ).show();
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        e.getPresentation().setVisible(
            Optional.ofNullable(e.getData(PlatformDataKeys.NAVIGATABLE_ARRAY))
                .map(Arrays::stream)
                .map(s -> s.anyMatch(it -> it instanceof DasTable))
                .orElse(false)
        );
    }
}
