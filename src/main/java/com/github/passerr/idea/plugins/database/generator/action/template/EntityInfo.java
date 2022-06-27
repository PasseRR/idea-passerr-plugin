package com.github.passerr.idea.plugins.database.generator.action.template;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    EntityInfo() {
    }

    public String getDesc() {
        return Optional.ofNullable(this.tableComment).filter(it -> !it.isEmpty()).orElse(this.tableName);
    }

    /**
     * 字段额外的导入包信息
     * @return {@link List}
     */
    public List<String> getFieldsImport() {
        return fields.stream().map(FieldInfo::getExtraImport).distinct().sorted().collect(Collectors.toList());
    }

    /**
     * 添加字段
     * @param fieldInfo {@link FieldInfo}
     */
    public void addField(FieldInfo fieldInfo) {
        this.fields.add(fieldInfo);
    }
}
