package com.github.passerr.idea.plugins.spring.web;

import com.github.passerr.idea.plugins.BaseTableModel;
import com.github.passerr.idea.plugins.IdeaJbTable;
import com.github.passerr.idea.plugins.IdeaPanelWithButtons;
import com.github.passerr.idea.plugins.spring.web.highlight.FileTemplateTokenType;
import com.github.passerr.idea.plugins.spring.web.highlight.TemplateHighlighter;
import com.github.passerr.idea.plugins.spring.web.po.ApiDocSettingPo;
import com.google.common.io.CharStreams;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.EditorSettings;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.event.DocumentAdapter;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.editor.ex.util.LayerDescriptor;
import com.intellij.openapi.editor.ex.util.LayeredLexerEditorHighlighter;
import com.intellij.openapi.fileTypes.FileTypes;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory;
import com.intellij.openapi.util.Pair;
import com.intellij.ui.BrowserHyperlinkListener;
import com.intellij.ui.PanelWithButtons;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.SeparatorFactory;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.table.JBTable;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;

import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * api文档配置视图
 * @author xiehai
 * @date 2021/06/30 19:39
 * @Copyright(c) tellyes tech. inc. co.,ltd
 */
public abstract class ApiDocConfigViews {
    /**
     * 根据已有配置生成视图
     * @param setting {@link ApiDocSettingPo}
     * @return {@link List}
     */
    public static List<Pair<String, JPanel>> panels(ApiDocSettingPo setting) {
        return
            Arrays.asList(
                Pair.pair("Api模版", apiTemplatePanel(setting)),
                Pair.pair("查询参数", queryParamPanel(setting))
            );
    }

    /**
     * api模版视图
     * @param setting {@link ApiDocSettingPo}
     * @return {@link JPanel}
     */
    private static JPanel apiTemplatePanel(ApiDocSettingPo setting) {
        JPanel panel = new JPanel(new GridBagLayout());

        // 编辑模块
        EditorFactory editorFactory = EditorFactory.getInstance();
        Document document = editorFactory.createDocument(setting.getTemplate());
        document.addDocumentListener(new DocumentAdapter() {
            @Override
            public void documentChanged(DocumentEvent e) {
                setting.setStringTemplate(e.getDocument().getText());
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
        SyntaxHighlighter ohl = SyntaxHighlighterFactory.getSyntaxHighlighter(FileTypes.PLAIN_TEXT, null, null);
        final EditorColorsScheme scheme = EditorColorsManager.getInstance().getGlobalScheme();
        LayeredLexerEditorHighlighter highlighter =
            new LayeredLexerEditorHighlighter(new TemplateHighlighter(), scheme);
        highlighter.registerLayer(FileTemplateTokenType.TEXT, new LayerDescriptor(ohl, ""));
        ((EditorEx) editor).setHighlighter(highlighter);
        JPanel templatePanel = new JPanel(new GridBagLayout());
        templatePanel.add(
            SeparatorFactory.createSeparator("模版:", null),
            new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                JBUI.insetsBottom(2), 0, 0
            )
        );
        templatePanel.add(
            editor.getComponent(),
            new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                JBUI.insetsTop(2), 0, 0
            )
        );
        panel.add(
            templatePanel,
            new GridBagConstraints(
                0, 0, 1, 1, 1, 1,
                GridBagConstraints.NORTH,
                GridBagConstraints.BOTH,
                JBUI.emptyInsets(), 0, 0
            )
        );

        // 描述模块
        JEditorPane desc = new JEditorPane(UIUtil.HTML_MIME, "");
        desc.setEditable(false);
        desc.addHyperlinkListener(new BrowserHyperlinkListener());
        try (InputStream resourceAsStream = ApiDocConfigViews.class.getResourceAsStream("/api-doc-desc.html")) {
            desc.setText(CharStreams.toString(new InputStreamReader(resourceAsStream, StandardCharsets.UTF_8)));
        } catch (IOException ignore) {
        }
        desc.setCaretPosition(0);

        JPanel descriptionPanel = new JPanel(new GridBagLayout());
        descriptionPanel.add(
            SeparatorFactory.createSeparator("描述:", null),
            new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                JBUI.insetsBottom(2), 0, 0
            )
        );
        descriptionPanel.add(
            ScrollPaneFactory.createScrollPane(desc),
            new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                JBUI.insetsTop(2), 0, 0
            )
        );
        panel.add(
            descriptionPanel,
            new GridBagConstraints(
                0, 1, 1, 1, 1, 1,
                GridBagConstraints.NORTH,
                GridBagConstraints.BOTH,
                JBUI.emptyInsets(), 0, 0
            )
        );

        return panel;
    }

    /**
     * 查询参数视图
     * @param setting {@link ApiDocSettingPo}
     * @return {@link JPanel}
     */
    private static JPanel queryParamPanel(ApiDocSettingPo setting) {
        JPanel panel = new JPanel(new GridBagLayout());
        PanelWithButtons top = new IdeaPanelWithButtons("忽略类型") {
            @Override
            protected JComponent createMainComponent() {
                BaseTableModel<String> model = new BaseTableModel<>(
                    Collections.singletonList("类型"), setting.getQueryParamIgnoreTypes());
                JBTable table = new IdeaJbTable(model);

                return
                    ToolbarDecorator.createDecorator(table)
                        .setAddAction(it -> {})
                        .setAddActionName("新增")
                        .setEditAction(it -> {})
                        .setEditActionName("编辑")
                        .setRemoveAction(it -> model.removeRow(table.getSelectedRow()))
                        .setRemoveActionName("删除")
                        .disableUpDownActions()
                        .createPanel();
            }
        };
        PanelWithButtons bottom = new IdeaPanelWithButtons("忽略注解") {
            @Override
            protected JComponent createMainComponent() {
                BaseTableModel<String> model = new BaseTableModel<>(
                    Collections.singletonList("注解"), setting.getQueryParamIgnoreAnnotations());
                JBTable table = new IdeaJbTable(model);

                return
                    ToolbarDecorator.createDecorator(table)
                        .setAddAction(it -> {})
                        .setAddActionName("新增")
                        .setEditAction(it -> {})
                        .setEditActionName("编辑")
                        .setRemoveAction(it -> model.removeRow(table.getSelectedRow()))
                        .setRemoveActionName("删除")
                        .disableUpDownActions()
                        .createPanel();
            }
        };
        panel.add(
            top,
            new GridBagConstraints(
                0, 0, 1, 1, 1, 1,
                GridBagConstraints.NORTH,
                GridBagConstraints.BOTH,
                JBUI.emptyInsets(), 0, 0
            )
        );
        panel.add(
            bottom,
            new GridBagConstraints(
                0, 1, 1, 1, 1, 1,
                GridBagConstraints.NORTH,
                GridBagConstraints.BOTH,
                JBUI.emptyInsets(), 0, 0
            )
        );

        return panel;
    }
}
