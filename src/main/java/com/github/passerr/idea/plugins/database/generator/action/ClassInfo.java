package com.github.passerr.idea.plugins.database.generator.action;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

/**
 * 类基础信息
 * @author xiehai
 * @date 2022/06/22 14:58
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
class ClassInfo {
    String packageName;
    /**
     * 实体类名称
     */
    String className;

    public String getFullClassName() {
        return String.format("%s.%s", this.packageName, this.className);
    }
}
