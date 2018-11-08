package com.github.passerr.idea.plugins.tool

import com.github.passerr.idea.plugins.tool.json.JsonTextHandler
import com.github.passerr.idea.plugins.tool.log.MybatisLogHandler
import com.intellij.openapi.project.Project
import org.apache.commons.lang.StringUtils
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea
import org.fife.ui.rsyntaxtextarea.Theme
import org.fife.ui.rtextarea.RTextScrollPane

import javax.swing.*
import java.awt.*
import java.awt.event.ActionEvent
import java.awt.event.InputEvent
import java.awt.event.KeyEvent

/**
 * 格式化视图
 * @author xiehai1
 * @date 2018/10/12 16:02
 * @Copyright tellyes tech. inc. co.,ltd
 */
class TextFormatView extends JRootPane {
    Project project
    /**
     * 转换类型
     */
    ConvertType convertType
    /**
     * cache instance by project
     */
    def private static INSTANCES = [:]

    private TextFormatView(Project project) {
        this.project = project
        RSyntaxTextArea textArea = new RSyntaxTextArea()
        this.initMenu(textArea)
        textArea.setCodeFoldingEnabled(true)
        textArea.setAutoIndentEnabled(true)
        textArea.getInputMap(WHEN_FOCUSED)
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
                    switch (TextFormatView.this.convertType) {
                        case ConvertType.JSON:
                            JsonTextHandler.handle(textArea)
                            break
                        case ConvertType.SQL:
                            MybatisLogHandler.handle(textArea)
                            break
                    }
                } finally {
                    TextFormatView.this.getContentPane().repaint()
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
        super.getContentPane().add(new RTextScrollPane(textArea))
    }

    /**
     * 菜单初始化
     */
    private void initMenu(RSyntaxTextArea textArea) {
        // json转换
        def jsonAction = new AbstractAction() {
            @Override
            void actionPerformed(ActionEvent e) {
                TextFormatView.this.convertType = ConvertType.JSON
                textArea.setText("")
                textArea.setSyntaxEditingStyle(ConvertType.JSON.style)
            }
        }
        jsonAction.putValue(AbstractAction.NAME, "Json格式化")
        JRadioButton json = new JRadioButton(jsonAction)
        json.setSelected(true)

        // mybatis日志转sql
        def sqlAction = new AbstractAction() {
            @Override
            void actionPerformed(ActionEvent e) {
                TextFormatView.this.convertType = ConvertType.SQL
                textArea.setText("")
                textArea.setSyntaxEditingStyle(ConvertType.SQL.style)
            }
        }
        sqlAction.putValue(AbstractAction.NAME, "Mybatis日志转Sql")
        JRadioButton sql = new JRadioButton(sqlAction)

        // 按钮组
        ButtonGroup buttonGroup = new ButtonGroup()
        buttonGroup.add(json)
        buttonGroup.add(sql)
        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT))
        radioPanel.add(json)
        radioPanel.add(sql)

        super.getContentPane().add(radioPanel, BorderLayout.NORTH)

        // 默认为为json转换
        this.convertType = ConvertType.JSON
        textArea.setSyntaxEditingStyle(this.convertType.style)
    }

    def static getInstance(Project project) {
        if (!INSTANCES.get(project)) {
            synchronized (TextFormatView.class) {
                if (!INSTANCES.get(project)) {
                    INSTANCES.put(project, new TextFormatView(project))
                }
            }
        }

        INSTANCES.get(project) as TextFormatView
    }
}