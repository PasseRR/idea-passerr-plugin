package com.github.passerr.idea.plugins.mybatis


import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.ide.CopyPasteManager
import com.intellij.util.ui.TextTransferable
import org.apache.commons.lang.StringUtils
/**
 * mybatis日志转可执行sql
 * @date 2018/11/08 11:47
 * @Copyright (c) gome inc Gome Co.,LTD
 * @author xiehai1
 */
class MybatisLog2SqlAction extends AnAction {
    @Override
    void actionPerformed(AnActionEvent e) {
        // 选中的日志内容
        def log = e.getData(PlatformDataKeys.EDITOR).getSelectionModel().getSelectedText()
        if (StringUtils.isNotEmpty(log)) {
            // 格式化后的sql
            def sql = LogParser.toSql(log)
            if (StringUtils.isNotEmpty(sql)) {
                // 自动发送到剪贴板
                CopyPasteManager.getInstance().setContents(new TextTransferable(SqlFormatter.format(sql)))
            }
        }
    }
}
