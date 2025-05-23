package com.github.passerr.idea.plugins.tip;

import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.projectView.ProjectViewNode;
import com.intellij.ide.projectView.ProjectViewNodeDecorator;

/**
 * 项目目录视图
 * @author xiehai
 * @date 2024/10/23 14:49
 */
public class PackageTipNodeDecorator implements ProjectViewNodeDecorator {
    @Override
    public void decorate(ProjectViewNode<?> node, PresentationData data) {
        Tips.tip(node.getProject(), node, node.getVirtualFile());
    }
}
