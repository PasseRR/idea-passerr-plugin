package com.github.passerr.idea.plugins.naming;

import com.intellij.codeInsight.actions.MultiCaretCodeInsightAction;
import com.intellij.codeInsight.actions.MultiCaretCodeInsightActionHandler;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorModificationUtil;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * 命名切换
 * @author xiehai1
 * @date 2018/10/12 12:31
 * @Copyright tellyes tech. inc. co.,ltd
 */
public class NamingAction extends MultiCaretCodeInsightAction {
    @Override
    @NotNull
    protected MultiCaretCodeInsightActionHandler getHandler() {
        NamingStateComponent.NamingState state = NamingStateComponent.getInstance().getState();

        return
            new MultiCaretCodeInsightActionHandler() {
                @Override
                public void invoke(@NotNull Project project, @NotNull Editor editor, @NotNull Caret caret,
                                   @NotNull PsiFile file) {
                    String text = caret.getEditor().getSelectionModel().getSelectedText();
                    if (StringUtils.isEmpty(text)) {
                        editor.getSelectionModel().selectWordAtCaret(true);
                        text = editor.getSelectionModel().getSelectedText();
                    }

                    if (StringUtils.isEmpty(text) || Objects.isNull(state)) {
                        return;
                    }

                    String newText = NamingNode.convert(text, state.getState());

                    ApplicationManager.getApplication().runWriteAction(() ->
                        CommandProcessor.getInstance().executeCommand(
                            project,
                            () ->
                                WriteAction.run(() -> {
                                    int start = editor.getSelectionModel().getSelectionStart();
                                    EditorModificationUtil.insertStringAtCaret(editor, newText);
                                    editor.getSelectionModel().setSelection(start, start + newText.length());
                                })
                            ,
                            "CamelCase",
                            ActionGroup.EMPTY_GROUP
                        )
                    );
                }
            };
    }
}

