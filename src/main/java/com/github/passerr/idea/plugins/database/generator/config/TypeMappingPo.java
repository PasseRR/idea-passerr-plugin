package com.github.passerr.idea.plugins.database.generator.config;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.JDBCType;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * jdbc与java类型映射
 * @author xiehai
 * @date 2022/06/24 10:34
 */
@Data
@Accessors(chain = true)
public class TypeMappingPo {
    String jdbcType;
    String javaType;

    TypeMappingPo deepCopy() {
        return new TypeMappingPo().setJavaType(this.javaType).setJdbcType(this.jdbcType);
    }

    /**
     * 默认jdb类型支持
     * @return {@link List}
     */
    static List<TypeMappingPo> defaultMappings() {
        List<TypeMappingPo> list = new ArrayList<>();
        BiConsumer<Class<?>, JDBCType> put = (clazz, type) ->
            // byte[]特殊处理
            list.add(
                new TypeMappingPo().setJdbcType(type.name())
                    .setJavaType(clazz == byte[].class ? "byte[]" : clazz.getName())
            );
        BiConsumer<Class<?>, List<JDBCType>> puts = (clazz, types) -> types.forEach(it -> put.accept(clazz, it));
        puts.accept(Boolean.class, Arrays.asList(JDBCType.BIT, JDBCType.BOOLEAN));
        puts.accept(Integer.class, Arrays.asList(JDBCType.TINYINT, JDBCType.SMALLINT, JDBCType.INTEGER));
        put.accept(BigInteger.class, JDBCType.BIGINT);
        puts.accept(Float.class, Arrays.asList(JDBCType.FLOAT, JDBCType.REAL));
        puts.accept(Double.class, Arrays.asList(JDBCType.DOUBLE, JDBCType.NUMERIC));
        put.accept(BigDecimal.class, JDBCType.DECIMAL);
        puts.accept(
            String.class,
            Arrays.asList(
                JDBCType.CHAR, JDBCType.VARCHAR, JDBCType.LONGVARCHAR,
                JDBCType.NCHAR, JDBCType.NVARCHAR, JDBCType.LONGNVARCHAR
            )
        );
        puts.accept(
            LocalDateTime.class,
            Arrays.asList(JDBCType.DATE, JDBCType.TIMESTAMP, JDBCType.TIMESTAMP_WITH_TIMEZONE)
        );
        puts.accept(LocalTime.class, Arrays.asList(JDBCType.TIME, JDBCType.TIME_WITH_TIMEZONE));
        puts.accept(
            byte[].class,
            Arrays.asList(
                JDBCType.BINARY, JDBCType.VARBINARY, JDBCType.LONGVARBINARY,
                JDBCType.CLOB, JDBCType.NCLOB, JDBCType.BLOB
            )
        );
        put.accept(String.class, JDBCType.OTHER);

        return list;
    }
}
