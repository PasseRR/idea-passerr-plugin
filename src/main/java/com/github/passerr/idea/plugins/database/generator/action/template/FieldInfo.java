package com.github.passerr.idea.plugins.database.generator.action.template;

import com.github.passerr.idea.plugins.naming.NamingStyle;
import com.github.passerr.idea.plugins.naming.NamingUtil;
import com.intellij.database.model.DasColumn;
import com.intellij.database.util.DasUtil;
import javafx.util.Pair;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

import java.util.Map;
import java.util.Optional;

/**
 * 实体字段信息
 * @author xiehai
 * @date 2022/06/22 14:26
 */
@Data
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Accessors(chain = true)
public class FieldInfo extends ClassInfo {
    /**
     * 列名
     */
    String columnName;
    /**
     * 列注释
     */
    String columnComment;
    /**
     * 列jdbc类型{@link java.sql.JDBCType} 用于特殊处理
     */
    String jdbcType;
    /**
     * 是否是主键
     */
    boolean primaryKey;

    FieldInfo() {

    }

    public String getDesc() {
        return Optional.ofNullable(this.columnComment).filter(it -> !it.isEmpty()).orElse(this.columnName);
    }

    static FieldInfo of(String databaseType, DasColumn column, Map<String, String> types) {
        FieldInfo fieldInfo =
            new FieldInfo().setColumnName(column.getName())
                .setColumnComment(column.getComment())
                .setPrimaryKey(DasUtil.isPrimary(column))
                .setJdbcType(TemplateUtil.convertToJdbcType(column.getDataType().typeName, databaseType).name());

        Pair<String, String> pair = TemplateUtil.parseClass(types.get(fieldInfo.getJdbcType()));
        if (!pair.getKey().startsWith("java.lang")) {
            fieldInfo.setPackageName(pair.getKey());
        }
        fieldInfo.setClassName(pair.getValue());
        fieldInfo.setFieldName(NamingUtil.toggle(NamingStyle.CAMEL, column.getName()));

        return fieldInfo;
    }
}
