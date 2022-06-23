package com.github.passerr.idea.plugins.spring.web;

import com.github.passerr.idea.plugins.base.BaseTableModel;
import com.github.passerr.idea.plugins.base.IdeaDialog;
import com.github.passerr.idea.plugins.base.IdeaJbTable;
import com.github.passerr.idea.plugins.base.IdeaPanelWithButtons;
import com.github.passerr.idea.plugins.base.utils.ResourceUtil;
import com.github.passerr.idea.plugins.base.utils.VelocityUtil;
import com.github.passerr.idea.plugins.spring.web.po.ApiDocObjectSerialPo;
import com.github.passerr.idea.plugins.spring.web.po.ApiDocSettingPo;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.util.Pair;
import com.intellij.ui.BrowserHyperlinkListener;
import com.intellij.ui.PanelWithButtons;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.SeparatorFactory;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.table.JBTable;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ItemEvent;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

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
                Pair.pair("查询参数", queryParamPanel(setting)),
                Pair.pair("报文体", bodyParamPanel(setting)),
                Pair.pair("序列化", serialPanel(setting))
            );
    }

    /**
     * api模版视图
     * @param setting {@link ApiDocSettingPo}
     * @return {@link JPanel}
     */
    private static JPanel apiTemplatePanel(ApiDocSettingPo setting) {
        JPanel panel = new JPanel(new GridBagLayout());

        JPanel templatePanel = new JPanel(new GridBagLayout());
        templatePanel.add(
            SeparatorFactory.createSeparator("模版:", null),
            new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                JBUI.insetsBottom(2), 0, 0
            )
        );
        templatePanel.add(
            VelocityUtil.velocityEditor(setting, ApiDocSettingPo::getTemplate, setting::setStringTemplate),
            new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                JBUI.insetsTop(2), 0, 0
            )
        );
        panel.add(
            templatePanel,
            new GridBagConstraints(0, 0, 1, 1, 1, 0.65, GridBagConstraints.NORTH, GridBagConstraints.BOTH,
                JBUI.emptyInsets(), 0, 0
            )
        );

        // 描述模块
        JEditorPane desc = new JEditorPane(UIUtil.HTML_MIME, "");
        desc.setEditable(false);
        desc.setEditorKit(new HTMLEditorKit());
        desc.addHyperlinkListener(new BrowserHyperlinkListener());
        desc.setText(ResourceUtil.readAsString("/api-doc/desc.html"));
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
            new GridBagConstraints(0, 1, 1, 1, 1, 0.35, GridBagConstraints.NORTH, GridBagConstraints.BOTH,
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
        PanelWithButtons top = new IdeaPanelWithButtons("忽略类型:") {
            @Override
            protected JComponent createMainComponent() {
                BaseTableModel<String> model = new BaseTableModel<>(
                    Collections.singletonList("类型"), setting.getQueryParamIgnoreTypes());
                JBTable table = new IdeaJbTable(model);
                // 弹出层构建器
                BiFunction<StringBuilder, Runnable, JComponent> function = (s, r) -> {
                    JPanel p = new JPanel(new GridBagLayout());
                    GridBagConstraints gb = new GridBagConstraints(0, 0, 1, 1, 0, 0,
                        GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                        JBUI.insets(0, 0, 5, 10), 0, 0
                    );
                    JLabel typeLabel = new JLabel("类型");
                    p.add(typeLabel, gb);
                    JTextField textField = new JTextField(s.toString());
                    textField.getDocument()
                        .addDocumentListener(
                            new com.intellij.ui.DocumentAdapter() {
                                @Override
                                protected void textChanged(javax.swing.event.DocumentEvent e) {
                                    s.setLength(0);
                                    s.append(textField.getText());
                                    r.run();
                                }
                            });
                    Dimension oldPreferredSize = textField.getPreferredSize();
                    textField.setPreferredSize(new Dimension(300, oldPreferredSize.height));
                    gb.gridx = 1;
                    gb.gridwidth = GridBagConstraints.REMAINDER;
                    gb.weightx = 1;
                    p.add(textField, gb);
                    r.run();

                    return p;
                };
                return
                    ToolbarDecorator.createDecorator(table)
                        .setAddAction(it ->
                            new IdeaDialog<StringBuilder>(panel)
                                .title("新增忽略类型")
                                .value(new StringBuilder())
                                .okAction(t -> setting.getQueryParamIgnoreTypes().add(t.toString()))
                                .changePredicate(t -> t.length() > 0)
                                .componentFunction(t -> function.apply(t.getValue(), t::onChange))
                                .doInit()
                                .showAndGet()
                        )
                        .setAddActionName("新增")
                        .setEditAction(it ->
                            new IdeaDialog<StringBuilder>(panel)
                                .title("编辑忽略类型")
                                .value(new StringBuilder(model.getRow(table.getSelectedRow())))
                                .okAction(t ->
                                    setting.getQueryParamIgnoreTypes().set(table.getSelectedRow(), t.toString())
                                )
                                .changePredicate(t -> t.length() > 0)
                                .componentFunction(t -> function.apply(t.getValue(), t::onChange))
                                .doInit()
                                .showAndGet()
                        )
                        .setEditActionName("编辑")
                        .setRemoveAction(it -> model.removeRow(table.getSelectedRow()))
                        .setRemoveActionName("删除")
                        .disableUpDownActions()
                        .createPanel();
            }
        };
        PanelWithButtons bottom = new IdeaPanelWithButtons("忽略注解:") {
            @Override
            protected JComponent createMainComponent() {
                BaseTableModel<String> model = new BaseTableModel<>(
                    Collections.singletonList("注解"), setting.getQueryParamIgnoreAnnotations());
                JBTable table = new IdeaJbTable(model);
                // 弹出层构建器
                BiFunction<StringBuilder, Runnable, JComponent> function = (s, r) -> {
                    JPanel p = new JPanel(new GridBagLayout());
                    GridBagConstraints gb = new GridBagConstraints(0, 0, 1, 1, 0, 0,
                        GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                        JBUI.insets(0, 0, 5, 10), 0, 0
                    );
                    JLabel typeLabel = new JLabel("注解");
                    p.add(typeLabel, gb);
                    JTextField textField = new JTextField(s.toString());
                    textField.getDocument()
                        .addDocumentListener(
                            new com.intellij.ui.DocumentAdapter() {
                                @Override
                                protected void textChanged(javax.swing.event.DocumentEvent e) {
                                    s.setLength(0);
                                    s.append(textField.getText());
                                    r.run();
                                }
                            });
                    Dimension oldPreferredSize = textField.getPreferredSize();
                    textField.setPreferredSize(new Dimension(300, oldPreferredSize.height));
                    gb.gridx = 1;
                    gb.gridwidth = GridBagConstraints.REMAINDER;
                    gb.weightx = 1;
                    p.add(textField, gb);
                    r.run();

                    return p;
                };
                return
                    ToolbarDecorator.createDecorator(table)
                        .setAddAction(it ->
                            new IdeaDialog<StringBuilder>(panel)
                                .title("新增忽略注解")
                                .value(new StringBuilder())
                                .okAction(t -> setting.getQueryParamIgnoreAnnotations().add(t.toString()))
                                .changePredicate(t -> t.length() > 0)
                                .componentFunction(t -> function.apply(t.getValue(), t::onChange))
                                .doInit()
                                .showAndGet()
                        )
                        .setAddActionName("新增")
                        .setEditAction(it ->
                            new IdeaDialog<StringBuilder>(panel)
                                .title("编辑忽略注解")
                                .value(new StringBuilder(model.getRow(table.getSelectedRow())))
                                .okAction(t ->
                                    setting.getQueryParamIgnoreAnnotations().set(table.getSelectedRow(), t.toString())
                                )
                                .changePredicate(t -> t.length() > 0)
                                .componentFunction(t -> function.apply(t.getValue(), t::onChange))
                                .doInit()
                                .showAndGet()
                        )
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

    /**
     * 报文参数忽略类型设置
     * @param setting {@link ApiDocSettingPo}
     * @return {@link JPanel}
     */
    private static JPanel bodyParamPanel(ApiDocSettingPo setting) {
        JPanel panel = new JPanel(new GridBagLayout());
        PanelWithButtons bottom = new IdeaPanelWithButtons("忽略注解(字段上的注解):") {
            @Override
            protected JComponent createMainComponent() {
                BaseTableModel<String> model = new BaseTableModel<>(
                    Collections.singletonList("注解"), setting.getBodyIgnoreAnnotations());
                JBTable table = new IdeaJbTable(model);
                // 弹出层构建器
                BiFunction<StringBuilder, Runnable, JComponent> function = (s, r) -> {
                    JPanel p = new JPanel(new GridBagLayout());
                    GridBagConstraints gb = new GridBagConstraints(0, 0, 1, 1, 0, 0,
                        GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                        JBUI.insets(0, 0, 5, 10), 0, 0
                    );
                    JLabel typeLabel = new JLabel("注解");
                    p.add(typeLabel, gb);
                    JTextField textField = new JTextField(s.toString());
                    textField.getDocument()
                        .addDocumentListener(
                            new com.intellij.ui.DocumentAdapter() {
                                @Override
                                protected void textChanged(javax.swing.event.DocumentEvent e) {
                                    s.setLength(0);
                                    s.append(textField.getText());
                                    r.run();
                                }
                            });
                    Dimension oldPreferredSize = textField.getPreferredSize();
                    textField.setPreferredSize(new Dimension(300, oldPreferredSize.height));
                    gb.gridx = 1;
                    gb.gridwidth = GridBagConstraints.REMAINDER;
                    gb.weightx = 1;
                    p.add(textField, gb);
                    r.run();

                    return p;
                };
                return
                    ToolbarDecorator.createDecorator(table)
                        .setAddAction(it ->
                            new IdeaDialog<StringBuilder>(panel)
                                .title("新增忽略注解")
                                .value(new StringBuilder())
                                .okAction(t -> setting.getBodyIgnoreAnnotations().add(t.toString()))
                                .changePredicate(t -> t.length() > 0)
                                .componentFunction(t -> function.apply(t.getValue(), t::onChange))
                                .doInit()
                                .showAndGet()
                        )
                        .setAddActionName("新增")
                        .setEditAction(it ->
                            new IdeaDialog<StringBuilder>(panel)
                                .title("编辑忽略注解")
                                .value(new StringBuilder(model.getRow(table.getSelectedRow())))
                                .okAction(t ->
                                    setting.getBodyIgnoreAnnotations().set(table.getSelectedRow(), t.toString())
                                )
                                .changePredicate(t -> t.length() > 0)
                                .componentFunction(t -> function.apply(t.getValue(), t::onChange))
                                .doInit()
                                .showAndGet()
                        )
                        .setEditActionName("编辑")
                        .setRemoveAction(it -> model.removeRow(table.getSelectedRow()))
                        .setRemoveActionName("删除")
                        .disableUpDownActions()
                        .createPanel();
            }
        };
        panel.add(
            bottom,
            new GridBagConstraints(
                0, 0, 1, 1, 1, 1,
                GridBagConstraints.NORTH,
                GridBagConstraints.BOTH,
                JBUI.emptyInsets(), 0, 0
            )
        );

        return panel;
    }

    /**
     * 序列化配置
     * @param setting {@link ApiDocSettingPo}
     * @return {@link JPanel}
     */
    private static JPanel serialPanel(ApiDocSettingPo setting) {
        JPanel panel = new JPanel(new GridBagLayout());
        PanelWithButtons top = new IdeaPanelWithButtons("") {
            @Override
            protected JComponent createMainComponent() {
                BaseTableModel<ApiDocObjectSerialPo> model = new BaseTableModel<ApiDocObjectSerialPo>(
                    Arrays.asList("类型", "别名", "序列化默认值"), setting.getObjects()) {
                    @Override
                    protected List<Function<ApiDocObjectSerialPo, Object>> columns() {
                        return
                            Arrays.asList(
                                ApiDocObjectSerialPo::getType,
                                ApiDocObjectSerialPo::getAlias,
                                ApiDocObjectSerialPo::getValue
                            );
                    }
                };
                JBTable table = new IdeaJbTable(model);
                // 弹出层构建器
                Function<IdeaDialog<ApiDocObjectSerialPo>, JComponent> function = dialog -> {
                    ApiDocObjectSerialPo s = dialog.getValue();
                    JPanel p = new JPanel(new GridBagLayout());
                    GridBagConstraints gb = new GridBagConstraints(0, 0, 1, 1, 0, 0,
                        GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                        JBUI.insets(0, 0, 5, 10), 0, 0
                    );
                    JLabel typeLabel = new JLabel("类型");
                    p.add(typeLabel, gb);
                    JTextField textField = new JTextField();
                    Optional.ofNullable(s.getType()).ifPresent(textField::setText);
                    textField.getDocument()
                        .addDocumentListener(
                            new com.intellij.ui.DocumentAdapter() {
                                @Override
                                protected void textChanged(javax.swing.event.DocumentEvent e) {
                                    s.setType(textField.getText());
                                    dialog.onChange();
                                }
                            });
                    Dimension oldPreferredSize = textField.getPreferredSize();
                    textField.setPreferredSize(new Dimension(300, oldPreferredSize.height));
                    gb.gridx = 1;
                    gb.gridwidth = GridBagConstraints.REMAINDER;
                    gb.weightx = 1;
                    p.add(textField, gb);

                    JLabel aliasLabel = new JLabel("别名");
                    gb.gridy++;
                    gb.gridx = 0;
                    gb.gridwidth = 1;
                    gb.weightx = 0;
                    p.add(aliasLabel, gb);
                    JComboBox<String> aliasCombobox = new ComboBox<>(
                        Arrays.stream(AliasType.values())
                            .map(AliasType::getType)
                            .toArray(String[]::new)
                    );
                    aliasCombobox.addItemListener(e -> {
                        if (e.getStateChange() == ItemEvent.SELECTED) {
                            s.setAlias((String) e.getItem());
                        }
                        dialog.onChange();
                    });

                    if (Objects.isNull(s.getAlias())) {
                        // 新增的时候默认选中第一个
                        s.setAlias(aliasCombobox.getItemAt(0));
                    }
                    aliasCombobox.setSelectedItem(s.getAlias());

                    gb.gridx = 1;
                    gb.fill = GridBagConstraints.NONE;
                    gb.gridwidth = GridBagConstraints.REMAINDER;
                    gb.weightx = 0;
                    p.add(aliasCombobox, gb);

                    JLabel valueLabel = new JLabel("序列化默认值");
                    gb.gridy++;
                    gb.gridx = 0;
                    gb.gridwidth = 1;
                    gb.weightx = 0;
                    p.add(valueLabel, gb);
                    JTextField valueField = new JTextField();
                    Optional.ofNullable(s.getValue()).ifPresent(valueField::setText);
                    valueField.getDocument()
                        .addDocumentListener(
                            new com.intellij.ui.DocumentAdapter() {
                                @Override
                                protected void textChanged(javax.swing.event.DocumentEvent e) {
                                    s.setValue(valueField.getText());
                                    dialog.onChange();
                                }
                            });
                    valueField.setPreferredSize(new Dimension(300, oldPreferredSize.height));
                    gb.gridx = 1;
                    gb.gridwidth = GridBagConstraints.REMAINDER;
                    gb.weightx = 1;
                    p.add(valueField, gb);
                    dialog.onChange();

                    return p;
                };

                return
                    ToolbarDecorator.createDecorator(table)
                        .setAddAction(it ->
                            new IdeaDialog<ApiDocObjectSerialPo>(panel)
                                .title("新增序列化")
                                .value(new ApiDocObjectSerialPo())
                                .okAction(setting.getObjects()::add)
                                .changePredicate(ApiDocObjectSerialPo::isOk)
                                .componentFunction(function)
                                .doInit()
                                .showAndGet()
                        )
                        .setAddActionName("新增")
                        .setEditAction(it ->
                            new IdeaDialog<ApiDocObjectSerialPo>(panel)
                                .title("编辑序列化")
                                .value(model.getRow(table.getSelectedRow()))
                                .okAction(t -> setting.getObjects().set(table.getSelectedRow(), t))
                                .changePredicate(ApiDocObjectSerialPo::isOk)
                                .componentFunction(function)
                                .doInit()
                                .showAndGet()
                        )
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

        return panel;
    }
}
