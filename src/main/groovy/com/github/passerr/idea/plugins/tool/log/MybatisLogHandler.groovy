package com.github.passerr.idea.plugins.tool.log

import com.github.passerr.idea.plugins.mybatis.LogParser
import org.apache.commons.lang.StringUtils
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea
import org.fife.ui.rtextarea.RTextArea
import org.fife.ui.rtextarea.ToolTipSupplier

import java.awt.*
import java.awt.event.MouseEvent

/**
 * mybatis日志处理器
 * @author xiehai1
 * @date 2018/11/08 18:11
 * @Copyright ( c ) gome inc Gome Co.,LTD
 */
class MybatisLogHandler {
    static void handle(RSyntaxTextArea textArea) {
        def sql = LogParser.toSql(textArea.getText())
        if (StringUtils.isEmpty(sql)) {
            // 设置错误行背景色
            // 默认设置第一行
            textArea.addLineHighlight(0, Color.RED)
            // 设置提示信息
            textArea.setToolTipSupplier({ RTextArea rt, MouseEvent me ->
                def offset = textArea.getLineOfOffset(textArea.viewToModel(me.getPoint()))
                offset == 0 ? "the log you input which without \"Preparing:\" or \"Parameters:\"" : null
            } as ToolTipSupplier)
        } else {
            textArea.setText(SqlFormatter.format(sql))
        }
    }
}
