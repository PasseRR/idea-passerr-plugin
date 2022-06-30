package com.github.passerr.idea.plugins.database.generator.action;

import com.github.passerr.idea.plugins.base.utils.NotificationUtil;
import com.github.passerr.idea.plugins.database.generator.action.template.Templates;
import com.github.passerr.idea.plugins.database.generator.config.GeneratorTemplateStateComponent;
import com.github.passerr.idea.plugins.database.generator.config.po.MappingPo;
import com.github.passerr.idea.plugins.database.generator.config.po.TemplatePo;
import com.github.passerr.idea.plugins.database.generator.config.po.TemplatesPo;
import com.intellij.database.psi.DbTable;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.LabeledComponent;
import com.intellij.ui.DocumentAdapter;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang.StringUtils;
import org.jdesktop.swingx.JXTextField;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Generated;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import java.awt.BorderLayout;
import java.awt.event.ItemEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 代码生成弹窗
 * @author xiehai
 * @date 2022/06/24 15:58
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
class GenerateDialog extends DialogWrapper {
    List<DbTable> list;
    Module[] modules;
    TemplatesPo templatesPo;
    DynamicCondition condition = new DynamicCondition();

    protected GenerateDialog(Project project, List<DbTable> list) {
        super(project);
        super.setTitle("代码生成");
        super.setOKButtonText("确定");
        super.setCancelButtonText("取消");
        this.modules = ModuleManager.getInstance(project).getModules();
        condition.setModulePath(0, this.modules[0].getModuleFilePath());
        this.templatesPo = GeneratorTemplateStateComponent.getInstance().getState().deepCopy();
        // 至少保证两条数据
        condition.setBasePackage(this.templatesPo.getTemplates().get(0).getDetail().getSettings().getBasePackage());

        this.list = list;
        super.init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        ComboBox<String> template = new ComboBox<>(
            this.templatesPo.getTemplates().stream().map(TemplatePo::getName).toArray(String[]::new)
        );
        panel.add(LabeledComponent.create(template, "模板选择", BorderLayout.WEST));
        ComboBox<Module> module = new ComboBox<>(modules);
        module.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                Module item = (Module) e.getItem();
                this.condition.setModulePath(module.getSelectedIndex(), item.getModuleFilePath());
            }
        });
        panel.add(LabeledComponent.create(module, "模块选择", BorderLayout.WEST));
        // 包路径
        JXTextField basePackage = new JXTextField("基础包路径");
        basePackage.setColumns(25);
        basePackage.setText(condition.getBasePackage());
        basePackage.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                condition.setBasePackage(basePackage.getText());
            }
        });
        panel.add(LabeledComponent.create(basePackage, "基础包名", BorderLayout.WEST));

        // 文件覆盖
        JCheckBox overrideFile = new JCheckBox();
        overrideFile.setSelected(condition.isOverrideFile());
        overrideFile.addItemListener(e -> condition.setOverrideFile(e.getStateChange() == ItemEvent.SELECTED));
        panel.add(LabeledComponent.create(overrideFile, "覆盖文件", BorderLayout.WEST));

        // 去表前缀
        JXTextField tablePrefix = new JXTextField();
        tablePrefix.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                condition.setTablePrefix(tablePrefix.getText());
            }
        });
        panel.add(LabeledComponent.create(tablePrefix, "去表前缀", BorderLayout.WEST));

        template.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                TemplatePo t = this.templatesPo.getTemplates().get(template.getSelectedIndex());
                String p = t.getDetail().getSettings().getBasePackage();
                if (StringUtils.isEmpty(this.condition.getBasePackage())
                    || !this.condition.getBasePackage().startsWith(p)) {
                    basePackage.setText(p);
                }
                this.condition.setIndex(template.getSelectedIndex());
            }
        });

        return panel;
    }

    @Override
    @Generated({})
    protected void doOKAction() {
        try {
            TemplatePo template = this.templatesPo.getTemplates().get(this.condition.getIndex());
            this.condition.transferTo(template.getDetail().getSettings());
            // 模版参数
            Map<String, Object> map = new HashMap<>(8);
            map.put(
                "author",
                Optional.ofNullable(template.getDetail().getSettings().getAuthor())
                    .map(String::trim)
                    .orElse("generator")
            );
            map.put("date", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            // 缓存准备类型转换数据
            Map<String, String> types = template.getDetail().getTypes()
                .stream()
                .collect(Collectors.toMap(MappingPo::getJdbcType, MappingPo::getJavaType));

            this.list.forEach(it -> Templates.generate(map, it, template, types));
        } finally {
            NotificationUtil.notify(
                new Notification(
                    "代码生成",
                    "代码生成",
                    "代码生成成功",
                    NotificationType.INFORMATION
                )
            );
        }
        super.doOKAction();
    }
}
