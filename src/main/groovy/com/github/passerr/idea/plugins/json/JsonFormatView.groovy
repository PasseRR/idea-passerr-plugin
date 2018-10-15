package com.github.passerr.idea.plugins.json

import com.google.gson.*
import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.project.Project
import org.apache.commons.lang.StringUtils
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea
import org.fife.ui.rsyntaxtextarea.SyntaxConstants
import org.fife.ui.rsyntaxtextarea.Theme
import org.fife.ui.rtextarea.RTextArea
import org.fife.ui.rtextarea.RTextScrollPane
import org.fife.ui.rtextarea.ToolTipSupplier

import javax.swing.*
import java.awt.*
import java.awt.event.ActionEvent
import java.awt.event.InputEvent
import java.awt.event.KeyEvent
import java.awt.event.MouseEvent
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

/**
 * 格式化视图
 * @author xiehai1
 * @date 2018/10/12 16:02
 * @Copyright tellyes tech. inc. co.,ltd
 */
class JsonFormatView {
    JPanel panel = new JPanel(new BorderLayout())
    Project project
    /**
     * cache instance by project
     */
    def private static INSTANCES = [:]
    /**
     * json错误信息正则匹配
     */
    private static final Pattern ERROR_PATTERN = Pattern.compile("line (\\w+) column")

    private JsonFormatView(Project project) {
        this.project = project
        RSyntaxTextArea textArea = new RSyntaxTextArea()
        textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JSON)
        textArea.setCodeFoldingEnabled(true)
        textArea.setAutoIndentEnabled(true)
        textArea.getInputMap(JComponent.WHEN_FOCUSED)
        // 使用快捷键ctrl+enter格式化json
            .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.CTRL_MASK), "format")
        textArea.getActionMap().put("format", new AbstractAction() {
            @Override
            void actionPerformed(ActionEvent e) {
                // 文本框为空 不进行格式化
                if (StringUtils.isEmpty(textArea.getText())) {
                    return
                }
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
                        Notification n = new Notification("Json Format", "Json Format", msg, NotificationType.ERROR)
                        Notifications.Bus.notify(n)
                        new Thread() {
                            @Override
                            void run() {
                                TimeUnit.SECONDS.sleep(4)
                                n.expire()
                            }
                        }.start()
                    }
                } finally {
                    JsonFormatView.this.getPanel().repaint()
                }
            }
        })

        // 设置高亮主题
        InputStream inputStream = this.getClass().getResourceAsStream("/org/fife/ui/rsyntaxtextarea/themes/idea.xml")
        try {
            Theme theme = Theme.load(inputStream)
            theme.apply(textArea)
        } catch (IOException ignore) {
        }
        RTextScrollPane pane = new RTextScrollPane(textArea)
        panel.add(pane)
    }

    def static getInstance(Project project) {
        if (!INSTANCES.get(project)) {
            synchronized (JsonFormatView.class) {
                if (!INSTANCES.get(project)) {
                    INSTANCES.put(project, new JsonFormatView(project))
                }
            }
        }

        INSTANCES.get(project) as JsonFormatView
    }
}
