package com.github.passerr.idea.plugins.database.doc;

import com.intellij.database.model.DasObject;
import com.intellij.database.psi.DbElement;
import com.intellij.database.util.DasUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.pom.Navigatable;
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
public class ExportDatabaseDocAction extends AnAction {
    private static final String SCHEMA = "schema";

    @Override
    public void update(@NotNull AnActionEvent e) {
        boolean enable = Optional.ofNullable(e.getData(PlatformDataKeys.NAVIGATABLE_ARRAY))
            .map(Arrays::stream)
            .map(s ->
                s.anyMatch(it ->
                    it instanceof DbElement
                        // 仅当选中schema有效
                        && SCHEMA.equals(((DbElement) it).getTypeName())
                )
            )
            .orElse(false);
        e.getPresentation().setVisible(enable);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Navigatable[] data = e.getData(PlatformDataKeys.NAVIGATABLE_ARRAY);
        if (Objects.isNull(data)) {
            return;
        }

        List<DasObject> schemas =
            Arrays.stream(data)
                .map(DbElement.class::cast)
                .filter(it -> SCHEMA.equals(it.getTypeName()))
                .map(DasUtil::getSchemaObject)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        new ExportSettingDialog(schemas).show();
    }
}
