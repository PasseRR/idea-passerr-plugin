package com.github.passerr.idea.plugins.spring.web;

import com.github.passerr.idea.plugins.BaseTableModel;
import com.github.passerr.idea.plugins.IdeaJbTable;
import com.github.passerr.idea.plugins.IdeaPanelWithButtons;
import com.github.passerr.idea.plugins.spring.web.po.ApiDocSettingPo;
import com.google.common.io.CharStreams;
import com.intellij.codeInsight.template.TemplateContextType;
import com.intellij.codeInsight.template.impl.TemplateEditorUtil;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.EditorSettings;
import com.intellij.openapi.editor.event.DocumentAdapter;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.util.Pair;
import com.intellij.ui.BrowserHyperlinkListener;
import com.intellij.ui.PanelWithButtons;
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
        EditorFactory editorFactory = EditorFactory.getInstance();
        Document document = editorFactory.createDocument(setting.getTemplate());
        document.addDocumentListener(new DocumentAdapter() {
            @Override
            public void documentChanged(DocumentEvent e) {
                setting.setTemplate(e.getDocument().getText());
            }
        });
        Editor editor = editorFactory.createEditor(document);
        TemplateEditorUtil.setHighlighter(editor, (TemplateContextType) null);
        EditorSettings editorSettings = editor.getSettings();
        editorSettings.setVirtualSpace(false);
        editorSettings.setLineMarkerAreaShown(false);
        editorSettings.setIndentGuidesShown(true);
        editorSettings.setLineNumbersShown(true);
        editorSettings.setFoldingOutlineShown(false);
        editorSettings.setAdditionalColumnsCount(3);
        editorSettings.setAdditionalLinesCount(3);
        editorSettings.setCaretRowShown(false);

        JEditorPane desc = new JEditorPane(UIUtil.HTML_MIME, "");
        desc.setEditable(false);
        desc.addHyperlinkListener(new BrowserHyperlinkListener());
        try (InputStream resourceAsStream = ApiDocConfigViews.class.getResourceAsStream("/api-doc-desc.html")) {
            desc.setText(CharStreams.toString(new InputStreamReader(resourceAsStream, StandardCharsets.UTF_8)));
        } catch (IOException ignore) {
        }
        desc.setCaretPosition(0);

        panel.add(
            editor.getComponent(),
            new GridBagConstraints(
                0, 0, 1, 1, 1, 1,
                GridBagConstraints.NORTH,
                GridBagConstraints.BOTH,
                JBUI.emptyInsets(), 0, 0
            )
        );

        panel.add(
            desc,
            new GridBagConstraints(
                0, 1, 1, 1, 1, 1,
                GridBagConstraints.NORTH,
                GridBagConstraints.BOTH,
                JBUI.emptyInsets(), 0, 0
            )
        );

        // 编辑模块
        // 描述模块
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
