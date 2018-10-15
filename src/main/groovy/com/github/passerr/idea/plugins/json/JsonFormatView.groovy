package com.github.passerr.idea.plugins.json

import com.google.gson.*
import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.project.Project
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea
import org.fife.ui.rsyntaxtextarea.SyntaxConstants
import org.fife.ui.rsyntaxtextarea.Theme
import org.fife.ui.rtextarea.RTextScrollPane

import javax.swing.*
import java.awt.*
import java.awt.event.ActionEvent
import java.awt.event.InputEvent
import java.awt.event.KeyEvent
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

/**
 * 格式化视图
 * @author xiehai1
 * @date 2018/10/12 16:02
 * @Copyright ( c ) gome inc Gome Co.,LTD
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
            .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.CTRL_MASK), "format")
        textArea.getActionMap().put("format", new AbstractAction() {
            @Override
            void actionPerformed(ActionEvent e) {
                try {
                    textArea.removeAllLineHighlights()
                    JsonParser jsonParser = new JsonParser()
                    JsonObject jsonObject = jsonParser.parse(textArea.getText()).getAsJsonObject()
                    Gson gson = new GsonBuilder().setPrettyPrinting().create()
                    textArea.setText(gson.toJson(jsonObject))
                } catch (JsonSyntaxException ex) {
                    String msg = ex.getMessage()
                    def matcher = ERROR_PATTERN.matcher(msg)
                    if (matcher.find()) {
                        // 行索引从0开始
                        textArea.addLineHighlight((matcher.group(1) as int) - 1, Color.RED)
                    } else {
                        Notification n = new Notification("Json Format", "Json Format", msg, NotificationType.ERROR)
                        Notifications.Bus.notify(n)
                        new Thread() {
                            @Override
                            void run() {
                                TimeUnit.SECONDS.sleep(2)
                                n.expire()
                            }
                        }.start()
                    }
                } finally {
                    JsonFormatView.this.getPanel().repaint()
                }
            }
        })

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
