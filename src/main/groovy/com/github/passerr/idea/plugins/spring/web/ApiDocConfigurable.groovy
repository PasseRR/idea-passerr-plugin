package com.github.passerr.idea.plugins.spring.web

import com.github.passerr.idea.plugins.spring.web.po.ApiDocSettingPo
import com.intellij.openapi.Disposable
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.options.ConfigurationException
import com.intellij.openapi.util.Disposer
import com.intellij.ui.TabbedPaneWrapper
import com.intellij.ui.tabs.JBTabs
import groovy.swing.SwingBuilder

import javax.swing.*
import javax.swing.event.ChangeEvent
import javax.swing.event.ChangeListener
import java.awt.*
/**
 * @date 2021/06/29 10:23
 * @Copyright (c) wisewe co.,ltd
 * @author xiehai
 */
class ApiDocConfigurable implements Configurable {
    private JPanel mainPanel
    private JScrollPane contentPanel
    private Disposable disposable
    private ApiDocSettingPo source = ApiDocStateComponent.getInstance().getState()
    private ApiDocSettingPo copy = ApiDocSettingPo.deepCopy(this.source)

    @Override
    String getDisplayName() {
        return "Api Doc Setting"
    }

    @Override
    String getHelpTopic() {
        return "doc"
    }

    @Override
    JComponent createComponent() {
        this.disposable = Disposer.newDisposable()
        def sb = new SwingBuilder()
        def tabbedPanel = new TabbedPaneWrapper(disposable)
        def features = ["Api模版", "忽略类型", "查询参数", "类型别名", "序列化"]
        def featurePanels = [
            sb.panel([layout: new BorderLayout()]) {
                sb.textField(name: "text", text: "Api模版", constraints: BorderLayout.CENTER)
            },
            sb.panel([layout: new BorderLayout()]) {
                sb.textField(name: "text", text: "忽略类型", constraints: BorderLayout.CENTER)
            },
            sb.panel([layout: new BorderLayout()]) {
                sb.textField(name: "text", text: "查询参数", constraints: BorderLayout.CENTER)
            },
            sb.panel([layout: new BorderLayout()]) {
                sb.textField(name: "text", text: "类型别名", constraints: BorderLayout.CENTER)
            },
            sb.panel([layout: new BorderLayout()]) {
                sb.textField(name: "text", text: "序列化", constraints: BorderLayout.CENTER)
            }
        ]
        features.eachWithIndex { it, index ->
            tabbedPanel.addTab(it, featurePanels[index])
        }
        tabbedPanel.addChangeListener(new ChangeListener() {
            @Override
            void stateChanged(ChangeEvent e) {
                int index = features.indexOf(((JBTabs) e.getSource()).getSelectedInfo().getText())
                ApiDocConfigurable.this.with {
                    if (isModified()) {
                        apply()
                    }
                    repaint(featurePanels[index])
                }
            }
        })


        this.mainPanel = new JPanel(new BorderLayout())
        this.contentPanel = new JScrollPane()
        this.mainPanel.add(tabbedPanel.getComponent(), BorderLayout.NORTH)
        this.mainPanel.add(contentPanel, BorderLayout.CENTER)
        this.repaint(featurePanels[tabbedPanel.getSelectedIndex()])

        return this.mainPanel
    }

    void repaint(JComponent component) {
        this.contentPanel.with {
            removeAll()
            add(component)
            repaint()
        }
    }

    @Override
    boolean isModified() {
        this.copy.template = "${System.currentTimeMillis()}"
        this.source != this.copy
    }

    @Override
    void apply() throws ConfigurationException {
        this.source.update(this.copy)
    }

    @Override
    void reset() {
        this.copy = ApiDocSettingPo.deepCopy(this.source)
    }

    @Override
    void disposeUIResources() {
        if (this.disposable) {
            Disposer.dispose(this.disposable)
            this.disposable = null
        }
    }
}
