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
        this.baseComp(panel, 0);
        this.entityComp(panel, 1);
        this.mapperComp(panel, 2);
        this.mapperXmlComp(panel, 3);

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
        isResources.addItemListener(e -> {
            this.configInfo.setResourcesDir(e.getStateChange() == ItemEvent.SELECTED);
        });
        LabeledComponent<JCheckBox> resourcesLabeled =
            LabeledComponent.create(isResources, "是否资源目录路径", BorderLayout.BEFORE_LINE_BEGINS);
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
    }
}
