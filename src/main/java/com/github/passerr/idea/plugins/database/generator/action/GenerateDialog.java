package com.github.passerr.idea.plugins.database.generator.action;

import com.github.passerr.idea.plugins.base.utils.NotificationUtil;
import com.github.passerr.idea.plugins.database.generator.action.template.Templates;
import com.github.passerr.idea.plugins.database.generator.config.ConfigPo;
import com.github.passerr.idea.plugins.database.generator.config.GeneratorStateComponent;
import com.intellij.database.model.DasTable;
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
    ConfigPo configPo;
    Module[] modules;

    protected GenerateDialog(Project project, List<DasTable> list) {
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
            map.put("author", configPo.getAuthor());
            map.put("date", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            this.list.forEach(it -> Templates.generate(map, it, this.configPo, this.configInfo));
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
