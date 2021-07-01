package com.github.passerr.idea.plugins.mybatis;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.util.ui.TextTransferable;
import org.apache.commons.lang.StringUtils;

import java.util.Optional;

/**
 * mybatis日志转可执行sql
 * @author xiehai1
 * @date 2018/11/08 11:47
 * @Copyright (c) gome inc Gome Co.,LTD
 */
public class MybatisLog2SqlAction extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent e) {
        // 选中的日志内容
        String log =
            Optional.ofNullable(e.getData(PlatformDataKeys.EDITOR))
                .map(Editor::getSelectionModel)
                .map(SelectionModel::getSelectedText)
                .orElse(null);
        if (StringUtils.isNotEmpty(log)) {
            // 格式化后的sql
            String sql = LogParser.toSql(log);
            if (StringUtils.isNotEmpty(sql)) {
                // 自动发送到剪贴板
                CopyPasteManager.getInstance().setContents(new TextTransferable(SqlFormatter.format(sql)));
            }
        }
    }
}
