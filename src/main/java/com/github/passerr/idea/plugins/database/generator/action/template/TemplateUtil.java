package com.github.passerr.idea.plugins.database.generator.action.template;

import com.github.passerr.idea.plugins.base.constants.StringConstants;
import javafx.util.Pair;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.sql.JDBCType;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 模板工具
 * @author xiehai
 * @date 2022/06/28 09:49
 */
interface TemplateUtil {
    /**
     * 数据库数据类型转为jdbc映射
     * @param type         数据类型
     * @param databaseType 数据库类型
     * @return {@link JDBCType}
     */
    static JDBCType convertToJdbcType(String type, String databaseType) {
        if (Objects.isNull(type) || type.isEmpty()) {
            return JDBCType.OTHER;
        }

        String fixed = type.toUpperCase();
        if (fixed.contains(JDBCType.BIGINT.name())) {
            return JDBCType.BIGINT;
        } else if (fixed.contains(JDBCType.TINYINT.name())) {
            return JDBCType.TINYINT;
        } else if (fixed.contains(JDBCType.LONGVARBINARY.name())) {
            return JDBCType.LONGVARBINARY;
        } else if (fixed.contains(JDBCType.VARBINARY.name())) {
            return JDBCType.VARBINARY;
        } else if (fixed.contains(JDBCType.LONGVARCHAR.name())) {
            return JDBCType.LONGVARCHAR;
        } else if (fixed.contains(JDBCType.SMALLINT.name())) {
            return JDBCType.SMALLINT;
        } else if (fixed.contains("DATETIME")) {
            return JDBCType.TIMESTAMP;
        } else if (fixed.equals(JDBCType.DATE.name()) && "Oracle".equals(databaseType)) {
            return JDBCType.TIMESTAMP;
        } else if (fixed.contains("NUMBER")) {
            return JDBCType.DECIMAL;
        } else if (fixed.contains(JDBCType.BOOLEAN.name())) {
            return JDBCType.BOOLEAN;
        } else if (fixed.contains(JDBCType.BINARY.name())) {
            return JDBCType.VARBINARY;
        } else if (fixed.contains(JDBCType.BIT.name())) {
            return JDBCType.BIT;
        } else if (fixed.contains("BOOL")) {
            return JDBCType.BOOLEAN;
        } else if (fixed.contains(JDBCType.DATE.name())) {
            return JDBCType.DATE;
        } else if (fixed.contains(JDBCType.TIMESTAMP.name())) {
            return JDBCType.TIMESTAMP;
        } else if (fixed.contains("TIME")) {
            return JDBCType.TIME;
        } else if (!fixed.contains(JDBCType.REAL.name()) && !fixed.contains("NUMBER")) {
            if (fixed.contains(JDBCType.FLOAT.name())) {
                return JDBCType.FLOAT;
            } else if (fixed.contains(JDBCType.DOUBLE.name())) {
                return JDBCType.DOUBLE;
            } else if (JDBCType.CHAR.name().equals(fixed)) {
                return JDBCType.CHAR;
            } else if (fixed.equals("INT")) {
                return JDBCType.INTEGER;
            } else if (fixed.contains(JDBCType.DECIMAL.name())) {
                return JDBCType.DECIMAL;
            } else if (fixed.contains(JDBCType.NUMERIC.name())) {
                return JDBCType.NUMERIC;
            } else if (!fixed.contains(JDBCType.CHAR.name()) && !fixed.contains("TEXT")) {
                if (fixed.contains(JDBCType.BLOB.name())) {
                    return JDBCType.BLOB;
                } else if (fixed.contains(JDBCType.CLOB.name())) {
                    return JDBCType.CLOB;
                } else {
                    return fixed.contains("REFERENCE") ? JDBCType.REF : JDBCType.OTHER;
                }
            } else {
                return JDBCType.VARCHAR;
            }
        } else {
            return JDBCType.REAL;
        }
    }

    /**
     * 全路径类名解析为包名和类名称
     * @param name 全路径类名
     * @return {@link Pair}
     */
    static Pair<String, String> parseClass(String name) {
        int index = name.lastIndexOf(StringConstants.POINT);
        if (index < 0) {
            return new Pair<>(StringConstants.EMPTY, name);
        }

        return new Pair<>(name.substring(0, index), name.substring(index + 1));
    }

    /**
     * 包导入模板字符串
     * @param list 需要导入的包名
     * @return 格式化包名导入
     */
    static String imports(List<String> list) {
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }

        String collect = list.stream()
            .filter(StringUtils::isNotBlank)
            .map(it -> String.format("import %s;", it))
            .collect(Collectors.joining(StringConstants.LF));

        return Optional.of(collect).filter(StringUtils::isNotBlank).orElse(null);
    }

    /**
     * 包导入模板字符串
     * @param s 单个包名
     * @return 格式化包名导入
     */
    static String imports(String s) {
        return TemplateUtil.imports(Collections.singletonList(s));
    }
}
