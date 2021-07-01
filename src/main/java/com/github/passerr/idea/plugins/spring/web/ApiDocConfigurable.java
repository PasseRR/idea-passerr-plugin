package com.github.passerr.idea.plugins.spring.web;

import com.github.passerr.idea.plugins.spring.web.po.ApiDocSettingPo;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.Pair;
import com.intellij.ui.TabbedPaneWrapper;
import com.intellij.ui.tabs.JBTabs;
import com.intellij.ui.tabs.TabInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Generated;
import javax.swing.JComponent;
import javax.swing.JPanel;
import java.util.List;
import java.util.Objects;

/**
 * api文档配置组件
 * @author xiehai
 * @date 2021/06/30 19:37
 * @Copyright(c) tellyes tech. inc. co.,ltd
 */
public class ApiDocConfigurable implements SearchableConfigurable, Configurable.NoScroll {
    Disposable disposable;
    ApiDocSettingPo source;
    ApiDocSettingPo copy;

    ApiDocConfigurable() {
        this.source = ApiDocStateComponent.getInstance().getState();
        assert this.source != null;
        this.copy = ApiDocSettingPo.deepCopy(this.source);
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
        this.disposable = Disposer.newDisposable();
        TabbedPaneWrapper tabbedPanel = new TabbedPaneWrapper(disposable);
        List<Pair<String, JPanel>> panels = ApiDocConfigViews.panels(this.copy);
        panels.forEach(it -> tabbedPanel.addTab(it.getFirst(), it.getSecond()));

        tabbedPanel.addChangeListener(e -> {
            TabInfo selectedInfo = ((JBTabs) e.getSource()).getSelectedInfo();
            if (Objects.isNull(selectedInfo)) {
                return;
            }
            // 切换tab自动保存
            if (this.isModified()) {
                this.apply();
            }
            String tab = selectedInfo.getText();
            panels.stream()
                .filter(it -> Objects.equals(it.getFirst(), tab))
                .findFirst()
                .ifPresent(it -> it.getSecond().repaint());
        });

        return tabbedPanel.getComponent();
    }

    @Override
    public boolean isModified() {
        return !Objects.equals(this.source, this.copy);
    }

    @Override
    public void apply() {
        this.source.update(this.copy);
    }

    @Override
    public void reset() {
        if (this.isModified()) {
            this.copy = ApiDocSettingPo.deepCopy(this.source);
        }
    }

    @Generated({})
    @Override
    public void disposeUIResources() {
        if (this.disposable != null) {
            Disposer.dispose(this.disposable);
            this.disposable = null;
        }
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
