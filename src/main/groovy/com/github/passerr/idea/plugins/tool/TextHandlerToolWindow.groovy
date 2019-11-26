package com.github.passerr.idea.plugins.tool

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import org.jetbrains.annotations.NotNull

/**
 * 字符串处理窗口
 * @author xiehai1* @date 2018/10/12 15:55
 * @Copyright tellyes tech. inc. co.,ltd
 */
class TextHandlerToolWindow implements ToolWindowFactory {
    @Override
    void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        toolWindow.getContentManager().with {
            removeAllContents(true)
            addContent(getFactory().createContent(TextFormatView.getInstance(project), "", true))
        }
    }
}
