package com.github.passerr.idea.plugins.database.generator.config;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.util.Disposer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Generated;
import javax.swing.JComponent;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.util.Objects;

/**
 * mybatis代码生成器配置
 * @author xiehai
 * @date 2022/06/22 16:42
 */
public class GeneratorConfigurable implements SearchableConfigurable, Configurable.NoScroll {
    Disposable disposable;
    ConfigPo src;
    ConfigPo copy;

    GeneratorConfigurable() {
        this.src = GeneratorStateComponent.getInstance().getState();
        Objects.requireNonNull(this.src);
        this.copy = this.src.deepCopy();
    }

    @Override
    public @NotNull String getId() {
        return Objects.requireNonNull(this.getHelpTopic());
    }

    @Override
    public String getDisplayName() {
        return "Generator Setting";
    }

    @Override
    public @Nullable String getHelpTopic() {
        return "generator";
    }

    @Override
    public @Nullable JComponent createComponent() {
        this.disposable = Disposer.newDisposable();
        JPanel panel = new JPanel(new BorderLayout());
        // 数据同步
        panel.add(Views.syncView(this.copy), BorderLayout.NORTH);
        // 模版配置
        panel.add(Views.tabsView(this.disposable, this.copy), BorderLayout.CENTER);
        // 描述
        panel.add(Views.DESC_VIEW, BorderLayout.SOUTH);

        return panel;
    }

    @Override
    @Generated({})
    public void disposeUIResources() {
        if (Objects.nonNull(this.disposable)) {
            Disposer.dispose(this.disposable);
            this.disposable = null;
        }
    }

    @Override
    public boolean isModified() {
        return !Objects.equals(this.src, this.copy);
    }

    @Override
    public void reset() {
        this.copy.from(this.src);
    }

    @Override
    public void apply() {
        this.src.from(this.copy);
    }
}
