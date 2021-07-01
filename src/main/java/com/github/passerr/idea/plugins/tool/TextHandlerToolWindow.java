package com.github.passerr.idea.plugins.tool;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.ContentManager;
import org.jetbrains.annotations.NotNull;

/**
 * 字符串处理窗口
 * @author xiehai
 * @date 2021/07/01 15:18
 * @Copyright(c) tellyes tech. inc. co.,ltd
 */
public class TextHandlerToolWindow implements ToolWindowFactory {
    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        ContentManager contentManager = toolWindow.getContentManager();
        contentManager.removeAllContents(true);
        contentManager.addContent(
            contentManager.getFactory().createContent(TextFormatView.getInstance(project), "", true)
        );
    }
}
