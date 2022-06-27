package com.github.passerr.idea.plugins.database.generator.action.template;

import com.intellij.database.model.DasColumn;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

import java.sql.JDBCType;
import java.sql.Types;
import java.util.Objects;
import java.util.Optional;

/**
 * 实体字段信息
 * @author xiehai
 * @date 2022/06/22 14:26
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Accessors(chain = true)
public class FieldInfo {
    /**
     * 列名
     */
    String columnName;
    /**
     * 列注释
     */
    String columnComment;
    /**
     * 字段名
     */
    String camelName;
    /**
     * 列java类型
     */
    String javaTypeName;
    /**
     * 列jdbc类型{@link java.sql.Types} 用于特殊处理
     */
    int jdbcType;
    /**
     * 是否是主键
     */
    boolean primaryKey;
    /**
     * 额外的导入信息
     */
    String extraImport;

    FieldInfo() {
    }

    public String getDesc() {
        return Optional.ofNullable(this.columnComment).filter(it -> !it.isEmpty()).orElse(this.columnName);
    }

    static FieldInfo of(DasColumn column) {
        // TODO 类型转换
        return
            new FieldInfo().setColumnName(column.getName())
                .setColumnComment(column.getName());
    }

    static int convertJdbcType(String type, String databaseType) {
        if (Objects.isNull(type) || type.isEmpty()) {
            return Types.OTHER;
        }

        String fixed = type.toUpperCase();
        if (fixed.contains(JDBCType.BIGINT.name())) {
            return Types.BIGINT;
        } else if (fixed.contains(JDBCType.TINYINT.name())) {
            return Types.TINYINT;
        } else if (fixed.contains(JDBCType.LONGVARBINARY.name())) {
            return Types.LONGVARBINARY;
        } else if (fixed.contains(JDBCType.VARBINARY.name())) {
            return Types.VARBINARY;
        } else if (fixed.contains(JDBCType.LONGVARCHAR.name())) {
            return Types.LONGVARCHAR;
        } else if (fixed.contains(JDBCType.SMALLINT.name())) {
            return Types.SMALLINT;
        } else if (fixed.contains("DATETIME")) {
            return Types.TIMESTAMP;
        } else if (fixed.equals(JDBCType.DATE.name()) && "Oracle".equals(databaseType)) {
            return Types.TIMESTAMP;
        } else if (fixed.contains("NUMBER")) {
            return Types.DECIMAL;
        } else if (fixed.contains(JDBCType.BOOLEAN.name())) {
            return Types.BOOLEAN;
        } else if (fixed.contains(JDBCType.BINARY.name())) {
            return Types.VARBINARY;
        } else if (fixed.contains(JDBCType.BIT.name())) {
            return Types.BIT;
        } else if (fixed.contains("BOOL")) {
            return Types.BOOLEAN;
        } else if (fixed.contains(JDBCType.DATE.name())) {
            return Types.DATE;
        } else if (fixed.contains(JDBCType.TIMESTAMP.name())) {
            return Types.TIMESTAMP;
        } else if (fixed.contains("TIME")) {
            return Types.TIME;
        } else if (!fixed.contains(JDBCType.REAL.name()) && !fixed.contains("NUMBER")) {
            if (fixed.contains(JDBCType.FLOAT.name())) {
                return Types.FLOAT;
            } else if (fixed.contains(JDBCType.DOUBLE.name())) {
                return Types.DOUBLE;
            } else if (JDBCType.CHAR.name().equals(fixed)) {
                return Types.CHAR;
            } else if (fixed.equals("INT")) {
                return Types.INTEGER;
            } else if (fixed.contains(JDBCType.DECIMAL.name())) {
                return Types.DECIMAL;
            } else if (fixed.contains(JDBCType.NUMERIC.name())) {
                return Types.NUMERIC;
            } else if (!fixed.contains(JDBCType.CHAR.name()) && !fixed.contains("TEXT")) {
                if (fixed.contains(JDBCType.BLOB.name())) {
                    return Types.BLOB;
                } else if (fixed.contains(JDBCType.CLOB.name())) {
                    return Types.CLOB;
                } else {
                    return fixed.contains("REFERENCE") ? Types.REF : Types.OTHER;
                }
            } else {
                return Types.VARCHAR;
            }
        } else {
            return Types.REAL;
        }
    }
}
