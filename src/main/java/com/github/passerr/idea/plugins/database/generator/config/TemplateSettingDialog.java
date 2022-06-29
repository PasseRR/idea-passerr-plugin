package com.github.passerr.idea.plugins.database.generator.config;

import com.github.passerr.idea.plugins.database.generator.config.po.TemplatePo;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;

/**
 * 模版设置弹窗
 * @author xiehai
 * @date 2022/06/29 09:55
 */
public class TemplateSettingDialog extends DialogWrapper {
    TemplatePo copy;

    protected TemplateSettingDialog(TemplatePo copy) {
        super(true);
        this.copy = copy;
        super.setTitle("模版设置");
        super.setOKButtonText("确定");
        super.setCancelButtonText("取消");
        super.init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return Views.tabsView(this.copy.getDetail());
    }
}
