package com.github.passerr.idea.plugins.base.utils;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.ShortcutSet;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.util.Consumer;

import javax.swing.Icon;
import javax.swing.JComponent;

/**
 * {@link DumbAwareAction}工具
 * @author xiehai
 * @date 2025/03/07 10:18
 */
public class DumbAwareActionUtils {
    public static DumbAwareActionBuilder builder(String text, Icon icon, Consumer<? super AnActionEvent> consumer) {
        DumbAwareAction action = DumbAwareAction.create(text, icon, consumer);
        return (ss, c) -> {
            action.registerCustomShortcutSet(ss, c);
            return action;
        };
    }

    public interface DumbAwareActionBuilder {
        DumbAwareAction build(ShortcutSet shortcutSet, JComponent component);
    }
}
