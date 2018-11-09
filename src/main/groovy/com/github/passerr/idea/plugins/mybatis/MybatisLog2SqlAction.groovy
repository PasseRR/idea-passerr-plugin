package com.github.passerr.idea.plugins.mybatis

import com.github.passerr.idea.plugins.tool.log.SqlFormatter
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import org.apache.commons.lang.StringUtils

import java.awt.*
import java.awt.datatransfer.Clipboard
import java.awt.datatransfer.StringSelection

/**
 * mybatis日志转可执行sql
 * @author xiehai1
 * @date 2018/11/08 11:47
 * @Copyright ( c ) gome inc Gome Co.,LTD
 */
class MybatisLog2SqlAction extends AnAction {
    @Override
    void actionPerformed(AnActionEvent e) {
        // 选中的日志内容
        def log = e.getData(PlatformDataKeys.EDITOR).getSelectionModel().getSelectedText()
        if (StringUtils.isNotEmpty(log)) {
            Optional.ofNullable(LogParser.toSql(log))
                .filter(StringUtils.&isNotEmpty)
                // 自动发送到剪贴板
                .ifPresent({ sql ->
                    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard()
                    clipboard.setContents(new StringSelection(SqlFormatter.format(sql)), null)
                })
        }
    }
}
