package com.github.passerr.idea.plugins.spring.web;

import com.github.passerr.idea.plugins.base.utils.DesktopUtil;
import com.github.passerr.idea.plugins.base.utils.PluginUtil;
import com.github.passerr.idea.plugins.spring.web.po.ApiDocSettingPo;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.util.Pair;
import com.intellij.ui.components.JBTabbedPane;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import java.awt.Component;
import java.util.List;
import java.util.Objects;

/**
 * api文档配置组件
 * @author xiehai
 * @date 2021/06/30 19:37
 * @Copyright(c) tellyes tech. inc. co.,ltd
 */
public class ApiDocConfigurable implements SearchableConfigurable, Configurable.NoScroll {
    ApiDocSettingPo source;
    ApiDocSettingPo copy;

    ApiDocConfigurable() {
        this.source = ApiDocStateComponent.getInstance().getState();
        assert this.source != null;
        this.copy = this.source.deepCopy();
    }

    @Override
    public String getDisplayName() {
        return "Api Doc Setting";
    }

    @Override
    public String getHelpTopic() {
        return "doc";
    }

    @Override
    public JComponent createComponent() {
        JBTabbedPane tabbedPanel = new JBTabbedPane();
        List<Pair<String, JPanel>> panels = ApiDocConfigViews.panels(this.copy);
        panels.forEach(it -> tabbedPanel.addTab(it.getFirst(), it.getSecond()));
        tabbedPanel.addTab("", new JPanel());
        int last = tabbedPanel.getTabCount() - 1;
        JButton button = new JButton("", AllIcons.Actions.Help);
        button.setPreferredSize(JBUI.size(30, 30));
        button.setToolTipText("配置帮助文档");
        button.addActionListener(e -> DesktopUtil.browser(PluginUtil.gitBlobUrl("docs/api-doc.md")));
        tabbedPanel.setTabComponentAt(last, button);
        tabbedPanel.setEnabledAt(last, false);

        tabbedPanel.addChangeListener(e -> {
            int selectedIndex = ((JBTabbedPane) e.getSource()).getSelectedIndex();

            // 切换tab自动保存
            if (this.isModified()) {
                this.apply();
            }
            Component component = tabbedPanel.getTabComponentAt(selectedIndex);
            component.validate();
            component.repaint();
        });

        return tabbedPanel;
    }

    @Override
    public boolean isModified() {
        return !Objects.equals(this.source, this.copy);
    }

    @Override
    public void apply() {
        this.source.shallowCopy(this.copy);
    }

    @Override
    public void reset() {
        this.copy.shallowCopy(this.source);
    }

    @NotNull
    @Override
    public String getId() {
        return Objects.requireNonNull(this.getHelpTopic());
    }

    @Nullable
    @Override
    public Runnable enableSearch(String option) {
        return null;
    }
}
