package com.github.passerr.idea.plugins.tool

import com.github.passerr.idea.plugins.NotificationThread
import com.github.passerr.idea.plugins.mybatis.LogParser
import com.github.passerr.idea.plugins.mybatis.SqlFormatter
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import com.google.gson.JsonSyntaxException
import com.intellij.notification.Notification
import org.apache.commons.lang.StringUtils
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea
import org.fife.ui.rsyntaxtextarea.SyntaxConstants
import org.fife.ui.rtextarea.RTextArea
import org.fife.ui.rtextarea.ToolTipSupplier

import java.awt.*
import java.awt.event.MouseEvent
import java.util.regex.Pattern

import static com.intellij.notification.NotificationType.ERROR

/**
 * 转换类型
 * @author xiehai1* @date 2018/11/08 18:33
 * @Copyright (c) gome inc Gome Co.,LTD
 */
enum ConvertType {
    /**
     * 自动格式化json
     */
    JSON(SyntaxConstants.SYNTAX_STYLE_JSON){
        @Override
        void handle(RSyntaxTextArea textArea) {
            try {
                textArea.setToolTipSupplier(null)
                textArea.removeAllLineHighlights()
                JsonParser jsonParser = new JsonParser()
                // 可以同时解析数组或者Object
                JsonElement element = jsonParser.parse(textArea.getText())
                Gson gson = new GsonBuilder().setPrettyPrinting().create()
                textArea.setText(gson.toJson(element))
                // 格式化成功后定位到第一行
                textArea.setCaretPosition(0)
            } catch (JsonSyntaxException ex) {
                String msg = ex.getMessage()
                def matcher = ERROR_PATTERN.matcher(msg)
                if (matcher.find()) {
                    // 行索引从0开始
                    int lineIndex = (matcher.group(1) as int) - 1
                    // 设置错误行背景色
                    textArea.addLineHighlight(lineIndex, Color.RED)
                    // 设置提示信息
                    textArea.setToolTipSupplier({ RTextArea rt, MouseEvent me ->
                        def offset = textArea.getLineOfOffset(textArea.viewToModel(me.getPoint()))
                        offset == lineIndex ? msg : null
                    } as ToolTipSupplier)
                    // 定位到失败行
                    textArea.setCaretPosition(lineIndex)
                } else {
                    new NotificationThread(new Notification("Json Format", "Json Format", msg, ERROR)).start()
                }
            }
        }
    },
    /**
     * mybatis日志转可执行sql
     */
    SQL(SyntaxConstants.SYNTAX_STYLE_SQL){
        @Override
        void handle(RSyntaxTextArea textArea) {
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
    },
    /**
     * 文本
     */
    TEXT(SyntaxConstants.SYNTAX_STYLE_NONE)

    String style

    /**
     * json错误信息正则匹配
     */
    private static final ERROR_PATTERN = Pattern.compile("line (\\w+) column")

    ConvertType(String style) {
        this.style = style
    }

    /**
     * 对文本进行格式化
     * @param textArea {@link RSyntaxTextArea}
     */
    void handle(RSyntaxTextArea textArea) {
        // 默认不做任何处理
    }
}
