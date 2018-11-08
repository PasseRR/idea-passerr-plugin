package com.github.passerr.idea.plugins.tool

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import com.intellij.ui.content.ContentManager
import org.jetbrains.annotations.NotNull

import javax.swing.*

/**
 * 字符串处理窗口
 * @author xiehai1
 * @date 2018/10/12 15:55
 * @Copyright tellyes tech. inc. co.,ltd
 */
class TextHandlerToolWindow implements ToolWindowFactory {
    @Override
    void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        JComponent component = TextFormatView.getInstance(project)
        ContentManager contentManager = toolWindow.getContentManager()
        ContentFactory contentFactory = contentManager.getFactory()
        contentManager.removeAllContents(true)
        contentManager.addContent(contentFactory.createContent(component, "", true))
    }
}
