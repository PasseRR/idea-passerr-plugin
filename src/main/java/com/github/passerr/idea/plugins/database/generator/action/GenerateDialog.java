package com.github.passerr.idea.plugins.database.generator.action;

import com.github.passerr.idea.plugins.base.utils.NotificationUtil;
import com.github.passerr.idea.plugins.database.generator.action.template.Templates;
import com.github.passerr.idea.plugins.database.generator.config.ConfigPo;
import com.github.passerr.idea.plugins.database.generator.config.GeneratorStateComponent;
import com.github.passerr.idea.plugins.database.generator.config.TypeMappingPo;
import com.intellij.database.psi.DbTable;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Generated;
import javax.swing.JComponent;
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
    /**
     * 配置信息
     */
    DialogConfigInfo configInfo;
    ConfigPo configPo;
    Module[] modules;

    protected GenerateDialog(Project project, List<DbTable> list) {
        super(project);
        this.modules = ModuleManager.getInstance(project).getModules();
        super.setTitle("代码生成");
        super.setOKButtonText("确定");
        super.setCancelButtonText("取消");
        this.configInfo = new DialogConfigInfo(Views.modulePath(0, modules[0].getModuleFilePath()));
        this.list = list;
        this.configPo = GeneratorStateComponent.getInstance().getState().deepCopy();
        super.init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return Views.dialogMainPanel(this.modules, this.configPo, this.configInfo);
    }

    @Override
    @Generated({})
    protected void doOKAction() {
        try {
            // 模版参数
            Map<String, Object> map = new HashMap<>(8);
            map.put(
                "author",
                Optional.ofNullable(configPo.getConfig().getAuthor())
                    .map(String::trim)
                    .orElse("generator")
            );
            map.put("date", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            // 缓存准备类型转换数据
            Map<String, String> types = this.configPo.getTypes()
                .stream()
                .collect(Collectors.toMap(TypeMappingPo::getJdbcType, TypeMappingPo::getJavaType));

            this.list.forEach(it -> Templates.generate(map, it, this.configPo, this.configInfo, types));
        } finally {
            NotificationUtil.notify(
                new Notification(
                    "code generate",
                    "code generate",
                    "code generate success!",
                    NotificationType.INFORMATION
                )
            );
        }
        super.doOKAction();
    }
}
