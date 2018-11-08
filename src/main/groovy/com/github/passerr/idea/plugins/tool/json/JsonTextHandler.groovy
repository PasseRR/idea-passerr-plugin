package com.github.passerr.idea.plugins.tool.json

import com.github.passerr.idea.plugins.NotificationThread
import com.google.gson.*
import com.intellij.notification.Notification
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea
import org.fife.ui.rtextarea.RTextArea
import org.fife.ui.rtextarea.ToolTipSupplier

import java.awt.*
import java.awt.event.MouseEvent
import java.util.regex.Pattern
import static com.intellij.notification.NotificationType.ERROR

/**
 * json字符串处理器
 * @author xiehai1
 * @date 2018/11/08 18:05
 * @Copyright ( c ) gome inc Gome Co.,LTD
 */
class JsonTextHandler {
    /**
     * json错误信息正则匹配
     */
    private static final Pattern ERROR_PATTERN = Pattern.compile("line (\\w+) column")

    /**
     * json字符串格式化
     * @param textArea 文本输入域
     */
    static void handle(RSyntaxTextArea textArea) {
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
}
