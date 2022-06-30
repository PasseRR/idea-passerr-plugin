package com.github.passerr.idea.plugins.database.generator.config;

import com.github.passerr.idea.plugins.base.BaseTableModel;
import com.github.passerr.idea.plugins.base.IdeaDialog;
import com.github.passerr.idea.plugins.base.IdeaJbTable;
import com.github.passerr.idea.plugins.base.IdeaPanelWithButtons;
import com.github.passerr.idea.plugins.base.utils.DesktopUtil;
import com.github.passerr.idea.plugins.base.utils.PluginUtil;
import com.github.passerr.idea.plugins.base.utils.VelocityUtil;
import com.github.passerr.idea.plugins.database.generator.config.po.DetailPo;
import com.github.passerr.idea.plugins.database.generator.config.po.MappingPo;
import com.github.passerr.idea.plugins.database.generator.config.po.SettingPo;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.ui.LabeledComponent;
import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.PanelWithButtons;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.components.JBTabbedPane;
import com.intellij.ui.table.JBTable;
import com.intellij.util.ui.JBDimension;
import com.intellij.util.ui.JBUI;
import org.jdesktop.swingx.JXTextField;
import org.jetbrains.annotations.NotNull;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ItemEvent;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * 配置视图
 * @author xiehai
 * @date 2022/06/23 14:45
 */
class Views {
    /**
     * tab配置模块ui
     * @return {@link Component}
     */
    static JComponent tabsView(DetailPo detail) {
        JPanel panel = new JPanel(new BorderLayout());
        JCheckBox useServiceImpl = new JCheckBox();
        useServiceImpl.setSelected(detail.isUseServiceImpl());
        panel.add(LabeledComponent.create(useServiceImpl, "使用service实现", BorderLayout.WEST), BorderLayout.NORTH);
        // tab配置
        JBTabbedPane tabbedPanel = new JBTabbedPane();
        tabbedPanel.addTab(
            "entity模版",
            VelocityUtil.velocityEditor(detail, DetailPo::getEntity, detail::setEntity)
        );
        tabbedPanel.addTab(
            "mapper模版",
            VelocityUtil.velocityEditor(detail, DetailPo::getMapper, detail::setEntity)
        );
        tabbedPanel.addTab(
            "mapper xml模版",
            VelocityUtil.velocityEditor(detail, DetailPo::getMapperXml, detail::setMapperXml)
        );
        tabbedPanel.addTab(
            "service模版",
            VelocityUtil.velocityEditor(detail, DetailPo::getService, detail::setService)
        );
        tabbedPanel.addTab(
            "service实现模版",
            VelocityUtil.velocityEditor(detail, DetailPo::getServiceImpl, detail::setServiceImpl)
        );
        tabbedPanel.addTab(
            "controller模版",
            VelocityUtil.velocityEditor(detail, DetailPo::getController, detail::setController)
        );
        tabbedPanel.add("类型映射", Views.typesTab(detail.getTypes()));
        JComponent settingPanel = Views.settingPanel(detail);
        tabbedPanel.add("代码生成配置", settingPanel);
        tabbedPanel.add("", new JPanel());
        int last = tabbedPanel.getTabCount() - 1;
        JButton button = new JButton("", AllIcons.Actions.Help);
        button.setPreferredSize(JBUI.size(30, 30));
        button.setToolTipText("配置帮助文档");
        button.addActionListener(e -> DesktopUtil.browser(PluginUtil.gitBlobUrl("docs/generator.md")));
        tabbedPanel.setTabComponentAt(last, button);
        tabbedPanel.setEnabledAt(last, false);
        panel.add(tabbedPanel, BorderLayout.CENTER);

        Component[] components = settingPanel.getComponents();
        Consumer<Boolean> switchEnable = selected -> {
            detail.setUseServiceImpl(selected);
            tabbedPanel.setEnabledAt(4, selected);
            tabbedPanel.getTabComponentAt(4).setEnabled(selected);
            int c1 = components.length - 1, c2 = c1 - 1;
            if (c1 >= 0) {
                components[c1].setVisible(selected);
            }
            if (c2 >= 0) {
                components[c2].setVisible(selected);
            }
        };

        switchEnable.accept(detail.isUseServiceImpl());

        useServiceImpl.addItemListener(e -> {
            boolean selected = e.getStateChange() == ItemEvent.SELECTED;
            switchEnable.accept(selected);
        });

        JBDimension size = JBUI.size(835, 200);
        panel.setSize(size);
        panel.setMaximumSize(size);
        panel.setPreferredSize(size);
        tabbedPanel.setSize(size);
        tabbedPanel.setMaximumSize(size);
        tabbedPanel.setPreferredSize(size);
        Arrays.stream(tabbedPanel.getComponents()).forEach(it -> {
            it.setSize(size);
            it.setMaximumSize(size);
            it.setPreferredSize(size);
        });

        panel.repaint();

        return panel;
    }

    /**
     * 类型映射配置表
     * @param list 类型映射列表
     * @return {@link Component}
     */
    private static Component typesTab(List<MappingPo> list) {
        JPanel panel = new JPanel(new GridBagLayout());
        PanelWithButtons top = new IdeaPanelWithButtons() {
            @Override
            protected JComponent createMainComponent() {
                BaseTableModel<MappingPo> model =
                    new BaseTableModel<MappingPo>(Arrays.asList("JDBC类型", "JAVA类型"), list) {
                        @Override
                        protected List<Function<MappingPo, Object>> columns() {
                            return Arrays.asList(MappingPo::getJdbcType, MappingPo::getJavaType);
                        }
                    };
                JBTable table = new IdeaJbTable(model);
                // 弹出层构建器
                Function<IdeaDialog<MappingPo>, JComponent> function = dialog -> {
                    JPanel p = new JPanel();
                    p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
                    MappingPo value = dialog.getValue();

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
                            new IdeaDialog<MappingPo>(panel)
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

    static JComponent settingPanel(DetailPo detailPo) {
        JPanel panel = new JPanel(new GridBagLayout());
        int row = 0;
        Views.baseComp(detailPo.getSettings(), panel, row++);
        Views.entityComp(detailPo.getSettings(), panel, row++);
        Views.mapperComp(detailPo.getSettings(), panel, row++);
        Views.mapperXmlComp(detailPo.getSettings(), panel, row++);
        Views.serviceComp(detailPo.getSettings(), panel, row++);
        Views.controllerComp(detailPo.getSettings(), panel, row++);
        Views.serviceImplComp(detailPo.getSettings(), panel, row);

        return panel;
    }

    static void baseComp(SettingPo setting, JPanel panel, int row) {
        JXTextField author = new JXTextField();
        author.setColumns(30);
        author.setText(setting.getAuthor());
        author.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                setting.setAuthor(author.getText());
            }
        });
        panel.add(
            LabeledComponent.create(author, "代码作者", BorderLayout.BEFORE_LINE_BEGINS),
            new GridBagConstraints(
                0, row, 2, 1, 1, 1,
                GridBagConstraints.NORTH, GridBagConstraints.BOTH,
                JBUI.insetsBottom(2), 0, 0
            )
        );

        // 包路径
        JXTextField basePackage = new JXTextField("基础包路径(代码生成时可以修改)");
        basePackage.setColumns(30);
        basePackage.setText(setting.getBasePackage());
        basePackage.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                setting.setBasePackage(basePackage.getText());
            }
        });
        panel.add(
            LabeledComponent.create(basePackage, "基础包", BorderLayout.BEFORE_LINE_BEGINS),
            new GridBagConstraints(
                2, row, 2, 1, 1, 1,
                GridBagConstraints.NORTH, GridBagConstraints.BOTH,
                JBUI.insetsBottom(2), 0, 0
            )
        );
    }

    static void entityComp(SettingPo setting, JPanel panel, int row) {
        // 默认包名
        JXTextField defaultPackage = new JXTextField();
        defaultPackage.setText(setting.getEntityPackage());
        defaultPackage.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                setting.setEntityPackage(defaultPackage.getText());
            }
        });
        panel.add(
            LabeledComponent.create(defaultPackage, "entity包名", BorderLayout.BEFORE_LINE_BEGINS),
            new GridBagConstraints(
                0, row, 2, 1, 1, 1,
                GridBagConstraints.NORTH, GridBagConstraints.BOTH,
                JBUI.insetsBottom(2), 0, 0
            )
        );

        JXTextField suffix = new JXTextField();
        suffix.setText(setting.getEntitySuffix());
        suffix.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                setting.setEntitySuffix(suffix.getText());
            }
        });
        panel.add(
            LabeledComponent.create(suffix, "entity后缀", BorderLayout.BEFORE_LINE_BEGINS),
            new GridBagConstraints(
                2, row, 2, 1, 1, 1,
                GridBagConstraints.NORTH, GridBagConstraints.BOTH,
                JBUI.insetsBottom(2), 0, 0
            )
        );
    }

    static void mapperComp(SettingPo setting, JPanel panel, int row) {
        // 默认包名
        JXTextField defaultPackage = new JXTextField();
        defaultPackage.setText(setting.getMapperPackage());
        defaultPackage.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                setting.setMapperPackage(defaultPackage.getText());
            }
        });
        panel.add(
            LabeledComponent.create(defaultPackage, "mapper包名", BorderLayout.BEFORE_LINE_BEGINS),
            new GridBagConstraints(
                0, row, 2, 1, 1, 1,
                GridBagConstraints.NORTH, GridBagConstraints.BOTH,
                JBUI.insetsBottom(2), 0, 0
            )
        );

        JXTextField suffix = new JXTextField();
        suffix.setText(setting.getMapperSuffix());
        suffix.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                setting.setMapperSuffix(suffix.getText());
            }
        });
        panel.add(
            LabeledComponent.create(suffix, "mapper后缀", BorderLayout.BEFORE_LINE_BEGINS),
            new GridBagConstraints(
                2, row, 2, 1, 1, 1,
                GridBagConstraints.NORTH, GridBagConstraints.BOTH,
                JBUI.insetsBottom(2), 0, 0
            )
        );
    }

    static void mapperXmlComp(SettingPo setting, JPanel panel, int row) {
        JCheckBox needXml = new JCheckBox();
        needXml.setSelected(setting.isNeedMapperXml());
        panel.add(
            LabeledComponent.create(needXml, "是否生成xml", BorderLayout.BEFORE_LINE_BEGINS),
            new GridBagConstraints(
                0, row, 1, 1, 1, 1,
                GridBagConstraints.NORTH, GridBagConstraints.BOTH,
                JBUI.insetsBottom(2), 0, 0
            )
        );
        JCheckBox isResources = new JCheckBox();
        isResources.setSelected(setting.isResourcesDir());
        isResources.addItemListener(e -> setting.setResourcesDir(e.getStateChange() == ItemEvent.SELECTED));
        LabeledComponent<JCheckBox> resourcesLabeled =
            LabeledComponent.create(isResources, "生成xml到资源目录", BorderLayout.BEFORE_LINE_BEGINS);
        panel.add(
            resourcesLabeled,
            new GridBagConstraints(
                1, row, 1, 1, 1, 1,
                GridBagConstraints.NORTH, GridBagConstraints.BOTH,
                JBUI.insetsBottom(2), 0, 0
            )
        );

        // 默认包名
        JXTextField defaultPackage = new JXTextField();
        defaultPackage.setText(setting.getMapperXmlPath());
        defaultPackage.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                setting.setMapperXmlPath(defaultPackage.getText());
            }
        });
        LabeledComponent<JXTextField> pathLabeled =
            LabeledComponent.create(defaultPackage, "mapper xml路径(使用包名形式)", BorderLayout.BEFORE_LINE_BEGINS);
        panel.add(
            pathLabeled,
            new GridBagConstraints(
                2, row, 2, 1, 1, 1,
                GridBagConstraints.NORTH, GridBagConstraints.BOTH,
                JBUI.insetsBottom(2), 0, 0
            )
        );

        Consumer<Boolean> switchEnable = selected -> {
            setting.setNeedMapperXml(selected);
            resourcesLabeled.setVisible(selected);
            pathLabeled.setVisible(selected);
        };
        switchEnable.accept(setting.isNeedMapperXml());
        needXml.addItemListener(e -> switchEnable.accept(e.getStateChange() == ItemEvent.SELECTED));
    }

    static void serviceComp(SettingPo setting, JPanel panel, int row) {
        // service包名
        JXTextField servicePackage = new JXTextField();
        servicePackage.setText(setting.getServicePackage());
        servicePackage.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                setting.setServicePackage(servicePackage.getText());
            }
        });
        panel.add(
            LabeledComponent.create(servicePackage, "service包名", BorderLayout.BEFORE_LINE_BEGINS),
            new GridBagConstraints(
                0, row, 2, 1, 1, 1,
                GridBagConstraints.NORTH, GridBagConstraints.BOTH,
                JBUI.insetsBottom(2), 0, 0
            )
        );

        JXTextField serviceSuffix = new JXTextField();
        serviceSuffix.setText(setting.getServiceSuffix());
        serviceSuffix.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                setting.setServiceSuffix(serviceSuffix.getText());
            }
        });
        panel.add(
            LabeledComponent.create(serviceSuffix, "service后缀", BorderLayout.BEFORE_LINE_BEGINS),
            new GridBagConstraints(
                2, row, 2, 1, 1, 1,
                GridBagConstraints.NORTH, GridBagConstraints.BOTH,
                JBUI.insetsBottom(2), 0, 0
            )
        );
    }

    static void controllerComp(SettingPo setting, JPanel panel, int row) {
        JXTextField controllerPackage = new JXTextField();
        controllerPackage.setText(setting.getControllerPackage());
        controllerPackage.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                setting.setControllerPackage(controllerPackage.getText());
            }
        });
        panel.add(
            LabeledComponent.create(controllerPackage, "controller包名", BorderLayout.BEFORE_LINE_BEGINS),
            new GridBagConstraints(
                0, row, 2, 1, 1, 1,
                GridBagConstraints.NORTH, GridBagConstraints.BOTH,
                JBUI.insetsBottom(2), 0, 0
            )
        );

        JXTextField controllerSuffix = new JXTextField();
        controllerSuffix.setText(setting.getControllerSuffix());
        controllerSuffix.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                setting.setControllerSuffix(controllerSuffix.getText());
            }
        });
        panel.add(
            LabeledComponent.create(controllerSuffix, "controller后缀", BorderLayout.BEFORE_LINE_BEGINS),
            new GridBagConstraints(
                2, row, 2, 1, 1, 1,
                GridBagConstraints.NORTH, GridBagConstraints.BOTH,
                JBUI.insetsBottom(2), 0, 0
            )
        );
    }

    static void serviceImplComp(SettingPo setting, JPanel panel, int row) {
        // serviceImpl包名
        JXTextField serviceImplPackage = new JXTextField();
        serviceImplPackage.setText(setting.getServiceImplPackage());
        serviceImplPackage.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                setting.setServiceImplPackage(serviceImplPackage.getText());
            }
        });
        panel.add(
            LabeledComponent.create(serviceImplPackage, "service实现包名", BorderLayout.BEFORE_LINE_BEGINS),
            new GridBagConstraints(
                0, row, 2, 1, 1, 1,
                GridBagConstraints.NORTH, GridBagConstraints.BOTH,
                JBUI.insetsBottom(2), 0, 0
            )
        );

        // service实现后缀
        JXTextField serviceImplSuffix = new JXTextField();
        serviceImplSuffix.setText(setting.getServiceImplSuffix());
        serviceImplSuffix.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                setting.setServiceImplSuffix(serviceImplSuffix.getText());
            }
        });
        panel.add(
            LabeledComponent.create(serviceImplSuffix, "service实现后缀", BorderLayout.BEFORE_LINE_BEGINS),
            new GridBagConstraints(
                2, row, 2, 1, 1, 1,
                GridBagConstraints.NORTH, GridBagConstraints.BOTH,
                JBUI.insetsBottom(2), 0, 0
            )
        );
    }
}
