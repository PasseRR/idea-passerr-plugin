package com.github.passerr.idea.plugins.database.generator.action;

import com.github.passerr.idea.plugins.database.generator.config.po.SettingPo;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

/**
 * 动态配置部分
 * @author xiehai
 * @date 2022/06/29 14:39
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
class DynamicCondition {
    /**
     * 基础包名
     */
    String basePackage;
    /**
     * 项目根路径
     */
    String basePath;
    /**
     * 表前缀
     */
    String tablePrefix;
    /**
     * 默认覆盖文件
     */
    boolean overrideFile = true;
    /**
     * 选择模版索引
     */
    int index;

    void transferTo(SettingPo settingPo) {
        settingPo.setBasePackage(this.basePackage);
        settingPo.setBasePath(this.basePath);
        settingPo.setTablePrefix(this.tablePrefix);
        settingPo.setOverrideFile(this.overrideFile);
    }

    void setModulePath(Module module) {
        String path = ModuleUtil.getModuleDirPath(module);
        if (path.endsWith(".idea")) {
            path = path.substring(0, path.length() - 4);
        }
        this.basePath = path;
    }
}
