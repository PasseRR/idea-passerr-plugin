package com.github.passerr.idea.plugins.database.generator.action;

import com.github.passerr.idea.plugins.base.constants.StringConstants;
import com.github.passerr.idea.plugins.database.generator.config.po.SettingPo;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.io.IOException;
import java.nio.file.Paths;

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

    void transerTo(SettingPo settingPo) {
        settingPo.setBasePackage(this.basePackage);
        settingPo.setBasePath(this.basePath);
        settingPo.setTablePrefix(this.tablePrefix);
        settingPo.setOverrideFile(this.overrideFile);
    }

    void setModulePath(int index, String basePath) {
        String prefix = "../";
        if (index == 0) {
            prefix += prefix;
        }
        try {
            this.basePath = Paths.get(basePath, prefix).toRealPath().toString();
        } catch (IOException ignore) {
            this.basePath = StringConstants.EMPTY;
        }
    }
}
