package com.github.passerr.idea.plugins.base;

import com.intellij.ui.PanelWithButtons;
import org.jetbrains.annotations.Nullable;

import javax.swing.JButton;

/**
 * {@link PanelWithButtons}重写
 * @author xiehai
 * @date 2021/06/30 19:42
 * @Copyright(c) tellyes tech. inc. co.,ltd
 * @see PanelWithButtons
 */
public abstract class IdeaPanelWithButtons extends PanelWithButtons {
    String title;

    public IdeaPanelWithButtons(String title) {
        super();
        this.title = title;
        super.initPanel();
    }

    public IdeaPanelWithButtons() {
        this(null);
    }

    @Nullable
    @Override
    protected String getLabelText() {
        return this.title;
    }

    @Override
    protected JButton[] createButtons() {
        return new JButton[0];
    }
}
