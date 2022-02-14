package com.github.passerr.idea.plugins.naming;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.util.Disposer;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBLabel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 命名配置
 * @author xiehai
 * @date 2022/02/14 16:42
 */
public class NamingConfigurable implements SearchableConfigurable, Configurable.NoScroll {
    Disposable disposable;
    final NamingStateComponent.NamingState state;
    int value;
    List<JBCheckBox> list = new ArrayList<>();

    NamingConfigurable() {
        this.state = NamingStateComponent.getInstance().getState();
        Objects.requireNonNull(this.state);
        this.value = this.state.getState();
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
        this.disposable = Disposer.newDisposable();
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(new JBLabel("勾选需要的命名方式"));

        for (NamingStyle it : NamingStyle.values()) {
            if (it.bit > 0) {
                JBCheckBox checkBox = new JBCheckBox(it.name, (this.value & it.bit) > 0);
                checkBox.addItemListener(e -> {
                    JBCheckBox box = (JBCheckBox) e.getSource();
                    selected(it.bit, box.isSelected());
                });
                panel.add(checkBox);
                list.add(checkBox);
            }
        }

        return panel;
    }

    protected void selected(int value, boolean isSelected) {
        this.value = isSelected ? (this.value | value): (~value & this.value);
    }

    @Override
    public boolean isModified() {
        return
            // 至少需要2种以上的切换方式
            this.list.stream().filter(JBCheckBox::isSelected).count() > 1
                && !Objects.equals(this.value, this.state.getState());
    }

    @Override
    public void apply() {
        this.state.setState(this.value);
    }
}
