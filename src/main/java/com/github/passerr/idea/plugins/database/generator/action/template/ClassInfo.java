package com.github.passerr.idea.plugins.database.generator.action.template;

import com.github.passerr.idea.plugins.naming.NamingStyle;
import com.github.passerr.idea.plugins.naming.NamingUtil;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
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
    /**
     * 实体作为属性/参数是的名字
     */
    @Setter(AccessLevel.PACKAGE)
    String fieldName;
    /**
     * 需要导入的包名
     */
    String imports;

    public String getFullClassName() {
        return String.format("%s.%s", this.packageName, this.className);
    }

    public void setClassName(String className) {
        // 类名必为帕斯卡命名
        this.className = className;
        this.fieldName = NamingUtil.toggle(NamingStyle.CAMEL, this.className);
    }
}
