package com.github.passerr.idea.plugins;

import com.intellij.ui.PanelWithButtons;

/**
 * {@link PanelWithButtons}重写
 * @author xiehai
 * @date 2021/06/30 19:42
 * @Copyright(c) tellyes tech. inc. co.,ltd
 * @see PanelWithButtons
 */
public abstract class IdeaPanelWithButtons extends PanelWithButtons {
    public IdeaPanelWithButtons() {
        super();
        super.initPanel();
    }
}
