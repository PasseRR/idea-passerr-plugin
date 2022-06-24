package com.github.passerr.idea.plugins.database.generator.action;

import com.intellij.database.model.DasTable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.LabeledComponent;
import com.intellij.ui.DocumentAdapter;
import com.intellij.util.ui.JBUI;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.jdesktop.swingx.JXTextField;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ItemEvent;
import java.util.List;

/**
 * 代码生成弹窗
 * @author xiehai
 * @date 2022/06/24 15:58
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
class GenerateDialog extends DialogWrapper {
    List<DasTable> list;
    /**
     * 配置信息
     */
    DialogConfigInfo configInfo;

    protected GenerateDialog(Project project, List<DasTable> list) {
        super(project);
        super.setTitle("代码生成");
        super.setOKButtonText("确定");
        super.setCancelButtonText("取消");
        this.configInfo = new DialogConfigInfo(project.getBasePath());
        this.list = list;
        super.init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        int row = 0;
        this.baseComp(panel, row++);
        this.entityComp(panel, row++);
        this.mapperComp(panel, row++);
        this.serviceComp(panel, row++);
        this.mapperXmlComp(panel, row++);
        this.serviceImplComp(panel, row);

        return panel;
    }

    protected void baseComp(JPanel panel, int row) {
        // 项目路径
        JXTextField projectPath = new JXTextField();
        projectPath.setEditable(false);
        projectPath.setColumns(25);
        projectPath.setText(this.configInfo.basePath);
        panel.add(
            LabeledComponent.create(projectPath, "项目路径", BorderLayout.BEFORE_LINE_BEGINS),
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
                GenerateDialog.this.configInfo.setBasePackage(basePackage.getText());
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

    protected void entityComp(JPanel panel, int row) {
        // 默认包名
        JXTextField defaultPackage = new JXTextField();
        defaultPackage.setText(this.configInfo.entityPackage);
        defaultPackage.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                GenerateDialog.this.configInfo.setEntityPackage(defaultPackage.getText());
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
                GenerateDialog.this.configInfo.setEntitySuffix(suffix.getText());
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

    protected void mapperComp(JPanel panel, int row) {
        // 默认包名
        JXTextField defaultPackage = new JXTextField();
        defaultPackage.setText(this.configInfo.mapperPackage);
        defaultPackage.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                GenerateDialog.this.configInfo.setMapperPackage(defaultPackage.getText());
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
        suffix.setText(this.configInfo.mapperSuffix);
        suffix.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                GenerateDialog.this.configInfo.setMapperSuffix(suffix.getText());
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

    protected void mapperXmlComp(JPanel panel, int row) {
        JCheckBox needXml = new JCheckBox();
        needXml.setSelected(this.configInfo.needMapperXml);
        panel.add(
            LabeledComponent.create(needXml, "是否生成xml", BorderLayout.BEFORE_LINE_BEGINS),
            new GridBagConstraints(
                0, row, 1, 1, 1, 1,
                GridBagConstraints.NORTH, GridBagConstraints.BOTH,
                JBUI.insetsBottom(2), 0, 0
            )
        );
        JCheckBox isResources = new JCheckBox();
        isResources.setSelected(this.configInfo.resourcesDir);
        isResources.addItemListener(e -> this.configInfo.setResourcesDir(e.getStateChange() == ItemEvent.SELECTED));
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
        defaultPackage.setText(this.configInfo.mapperXmlPath);
        defaultPackage.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                GenerateDialog.this.configInfo.setMapperXmlPath(defaultPackage.getText());
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
            this.configInfo.setNeedMapperXml(selected);
            resourcesLabeled.setVisible(selected);
            pathLabeled.setVisible(selected);
        });
    }

    protected void serviceComp(JPanel panel, int row) {
        // service包名
        JXTextField servicePackage = new JXTextField();
        servicePackage.setText(this.configInfo.servicePackage);
        servicePackage.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                GenerateDialog.this.configInfo.setServicePackage(servicePackage.getText());
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
        controllerPackage.setText(this.configInfo.controllerPackage);
        controllerPackage.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                GenerateDialog.this.configInfo.setControllerPackage(controllerPackage.getText());
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

    protected void serviceImplComp(JPanel panel, int row) {
        JCheckBox useServiceImpl = new JCheckBox();
        useServiceImpl.setSelected(this.configInfo.useServiceImpl);
        panel.add(
            LabeledComponent.create(useServiceImpl, "是否使用service实现", BorderLayout.BEFORE_LINE_BEGINS),
            new GridBagConstraints(
                0, row, 1, 1, 1, 1,
                GridBagConstraints.NORTH, GridBagConstraints.BOTH,
                JBUI.insetsBottom(2), 0, 0
            )
        );

        // serviceImpl包名
        JXTextField serviceImplPackage = new JXTextField();
        serviceImplPackage.setText(this.configInfo.serviceImplPackage);
        serviceImplPackage.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                GenerateDialog.this.configInfo.setServiceImplPackage(serviceImplPackage.getText());
            }
        });
        LabeledComponent<JXTextField> packageLabeled =
            LabeledComponent.create(serviceImplPackage, "service实现包名", BorderLayout.BEFORE_LINE_BEGINS);
        panel.add(
            packageLabeled,
            new GridBagConstraints(
                1, row, 1, 1, 1, 1,
                GridBagConstraints.NORTH, GridBagConstraints.BOTH,
                JBUI.insetsBottom(2), 0, 0
            )
        );

        // service实现后缀
        JXTextField serviceImplSuffix = new JXTextField();
        serviceImplSuffix.setText(this.configInfo.serviceImplSuffix);
        serviceImplSuffix.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                GenerateDialog.this.configInfo.setServiceImplSuffix(serviceImplSuffix.getText());
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

        useServiceImpl.addItemListener(e -> {
            boolean selected = e.getStateChange() == ItemEvent.SELECTED;
            this.configInfo.setUseServiceImpl(selected);
            packageLabeled.setVisible(selected);
            suffixLabeled.setVisible(selected);
        });
    }
}
