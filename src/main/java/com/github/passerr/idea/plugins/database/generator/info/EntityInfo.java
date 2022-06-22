package com.github.passerr.idea.plugins.database.generator.info;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;

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
    List<String> tableColumns;

    public String getDesc() {
        return Optional.ofNullable(this.tableComment).filter(it -> !it.isEmpty()).orElse(this.tableName);
    }
}
