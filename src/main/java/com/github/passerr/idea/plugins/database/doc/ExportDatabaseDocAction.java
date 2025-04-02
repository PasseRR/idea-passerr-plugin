package com.github.passerr.idea.plugins.database.doc;

import com.intellij.database.model.DasObject;
import com.intellij.database.model.ObjectKind;
import com.intellij.database.psi.DbElement;
import com.intellij.database.util.DasUtil;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 数据库设计文档导出
 * @author xiehai
 * @date 2022/06/16 16:57
 */
public class ExportDatabaseDocAction extends DumbAwareAction {
    @Override
    public void update(@NotNull AnActionEvent e) {
        e.getPresentation().setEnabledAndVisible(
            Optional.ofNullable(e.getData(LangDataKeys.PSI_ELEMENT_ARRAY))
                .map(Arrays::stream)
                .map(s ->
                    // 仅当选中schema有效
                    s.anyMatch(it -> it instanceof DbElement && ObjectKind.SCHEMA.equals(((DbElement) it).getKind()))
                )
                .orElse(false)
        );
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        PsiElement[] data = e.getData(LangDataKeys.PSI_ELEMENT_ARRAY);
        if (Objects.isNull(data)) {
            return;
        }

        List<DasObject> schemas =
            Arrays.stream(data)
                .map(DbElement.class::cast)
                .map(DasUtil::getSchemaObject)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        new ExportSettingDialog(schemas).show();
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }
}
