package com.github.passerr.idea.plugins.database.generator.config;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.SearchableConfigurable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;
import java.util.Objects;

/**
 * mybatis代码生成器配置
 * @author xiehai
 * @date 2022/06/22 16:42
 */
public class GeneratorConfigurable implements SearchableConfigurable, Configurable.NoScroll {
    Disposable disposable;

    GeneratorConfigurable() {
    }

    @Override
    public @NotNull String getId() {
        return Objects.requireNonNull(this.getHelpTopic());
    }

    @Override
    public String getDisplayName() {
        return "Naming Setting";
    }

    @Override
    public @Nullable String getHelpTopic() {
        return "naming";
    }

    @Override
    public @Nullable JComponent createComponent() {
        return null;
    }

    @Override
    public boolean isModified() {
        return false;
    }

    @Override
    public void apply() {
    }
}
