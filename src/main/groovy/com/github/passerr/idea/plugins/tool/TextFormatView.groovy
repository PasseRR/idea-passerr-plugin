package com.github.passerr.idea.plugins.tool

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
 * @author xiehai1* @Copyright tellyes tech. inc. co.,ltd
 * @date 2018/10/12 16:02
 */
class TextFormatView extends JRootPane {
    Project project
    /**
     * 转换类型
     */
    ConvertType convertType
    /**
     * 文本域
     */
    RSyntaxTextArea textArea
    /**
     * cache instance by project
     */
    private static Map<Project, TextFormatView> INSTANCES = new HashMap<>()

    private TextFormatView(Project project) {
        this.project = project
        this.doInitTextArea()
        this.doInitMenu()
    }

    /**
     * 文本域初始化
     */
    private void doInitTextArea() {
        this.textArea = new RSyntaxTextArea()
        this.textArea.with {
            setCodeFoldingEnabled(true)
            setAutoIndentEnabled(true)
            // 使用快捷键ctrl+enter格式化json
            getInputMap(WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.CTRL_MASK), "format")
            getActionMap().put("format", new AbstractAction() {
                @Override
                void actionPerformed(ActionEvent e) {
                    TextFormatView.this.doFormat()
                }
            })
        }

        // 设置高亮主题
        InputStream inputStream = this.getClass().getResourceAsStream("/org/fife/ui/rsyntaxtextarea/themes/idea.xml")
        try {
            Theme theme = Theme.load(inputStream)
            theme.apply(this.textArea)
        } catch (IOException ignore) {
        }
        super.getContentPane().add(new RTextScrollPane(this.textArea))
    }

    /**
     * 菜单初始化
     */
    private void doInitMenu() {
        // json转换
        JRadioButton json = new JRadioButton()
        json.with {
            setAction(new AbstractAction() {
                {
                    this.putValue(NAME, "Json格式化")
                }

                @Override
                void actionPerformed(ActionEvent e) {
                    TextFormatView.this.switchConvertType(ConvertType.JSON)
                }
            })
            setSelected(true)
            // 默认选中json格式化
            this.switchConvertType(ConvertType.JSON)
        }

        // mybatis日志转sql
        JRadioButton sql = new JRadioButton()
        sql.with {
            setAction(new AbstractAction() {
                {
                    this.putValue(NAME, "Mybatis日志转Sql")
                }

                @Override
                void actionPerformed(ActionEvent e) {
                    TextFormatView.this.switchConvertType(ConvertType.SQL)
                }
            })
        }

        // 按钮组
        ButtonGroup buttonGroup = new ButtonGroup()
        buttonGroup.add(json)
        buttonGroup.add(sql)
        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT))
        radioPanel.add(json)
        radioPanel.add(sql)

        super.getContentPane().add(radioPanel, BorderLayout.NORTH)
    }

    /**
     * 菜单切换操作
     * @param convertType 转换类型
     */
    private void switchConvertType(ConvertType convertType) {
        this.convertType = convertType
        if (this.textArea) {
            this.textArea.setText("")
            this.textArea.setSyntaxEditingStyle(convertType.style)
        }
    }

    /**
     * 格式化动作
     */
    private void doFormat() {
        // 文本框为空 不进行格式化
        if (StringUtils.isEmpty(this.textArea.getText())) {
            return
        }
        try {
            textArea.setToolTipSupplier(null)
            textArea.removeAllLineHighlights()
            this.convertType.handle(textArea)
        } finally {
            this.getContentPane().repaint()
        }
    }

    /**
     * 按照{@link Project}单例
     * @param project idea 项目
     * @return {@link TextFormatView}
     */
    static TextFormatView getInstance(Project project) {
        if (!INSTANCES.containsKey(project)) {
            synchronized (TextFormatView.class) {
                if (!INSTANCES.containsKey(project)) {
                    INSTANCES.putIfAbsent(project, new TextFormatView(project))
                }
            }
        }

        return INSTANCES.get(project)
    }
}