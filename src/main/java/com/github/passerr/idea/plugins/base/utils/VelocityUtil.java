package com.github.passerr.idea.plugins.base.utils;

import com.github.passerr.idea.plugins.spring.web.highlight.FileTemplateTokenType;
import com.github.passerr.idea.plugins.spring.web.highlight.TemplateHighlighter;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.EditorSettings;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.editor.ex.util.LayerDescriptor;
import com.intellij.openapi.editor.ex.util.LayeredLexerEditorHighlighter;
import com.intellij.openapi.editor.highlighter.EditorHighlighter;
import com.intellij.openapi.editor.highlighter.EditorHighlighterFactory;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.fileTypes.FileTypes;
import com.intellij.openapi.fileTypes.PlainSyntaxHighlighter;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory;
import com.intellij.openapi.project.ex.ProjectManagerEx;
import com.intellij.testFramework.LightVirtualFile;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.jetbrains.java.generate.velocity.VelocityFactory;

import javax.swing.JComponent;
import java.io.StringWriter;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * velocity工具类
 * @author xiehai
 * @date 2021/07/09 11:39
 * @Copyright(c) tellyes tech. inc. co.,ltd
 */
public interface VelocityUtil {
    /**
     * 模版替换
     * @param template 模版
     * @param map      变量
     * @return 替换后文本
     */
    static String format(StringBuilder template, Map<String, Object> map) {
        StringWriter writer = new StringWriter();
        VelocityEngine velocity = VelocityFactory.getVelocityEngine();
        velocity.evaluate(
            new VelocityContext(map),
            writer,
            VelocityUtil.class.getName(),
            template.toString()
        );

        return writer.toString();
    }

    /**
     * 创建velocity的编辑组件
     * @param value          值来源
     * @param init           编辑器初始化文本
     * @param updateConsumer 编辑器文本更新监听
     * @param <T>            值类型
     * @return {@link JComponent}
     */
    static <T> JComponent velocityEditor(T value, Function<T, CharSequence> init, Consumer<String> updateConsumer) {
        // 编辑模块
        EditorFactory editorFactory = EditorFactory.getInstance();
        Document document = editorFactory.createDocument(init.apply(value));
        document.addDocumentListener(new DocumentListener() {
            @Override
            public void documentChanged(DocumentEvent e) {
                updateConsumer.accept(e.getDocument().getText());
            }
        });
        Editor editor = editorFactory.createEditor(document);
        EditorSettings editorSettings = editor.getSettings();
        editorSettings.setVirtualSpace(false);
        editorSettings.setLineMarkerAreaShown(false);
        editorSettings.setIndentGuidesShown(true);
        editorSettings.setLineNumbersShown(true);
        editorSettings.setFoldingOutlineShown(false);
        editorSettings.setAdditionalColumnsCount(3);
        editorSettings.setAdditionalLinesCount(3);
        editorSettings.setCaretRowShown(false);
        ((EditorEx) editor).setHighlighter(createVelocityHighlight());

        return editor.getComponent();
    }

    /**
     * velocity高亮
     * @return {@link EditorHighlighter}
     */
    static EditorHighlighter createVelocityHighlight() {
        FileType ft = FileTypeManager.getInstance().getFileTypeByExtension("ft");
        if (ft != FileTypes.UNKNOWN) {
            return
                EditorHighlighterFactory.getInstance()
                    .createEditorHighlighter(
                        ProjectManagerEx.getInstance().getDefaultProject(),
                        new LightVirtualFile("aaa.psr.spring.web.ft")
                    );
        }

        SyntaxHighlighter ohl =
            Optional.ofNullable(SyntaxHighlighterFactory.getSyntaxHighlighter(FileTypes.PLAIN_TEXT, null, null))
                .orElseGet(PlainSyntaxHighlighter::new);
        final EditorColorsScheme scheme = EditorColorsManager.getInstance().getGlobalScheme();
        LayeredLexerEditorHighlighter highlighter =
            new LayeredLexerEditorHighlighter(new TemplateHighlighter(), scheme);
        highlighter.registerLayer(FileTemplateTokenType.TEXT, new LayerDescriptor(ohl, ""));

        return highlighter;
    }
}
