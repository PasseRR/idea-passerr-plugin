package com.github.passerr.idea.plugins.database.generator.config;

import com.github.passerr.idea.plugins.base.BaseTableModel;
import com.github.passerr.idea.plugins.base.IdeaDialog;
import com.github.passerr.idea.plugins.base.IdeaJbTable;
import com.github.passerr.idea.plugins.base.IdeaPanelWithButtons;
import com.github.passerr.idea.plugins.base.utils.ResourceUtil;
import com.github.passerr.idea.plugins.base.utils.VelocityUtil;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.ui.LabeledComponent;
import com.intellij.ui.BrowserHyperlinkListener;
import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.PanelWithButtons;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.SeparatorFactory;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.components.JBTabbedPane;
import com.intellij.ui.table.JBTable;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;
import org.jdesktop.swingx.JXTextField;
import org.jetbrains.annotations.NotNull;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ItemEvent;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

/**
 * 配置视图
 * @author xiehai
 * @date 2022/06/23 14:45
 */
class Views {
    /**
     * 常量view
     */
    static final Component DESC_VIEW = descView();

    /**
     * tab配置模块ui
     * @return {@link Component}
     */
    static Component tabsView(ConfigPo copy) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        JCheckBox useServiceImpl = new JCheckBox();
        panel.add(LabeledComponent.create(useServiceImpl, "使用service实现", BorderLayout.WEST));
        // tab配置
        JBTabbedPane tabbedPanel = new JBTabbedPane();
        tabbedPanel.addTab(
            "entity模版",
            VelocityUtil.velocityEditor(copy, ConfigPo::getEntity, copy::setEntity)
        );
        tabbedPanel.addTab(
            "mapper模版",
            VelocityUtil.velocityEditor(copy, ConfigPo::getMapper, copy::setEntity)
        );
        tabbedPanel.addTab(
            "mapper xml模版",
            VelocityUtil.velocityEditor(copy, ConfigPo::getMapperXml, copy::setMapperXml)
        );
        tabbedPanel.addTab(
            "service模版",
            VelocityUtil.velocityEditor(copy, ConfigPo::getService, copy::setService)
        );
        tabbedPanel.addTab(
            "service实现模版",
            VelocityUtil.velocityEditor(copy, ConfigPo::getServiceImpl, copy::setServiceImpl)
        );
        tabbedPanel.addTab(
            "controller模版",
            VelocityUtil.velocityEditor(copy, ConfigPo::getController, copy::setController)
        );
        tabbedPanel.setEnabledAt(4, false);
        tabbedPanel.getTabComponentAt(4).setEnabled(false);
        tabbedPanel.addTab("类型映射", typesTab(copy.getTypes()));
        tabbedPanel.addTab("", new JPanel());
        int last = tabbedPanel.getTabCount() - 1;
        JButton button = new JButton("", AllIcons.Actions.Refresh);
        button.setPreferredSize(new Dimension(30, 30));
        button.setToolTipText("从远程同步配置");
        button.addActionListener(e -> new SyncDialog(copy).show());
        tabbedPanel.setTabComponentAt(last, button);
        tabbedPanel.setEnabledAt(last, false);
        panel.add(tabbedPanel);

        useServiceImpl.addItemListener(e -> {
            boolean selected = e.getStateChange() == ItemEvent.SELECTED;
            copy.setUseServiceImpl(selected);
            tabbedPanel.setEnabledAt(4, selected);
            tabbedPanel.getTabComponentAt(4).setEnabled(selected);
        });

        return panel;
    }

    /**
     * 类型映射配置表
     * @param list 类型映射列表
     * @return {@link Component}
     */
    private static Component typesTab(List<TypeMappingPo> list) {
        JPanel panel = new JPanel(new GridBagLayout());
        PanelWithButtons top = new IdeaPanelWithButtons("") {
            @Override
            protected JComponent createMainComponent() {
                BaseTableModel<TypeMappingPo> model =
                    new BaseTableModel<TypeMappingPo>(Arrays.asList("JDBC类型", "JAVA类型"), list) {
                        @Override
                        protected List<Function<TypeMappingPo, Object>> columns() {
                            return Arrays.asList(TypeMappingPo::getJdbcType, TypeMappingPo::getJavaType);
                        }
                    };
                JBTable table = new IdeaJbTable(model);
                // 弹出层构建器
                Function<IdeaDialog<TypeMappingPo>, JComponent> function = dialog -> {
                    JPanel p = new JPanel();
                    p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
                    TypeMappingPo value = dialog.getValue();

                    JXTextField jdbcField = new JXTextField();
                    jdbcField.setText(value.getJdbcType());
                    jdbcField.setEditable(false);
                    jdbcField.setColumns(20);

                    p.add(LabeledComponent.create(jdbcField, "JDBC类型", BorderLayout.WEST));

                    JXTextField javaField = new JXTextField();
                    javaField.setText(value.getJavaType());
                    javaField.getDocument().addDocumentListener(new DocumentAdapter() {
                        @Override
                        protected void textChanged(@NotNull DocumentEvent e) {
                            value.setJavaType(javaField.getText());
                        }
                    });
                    p.add(LabeledComponent.create(javaField, "JAVA类型", BorderLayout.WEST));

                    return p;
                };
                return
                    ToolbarDecorator.createDecorator(table)
                        .disableAddAction()
                        .setEditAction(it ->
                            new IdeaDialog<TypeMappingPo>(panel)
                                .title("修改类型映射")
                                .value(model.getRow(table.getSelectedRow()))
                                .okAction(t ->
                                    list.set(table.getSelectedRow(), t)
                                )
                                .changePredicate(t -> !t.getJavaType().isEmpty())
                                .componentFunction(function)
                                .doInit()
                                .showAndGet()
                        )
                        .setEditActionName("编辑")
                        .disableRemoveAction()
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

    /**
     * velocity配置描述ui
     * @return {@link Component}
     */
    private static Component descView() {
        // 描述模块
        JEditorPane desc = new JEditorPane(UIUtil.HTML_MIME, "");
        desc.setEditable(false);
        desc.setEditorKit(new HTMLEditorKit());
        desc.addHyperlinkListener(new BrowserHyperlinkListener());
        desc.setText(ResourceUtil.readAsString("/generator/desc.html"));

        JPanel panel = new JPanel(new GridBagLayout());
        panel.add(
            SeparatorFactory.createSeparator("变量描述:", null),
            new GridBagConstraints(
                0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                JBUI.insetsBottom(2), 0, 0
            )
        );
        panel.add(
            ScrollPaneFactory.createScrollPane(desc),
            new GridBagConstraints(
                0, 1, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                JBUI.insetsTop(2), 0, 0
            )
        );

        return panel;
    }
}
