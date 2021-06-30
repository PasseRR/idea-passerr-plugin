package com.github.passerr.idea.plugins.spring.web


import com.github.passerr.idea.plugins.spring.web.po.ApiDocSettingPo
import com.intellij.openapi.Disposable
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.options.ConfigurationException
import com.intellij.openapi.util.Disposer
import com.intellij.ui.TabbedPaneWrapper
import com.intellij.ui.tabs.JBTabs

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
    JPanel mainPanel
    Disposable disposable
    ApiDocSettingPo source
    ApiDocSettingPo copy

    ApiDocConfigurable() {
        this.source = ApiDocStateComponent.getInstance().getState()
        this.copy = ApiDocSettingPo.deepCopy(this.source)
    }

    @Override
    String getDisplayName() {
        "Api Doc Setting"
    }

    @Override
    String getHelpTopic() {
        "doc"
    }

    @Override
    JComponent createComponent() {
        this.disposable = Disposer.newDisposable()
        def tabbedPanel = new TabbedPaneWrapper(disposable)
        def panels = ApiDocConfigView.panels(this.copy)
        panels.each { it -> tabbedPanel.addTab(it.first, it.second) }

        tabbedPanel.addChangeListener(new ChangeListener() {
            @Override
            void stateChanged(ChangeEvent e) {
                if (isModified()) {
                    doApply()
                }
                panels.find { p -> p.first == ((JBTabs) e.getSource()).getSelectedInfo().getText() }.second.repaint()
            }
        })


        this.mainPanel = new JPanel(new BorderLayout())
        this.mainPanel.add(tabbedPanel.getComponent(), BorderLayout.NORTH)

        return this.mainPanel
    }

    @Override
    boolean isModified() {
        this.source != this.copy
    }

    @Override
    void apply() throws ConfigurationException {
        this.doApply()
    }

    protected doApply() {
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
