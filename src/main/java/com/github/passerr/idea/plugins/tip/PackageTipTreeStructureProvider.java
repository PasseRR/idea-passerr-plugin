package com.github.passerr.idea.plugins.tip;

import com.intellij.ide.projectView.TreeStructureProvider;
import com.intellij.ide.projectView.ViewSettings;
import com.intellij.ide.projectView.impl.nodes.PsiDirectoryNode;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Objects;

/**
 * 目录备注
 * @author xiehai
 * @date 2024/10/21 16:41
 */
public class PackageTipTreeStructureProvider implements TreeStructureProvider, DumbAware {
    @Override
    public @NotNull Collection<AbstractTreeNode<?>> modify(@NotNull AbstractTreeNode<?> parent,
                                                           @NotNull Collection<AbstractTreeNode<?>> children,
                                                           ViewSettings settings) {

        Project project = parent.getProject();
        if (Objects.isNull(project)) {
            return children;
        }


        if (parent instanceof PsiDirectoryNode) {
            Tips.tip(project, parent, ((PsiDirectoryNode) parent).getVirtualFile());

            children.forEach(it -> {
                if (it instanceof PsiDirectoryNode) {
                    PsiDirectoryNode node = (PsiDirectoryNode) it;
                    VirtualFile file = node.getVirtualFile();
                    if (Objects.isNull(file)) {
                        return;
                    }

                    Tips.tip(project, it, file);
                }
            });
        }

        return children;
    }
}
