package com.github.passerr.idea.plugins.database.generator.action.template;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 表实体信息
 * @author xiehai
 * @date 2022/06/22 14:24
 */
@Data
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EntityInfo extends ClassInfo {
    /**
     * 基础名称 没有加后缀的名称
     */
    @Getter(AccessLevel.PACKAGE)
    String baseName;
    /**
     * 表注释
     */
    String tableComment;
    /**
     * 表名
     */
    String tableName;
    /**
     * 列名列表
     */
    List<FieldInfo> fields = new ArrayList<>();
    /**
     * 主键列信息
     */
    FieldInfo pk;
    /**
     * 小写串行命名
     */
    String kebabName;
    /**
     * 小写蛇形命名
     */
    String snakeName;

    EntityInfo() {
    }

    public String getDesc() {
        return Optional.ofNullable(this.tableComment).filter(it -> !it.isEmpty()).orElse(this.tableName);
    }

    /**
     * 添加字段
     * @param fieldInfo {@link FieldInfo}
     */
    public void addField(FieldInfo fieldInfo) {
        this.fields.add(fieldInfo);
    }
}
