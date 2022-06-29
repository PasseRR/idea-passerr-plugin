package com.github.passerr.idea.plugins.database.generator.config;

import com.github.passerr.idea.plugins.base.BaseTableModel;
import com.github.passerr.idea.plugins.base.IdeaDialog;
import com.github.passerr.idea.plugins.base.IdeaJbTable;
import com.github.passerr.idea.plugins.base.IdeaPanelWithButtons;
import com.github.passerr.idea.plugins.base.utils.GsonUtil;
import com.github.passerr.idea.plugins.database.generator.config.po.SyncUtil;
import com.github.passerr.idea.plugins.database.generator.config.po.TemplatePo;
import com.github.passerr.idea.plugins.database.generator.config.po.TemplatesPo;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CustomShortcutSet;
import com.intellij.openapi.actionSystem.ShortcutSet;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.ui.LabeledComponent;
import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.table.JBTable;
import com.intellij.util.ui.JBUI;
import org.apache.commons.lang3.StringUtils;
import org.jdesktop.swingx.JXTextField;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentEvent;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * mybatis代码生成器配置
 * @author xiehai
 * @date 2022/06/22 16:42
 */
public class GeneratorTemplateConfigurable implements SearchableConfigurable, Configurable.NoScroll {
    TemplatesPo src;
    TemplatesPo copy;

    GeneratorTemplateConfigurable() {
        this.src = GeneratorTemplateStateComponent.getInstance().getState();
        this.copy = GsonUtil.deepCopy(this.src, TemplatesPo.class);
    }

    @Override
    public @NotNull String getId() {
        return Objects.requireNonNull(this.getHelpTopic());
    }

    @Override
    public String getDisplayName() {
        return "Generator Templates";
    }

    @Override
    public @Nullable String getHelpTopic() {
        return "generator";
    }

    @Override
    public @Nullable JComponent createComponent() {
        JPanel panel = new JPanel(new GridBagLayout());
        final List<TemplatePo> templates = this.copy.getTemplates();
        IdeaPanelWithButtons top = new IdeaPanelWithButtons("代码模板") {
            @Override
            protected JComponent createMainComponent() {
                BaseTableModel<TemplatePo> model =
                    new BaseTableModel<TemplatePo>(Arrays.asList("模版名称", "同步URL"), templates) {
                        @Override
                        protected List<Function<TemplatePo, Object>> columns() {
                            return Arrays.asList(TemplatePo::getName, TemplatePo::getUrl);
                        }
                    };
                JBTable table = new IdeaJbTable(model);
                // 弹出层构建器
                Function<IdeaDialog<TemplatePo>, JComponent> function = dialog -> {
                    JPanel p = new JPanel();
                    p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
                    TemplatePo value = dialog.getValue();

                    JXTextField name = new JXTextField();
                    name.setText(value.getName());
                    name.setColumns(35);
                    name.getDocument().addDocumentListener(new DocumentAdapter() {
                        @Override
                        protected void textChanged(@NotNull DocumentEvent e) {
                            value.setName(name.getText().trim());
                            dialog.onChange();
                        }
                    });
                    p.add(LabeledComponent.create(name, "模板名称", BorderLayout.WEST));

                    JXTextField url = new JXTextField();
                    url.setText(value.getUrl());
                    url.setColumns(35);
                    url.getDocument().addDocumentListener(new DocumentAdapter() {
                        @Override
                        protected void textChanged(@NotNull DocumentEvent e) {
                            value.setUrl(url.getText().trim());
                            dialog.onChange();
                        }
                    });
                    p.add(LabeledComponent.create(url, "同步URL", BorderLayout.WEST));
                    dialog.onChange();

                    return p;
                };
                return
                    ToolbarDecorator.createDecorator(table)
                        .setAddAction(it ->
                            new IdeaDialog<TemplatePo>(panel)
                                .title("添加代码模板")
                                .value(new TemplatePo())
                                .okAction(t -> {
                                    templates.add(t);
                                    model.fireTableRowsInserted(model.getRowCount() - 1, model.getRowCount() - 1);
                                })
                                .changePredicate(t -> StringUtils.isNotBlank(t.getName()))
                                .componentFunction(function)
                                .doInit()
                                .showAndGet()
                        )
                        .setAddActionName("新增")
                        .setEditAction(it ->
                            new IdeaDialog<TemplatePo>(panel)
                                .title("修改代码模板")
                                .value(model.getRow(table.getSelectedRow()))
                                .okAction(t -> {
                                    templates.set(table.getSelectedRow(), t);
                                    model.fireTableRowsUpdated(table.getSelectedRow(), table.getSelectedRow());
                                })
                                .changePredicate(t -> !t.getName().isEmpty())
                                .componentFunction(function)
                                .doInit()
                                .showAndGet()
                        )
                        .setEditActionName("编辑")
                        .setRemoveAction(it -> model.removeRow(table.getSelectedRow()))
                        .setRemoveActionName("删除")
                        .addExtraAction(
                            new ToolbarDecorator.ElementActionButton("模板设置", AllIcons.General.Settings) {
                                @Override
                                public void actionPerformed(@NotNull AnActionEvent e) {
                                    new TemplateSettingDialog(copy.getTemplates().get(table.getSelectedRow())).show();
                                }

                                @Override
                                public ShortcutSet getShortcut() {
                                    return
                                        new CustomShortcutSet(
                                            KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.ALT_MASK)
                                        );
                                }
                            }
                        )
                        .addExtraActions(
                            new ToolbarDecorator.ElementActionButton("同步", AllIcons.Actions.Refresh) {
                                @Override
                                public void actionPerformed(@NotNull AnActionEvent e) {
                                    SyncUtil.sync(copy.getTemplates().get(table.getSelectedRow()));
                                }

                                @Override
                                public ShortcutSet getShortcut() {
                                    return new CustomShortcutSet(KeyEvent.VK_F5);
                                }
                            }
                        )
                        .setMoveUpAction(it -> {
                            int index = table.getSelectedRow(), next = index - 1;
                            TemplatePo t = templates.get(index);
                            templates.set(index, templates.get(next));
                            templates.set(next, t);
                            table.setRowSelectionInterval(next, next);
                            model.fireTableRowsUpdated(next, index);
                        })
                        .setMoveUpActionName("上移")
                        .setMoveDownAction(it -> {
                            int index = table.getSelectedRow(), next = index + 1;
                            TemplatePo t = templates.get(index);
                            templates.set(index, templates.get(next));
                            templates.set(next, t);
                            table.setRowSelectionInterval(next, next);
                            model.fireTableRowsUpdated(index, next);
                        })
                        .setMoveUpActionName("下移")
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

        return panel;
    }

    @Override
    public boolean isModified() {
        return !Objects.equals(this.src, this.copy);
    }

    @Override
    public void reset() {
        this.copy.from(this.src);
    }

    @Override
    public void apply() {
        this.src.from(this.copy);
    }
}
