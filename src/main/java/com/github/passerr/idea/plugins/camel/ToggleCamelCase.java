package com.github.passerr.idea.plugins.camel;

import com.intellij.codeInsight.actions.MultiCaretCodeInsightAction;
import com.intellij.codeInsight.actions.MultiCaretCodeInsightActionHandler;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.Result;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorModificationUtil;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.IntStream;

/**
 * 驼峰命名切换
 * @author xiehai1
 * @date 2018/10/12 12:31
 * @Copyright tellyes tech. inc. co.,ltd
 */
public class ToggleCamelCase extends MultiCaretCodeInsightAction {
    private static final String UNDERSCORE = "_";

    @Override
    @NotNull
    protected MultiCaretCodeInsightActionHandler getHandler() {
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
                    assert Objects.nonNull(text);

                    String newText;
                    if (Objects.equals(text, text.toLowerCase()) && text.contains(UNDERSCORE)) {
                        // snake_case to SNAKE_CASE
                        newText = text.toUpperCase();
                    } else if (Objects.equals(text, text.toUpperCase()) && text.contains(UNDERSCORE)) {
                        // SNAKE_CASE to SnakeCase
                        newText = toCamelCase(text.toLowerCase());
                    } else if (!Objects.equals(text, text.toUpperCase())
                        && text.substring(0, 1).equals(text.substring(0, 1).toUpperCase())
                        && !text.contains(UNDERSCORE)) {
                        // CamelCase to camelCase
                        newText = text.substring(0, 1).toLowerCase() + text.substring(1);
                    } else {
                        // camelCase to snake_case
                        newText = toSnakeCase(text);
                    }

                    ApplicationManager.getApplication().runWriteAction(() ->
                        CommandProcessor.getInstance().executeCommand(
                            project,
                            () ->
                                new WriteAction<Object>() {
                                    @Override
                                    protected void run(@NotNull Result<Object> result) {
                                        int start = editor.getSelectionModel().getSelectionStart();
                                        EditorModificationUtil.insertStringAtCaret(editor, newText);
                                        editor.getSelectionModel().setSelection(start, start + newText.length());
                                    }
                                }.execute().throwException()
                            ,
                            "CamelCase",
                            ActionGroup.EMPTY_GROUP
                        )
                    );
                }
            };
    }

    /**
     * camelCase to snake_case
     * @param text camelCase
     * @return snake_case
     */
    private static String toSnakeCase(String text) {
        StringBuilder result = new StringBuilder().append(Character.toLowerCase(text.charAt(0)));
        IntStream.range(1, text.length())
            .forEach(i -> {
                char c = text.charAt(i);
                if (Character.isUpperCase(c)) {
                    result.append(UNDERSCORE).append(Character.toLowerCase(c));
                } else {
                    result.append(c);
                }
            });

        return result.toString();
    }

    /**
     * SNAKE_CASE to SnakeCase
     * @param text SNAKE_CASE
     * @return SnakeCase
     */
    private static String toCamelCase(String text) {
        StringBuilder result = new StringBuilder();
        Arrays.stream(text.split(UNDERSCORE))
            .forEach(token -> {
                if (token.length() >= 1) {
                    result.append(token.substring(0, 1).toUpperCase())
                        .append(token.substring(1));
                } else {
                    result.append(UNDERSCORE);
                }
            });

        return result.toString();
    }
}

