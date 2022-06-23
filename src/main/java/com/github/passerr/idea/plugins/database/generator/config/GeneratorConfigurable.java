package com.github.passerr.idea.plugins.database.generator.config;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;
import javax.swing.JPanel;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Objects;

/**
 * mybatis代码生成器配置
 * @author xiehai
 * @date 2022/06/22 16:42
 */
public class GeneratorConfigurable implements SearchableConfigurable, Configurable.NoScroll {
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
        JPanel panel = new JPanel(new GridBagLayout());
        // 数据同步
        panel.add(
            Views.syncView(this.copy),
            new GridBagConstraints(
                0, 0, 1, 1, 1, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                JBUI.insetsBottom(2), 0, 0
            )
        );
        // 模版配置
        panel.add(
            Views.tabsView(this.copy),
            new GridBagConstraints(
                0, 1, 1, 1, 1, 0.6,
                GridBagConstraints.NORTH, GridBagConstraints.BOTH,
                JBUI.insetsBottom(2), 0, 0
            )
        );
        // 描述
        panel.add(
            Views.DESC_VIEW,
            new GridBagConstraints(
                0, 2, 1, 1, 1, 0.4,
                GridBagConstraints.NORTH, GridBagConstraints.BOTH,
                JBUI.insetsBottom(2), 0, 0
            )
        );

        return panel;
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
