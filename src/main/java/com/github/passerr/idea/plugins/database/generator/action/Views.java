package com.github.passerr.idea.plugins.database.generator.action;

import com.github.passerr.idea.plugins.base.constants.StringConstants;
import com.github.passerr.idea.plugins.database.generator.config.ConfigPo;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.LabeledComponent;
import com.intellij.ui.DocumentAdapter;
import com.intellij.util.ui.JBUI;
import org.jdesktop.swingx.JXTextField;
import org.jetbrains.annotations.NotNull;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ItemEvent;
import java.io.IOException;
import java.nio.file.Paths;

/**
 * 代码生成视图
 * @author xiehai
 * @date 2022/06/27 15:36
 */
class Views {
    static JPanel dialogMainPanel(Module[] modules, ConfigPo configPo, DialogConfigInfo configInfo) {
        JPanel panel = new JPanel(new GridBagLayout());
        int row = 0;
        baseComp(modules, configInfo, panel, row++);
        entityComp(configInfo, panel, row++);
        mapperComp(configInfo, panel, row++);
        mapperXmlComp(configInfo, panel, row++);
        serviceComp(configInfo, panel, row++);
        if (configPo.isUseServiceImpl()) {
            serviceImplComp(configInfo, panel, row++);
        }

        // TODO 添加 是否文件覆盖、表前缀

        return panel;
    }

    static void baseComp(Module[] modules, DialogConfigInfo configInfo, JPanel panel, int row) {
        ComboBox<Module> comboBox = new ComboBox<>(modules);
        comboBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                Module item = (Module) e.getItem();
                configInfo.basePath = Views.modulePath(comboBox.getSelectedIndex(), item.getModuleFilePath());
            }
        });
        comboBox.setSelectedIndex(0);
        panel.add(
            LabeledComponent.create(comboBox, "选择模块", BorderLayout.BEFORE_LINE_BEGINS),
            new GridBagConstraints(
                0, row, 2, 1, 1, 1,
                GridBagConstraints.NORTH, GridBagConstraints.BOTH,
                JBUI.insetsBottom(2), 0, 0
            )
        );

        // 包路径
        JXTextField basePackage = new JXTextField("基础包路径");
        basePackage.setColumns(25);
        basePackage.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                configInfo.setBasePackage(basePackage.getText());
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

    static void entityComp(DialogConfigInfo configInfo, JPanel panel, int row) {
        // 默认包名
        JXTextField defaultPackage = new JXTextField();
        defaultPackage.setText(configInfo.entityPackage);
        defaultPackage.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                configInfo.setEntityPackage(defaultPackage.getText());
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
        suffix.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                configInfo.setEntitySuffix(suffix.getText());
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

    static void mapperComp(DialogConfigInfo configInfo, JPanel panel, int row) {
        // 默认包名
        JXTextField defaultPackage = new JXTextField();
        defaultPackage.setText(configInfo.mapperPackage);
        defaultPackage.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                configInfo.setMapperPackage(defaultPackage.getText());
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
        suffix.setText(configInfo.mapperSuffix);
        suffix.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                configInfo.setMapperSuffix(suffix.getText());
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

    static void mapperXmlComp(DialogConfigInfo configInfo, JPanel panel, int row) {
        JCheckBox needXml = new JCheckBox();
        needXml.setSelected(configInfo.needMapperXml);
        panel.add(
            LabeledComponent.create(needXml, "是否生成xml", BorderLayout.BEFORE_LINE_BEGINS),
            new GridBagConstraints(
                0, row, 1, 1, 1, 1,
                GridBagConstraints.NORTH, GridBagConstraints.BOTH,
                JBUI.insetsBottom(2), 0, 0
            )
        );
        JCheckBox isResources = new JCheckBox();
        isResources.setSelected(configInfo.resourcesDir);
        isResources.addItemListener(e -> configInfo.setResourcesDir(e.getStateChange() == ItemEvent.SELECTED));
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
        defaultPackage.setText(configInfo.mapperXmlPath);
        defaultPackage.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                configInfo.setMapperXmlPath(defaultPackage.getText());
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

        needXml.addItemListener(e -> {
            boolean selected = e.getStateChange() == ItemEvent.SELECTED;
            configInfo.setNeedMapperXml(selected);
            resourcesLabeled.setVisible(selected);
            pathLabeled.setVisible(selected);
        });
    }

    static void serviceComp(DialogConfigInfo configInfo, JPanel panel, int row) {
        // service包名
        JXTextField servicePackage = new JXTextField();
        servicePackage.setText(configInfo.servicePackage);
        servicePackage.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                configInfo.setServicePackage(servicePackage.getText());
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

        // controller包名
        JXTextField controllerPackage = new JXTextField();
        controllerPackage.setText(configInfo.controllerPackage);
        controllerPackage.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                configInfo.setControllerPackage(controllerPackage.getText());
            }
        });
        panel.add(
            LabeledComponent.create(controllerPackage, "controller包名", BorderLayout.BEFORE_LINE_BEGINS),
            new GridBagConstraints(
                2, row, 2, 1, 1, 1,
                GridBagConstraints.NORTH, GridBagConstraints.BOTH,
                JBUI.insetsBottom(2), 0, 0
            )
        );
    }

    static void serviceImplComp(DialogConfigInfo configInfo, JPanel panel, int row) {
        // serviceImpl包名
        JXTextField serviceImplPackage = new JXTextField();
        serviceImplPackage.setText(configInfo.serviceImplPackage);
        serviceImplPackage.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                configInfo.setServiceImplPackage(serviceImplPackage.getText());
            }
        });
        LabeledComponent<JXTextField> packageLabeled =
            LabeledComponent.create(serviceImplPackage, "service实现包名", BorderLayout.BEFORE_LINE_BEGINS);
        panel.add(
            packageLabeled,
            new GridBagConstraints(
                0, row, 1, 1, 1, 1,
                GridBagConstraints.NORTH, GridBagConstraints.BOTH,
                JBUI.insetsBottom(2), 0, 0
            )
        );

        // service实现后缀
        JXTextField serviceImplSuffix = new JXTextField();
        serviceImplSuffix.setText(configInfo.serviceImplSuffix);
        serviceImplSuffix.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                configInfo.setServiceImplSuffix(serviceImplSuffix.getText());
            }
        });
        LabeledComponent<JXTextField> suffixLabeled =
            LabeledComponent.create(serviceImplSuffix, "service实现后缀", BorderLayout.BEFORE_LINE_BEGINS);
        panel.add(
            suffixLabeled,
            new GridBagConstraints(
                2, row, 2, 1, 1, 1,
                GridBagConstraints.NORTH, GridBagConstraints.BOTH,
                JBUI.insetsBottom(2), 0, 0
            )
        );
    }

    static String modulePath(int index, String moduleFilePath) {
        String prefix = "../";
        if (index == 0) {
            prefix += prefix;
        }
        try {
            return Paths.get(moduleFilePath, prefix).toRealPath().toString();
        } catch (IOException ignore) {
        }

        return StringConstants.EMPTY;
    }
}
