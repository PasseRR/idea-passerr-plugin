package com.github.passerr.idea.plugins.tool

import com.intellij.openapi.project.Project
import org.apache.commons.lang.StringUtils
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea
import org.fife.ui.rsyntaxtextarea.Theme
import org.fife.ui.rtextarea.RTextScrollPane

import javax.swing.*
import java.awt.*
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.InputEvent
import java.awt.event.ItemEvent
import java.awt.event.ItemListener
import java.awt.event.KeyEvent

/**
 * 格式化视图
 * @author xiehai1* @Copyright tellyes tech. inc. co.,ltd
 * @date 2018/10/12 16:02
 */
class TextFormatView extends JRootPane {
    Project project
    /**
     * 菜单
     */
    ToolMenu menu
    /**
     * 输入文本域
     */
    RSyntaxTextArea inputTextArea = new RSyntaxTextArea(19, 0)
    /**
     * 输出文本域
     */
    RSyntaxTextArea outputTextArea = new RSyntaxTextArea(34, 0)
    /**
     * cache instance by project
     */
    private static Map<Project, TextFormatView> INSTANCES = new HashMap<>()

    private TextFormatView(Project project) {
        this.project = project
        super.getContentPane().setLayout(new BoxLayout(super.getContentPane(), BoxLayout.Y_AXIS))
        // 初始化输入文本域
        this.doInitInputTextArea()
        // 初始化菜单
        this.doInitMenu()
        // 初始输出化文本域
        this.doInitOutputTextArea()
        // 设置高亮主题
        InputStream inputStream
        try {
            inputStream = this.getClass().getResourceAsStream("/org/fife/ui/rsyntaxtextarea/themes/idea.xml")
            Theme theme = Theme.load(inputStream)
            theme.apply(this.inputTextArea)
            theme.apply(this.outputTextArea)
        } catch (IOException ignore) {
        } finally {
            inputStream && inputStream.close()
        }
    }

    /**
     * 输入文本域初始化
     */
    private void doInitInputTextArea() {
        this.inputTextArea.with {
            // 使用快捷键ctrl+enter格式化json
            getInputMap(WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.CTRL_MASK), "format")
            getActionMap().put("format", new AbstractAction() {
                @Override
                void actionPerformed(ActionEvent e) {
                    TextFormatView.this.doFormat()
                }
            })
        }
        super.getContentPane().add(new RTextScrollPane(this.inputTextArea))
    }

    /**
     * 输出文本域初始化
     */
    private void doInitOutputTextArea() {
        this.outputTextArea.with {
            setCodeFoldingEnabled(true)
            setAutoIndentEnabled(true)
        }
        super.getContentPane().add(new RTextScrollPane(this.outputTextArea))
    }

    /**
     * 菜单初始化
     */
    private void doInitMenu() {
        def subMenuItemListener = new ItemListener() {
            @Override
            void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    switchMenu(e.getItem() as ToolMenu)
                }
            }
        }
        // 编/解码
        JComboBox<ToolMenu> encodeMenu = new JComboBox<>(
            [
                ToolMenu.URL_ENCODE,
                ToolMenu.URL_DECODE
            ] as ToolMenu[])
        encodeMenu.addItemListener(subMenuItemListener)
        encodeMenu.setVisible(false)

        // 加/解密
        JComboBox<ToolMenu> encryptMenu = new JComboBox<>(
            [
                ToolMenu.MD5_ENCRYPTION,
                ToolMenu.BASE64_ENCRYPTION,
                ToolMenu.BASE64_DECRYPTION
            ] as ToolMenu[])
        encryptMenu.addItemListener(subMenuItemListener)
        encryptMenu.setVisible(false)

        // 主菜单
        JComboBox<ToolMenu> mainMenu = new JComboBox<>(
            [
                ToolMenu.JSON,
                ToolMenu.SQL,
                ToolMenu.ENCODE,
                ToolMenu.ENCRYPT
            ] as ToolMenu[])
        mainMenu.addItemListener(new ItemListener() {
            @Override
            void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    ToolMenu toolMenu = e.getItem() as ToolMenu
                    switch (toolMenu) {
                        case ToolMenu.ENCODE:
                            encryptMenu.setVisible(false)
                            encodeMenu.setVisible(true)
                            encodeMenu.setSelectedIndex(0)
                            switchMenu(encodeMenu.getSelectedItem())
                            break
                        case ToolMenu.ENCRYPT:
                            encodeMenu.setVisible(false)
                            encryptMenu.setVisible(true)
                            encryptMenu.setSelectedIndex(0)
                            switchMenu(encryptMenu.getSelectedItem())
                            break
                        default:
                            encodeMenu.setVisible(false)
                            encryptMenu.setVisible(false)
                            // 菜单切换
                            switchMenu(toolMenu)
                            break
                    }
                }
            }
        })
        // 默认选中第一个
        mainMenu.setSelectedItem(0)
        this.switchMenu(mainMenu.getSelectedItem())

        // 转换按钮
        JButton format = new JButton("转换")
        format.addActionListener(new ActionListener() {
            @Override
            void actionPerformed(ActionEvent e) {
                doFormat()
            }
        })
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER))
        panel.add(mainMenu)
        panel.add(encodeMenu)
        panel.add(encryptMenu)
        panel.add(format)

        super.getContentPane().add(panel)
    }

    /**
     * 菜单切换操作
     * @param convertType 转换类型
     */
    private void switchMenu(ToolMenu menu) {
        this.menu = menu
        if (this.inputTextArea) {
            this.inputTextArea.setToolTipSupplier(null)
            this.inputTextArea.removeAllLineHighlights()
            menu.type && this.outputTextArea.setSyntaxEditingStyle(menu.type.style)
        }
    }

    /**
     * 格式化动作
     */
    private void doFormat() {
        // 文本框为空 不进行格式化
        if (StringUtils.isEmpty(this.inputTextArea.getText())) {
            return
        }
        try {
            this.inputTextArea.setToolTipSupplier(null)
            this.inputTextArea.removeAllLineHighlights()
            this.menu.handle(this.inputTextArea, this.outputTextArea)
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