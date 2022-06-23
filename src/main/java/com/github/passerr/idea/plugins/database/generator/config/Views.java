package com.github.passerr.idea.plugins.database.generator.config;

import com.github.passerr.idea.plugins.base.utils.ResourceUtil;
import com.github.passerr.idea.plugins.base.utils.VelocityUtil;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.ui.LabeledComponent;
import com.intellij.ui.BrowserHyperlinkListener;
import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.SeparatorFactory;
import com.intellij.ui.TabbedPaneWrapper;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;
import org.jdesktop.swingx.JXTextField;
import org.jetbrains.annotations.NotNull;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

/**
 * 配置视图
 * @author xiehai
 * @date 2022/06/23 14:45
 */
class Views {
    /**
     * 常量view
     */
    static final Component DESC_VIEW = descView();

    /**
     * 同步模块ui
     * @return {@link Component}
     */
    static Component syncView(ConfigPo copy) {
        JPanel panel = new JPanel(new BorderLayout());
        JXTextField urlField = new JXTextField();
        urlField.setPrompt("配置同步URL");
        urlField.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                copy.setUrl(new StringBuilder(urlField.getText()));
            }
        });
        panel.add(LabeledComponent.create(urlField, "同步URL", BorderLayout.WEST), BorderLayout.CENTER);
        JButton button = new JButton("同步");
        button.addActionListener(e -> {
            // TODO 同步
        });
        panel.add(button, BorderLayout.EAST);

        return panel;
    }

    /**
     * tab配置模块ui
     * @return {@link Component}
     */
    static Component tabsView(Disposable disposable, ConfigPo copy) {
        // tab配置
        TabbedPaneWrapper tabbedPanel = new TabbedPaneWrapper(disposable);
        tabbedPanel.addTab(
            "entity模版",
            VelocityUtil.velocityEditor(copy, ConfigPo::getEntity, s -> copy.setEntity(new StringBuilder(s)))
        );
        tabbedPanel.addTab(
            "mapper模版",
            VelocityUtil.velocityEditor(copy, ConfigPo::getMapper, s -> copy.setMapper(new StringBuilder(s)))
        );
        tabbedPanel.addTab(
            "mapper xml模版",
            VelocityUtil.velocityEditor(copy, ConfigPo::getMapperXml, s -> copy.setMapperXml(new StringBuilder(s)))
        );
        tabbedPanel.addTab(
            "service模版",
            VelocityUtil.velocityEditor(copy, ConfigPo::getService, s -> copy.setService(new StringBuilder(s)))
        );
        tabbedPanel.addTab(
            "serviceImpl模版",
            VelocityUtil.velocityEditor(copy, ConfigPo::getServiceImpl, s -> copy.setServiceImpl(new StringBuilder(s)))
        );
        tabbedPanel.addTab(
            "controller模版",
            VelocityUtil.velocityEditor(copy, ConfigPo::getController, s -> copy.setController(new StringBuilder(s)))
        );

        return tabbedPanel.getComponent();
    }

    /**
     * velocity配置描述ui
     * @return {@link Component}
     */
    private static Component descView() {
        // 描述模块
        JEditorPane desc = new JEditorPane(UIUtil.HTML_MIME, "");
        desc.setEditable(false);
        desc.setEditorKit(new HTMLEditorKit());
        desc.addHyperlinkListener(new BrowserHyperlinkListener());
        desc.setText(ResourceUtil.readAsString("/generator/desc.html"));

        JPanel panel = new JPanel(new GridBagLayout());
        panel.add(
            SeparatorFactory.createSeparator("描述:", null),
            new GridBagConstraints(
                0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                JBUI.insetsBottom(2), 0, 0
            )
        );
        panel.add(
            ScrollPaneFactory.createScrollPane(desc),
            new GridBagConstraints(
                0, 1, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                JBUI.insetsTop(2), 0, 0
            )
        );

        return panel;
    }
}
