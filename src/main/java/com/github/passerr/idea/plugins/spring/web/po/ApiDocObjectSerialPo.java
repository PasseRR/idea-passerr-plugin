package com.github.passerr.idea.plugins.spring.web.po;

import com.github.passerr.idea.plugins.spring.web.AliasType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 对象序列化
 * @author xiehai
 * @date 2021/06/30 19:29
 * @Copyright(c) tellyes tech. inc. co.,ltd
 */
@Data
@EqualsAndHashCode
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class ApiDocObjectSerialPo {
    /**
     * 类型全名
     */
    String type;
    /**
     * 别名
     */
    String alias;
    /**
     * 默认值
     */
    String value;

    public ApiDocObjectSerialPo deepCopy() {
        return new ApiDocObjectSerialPo(this.type, this.alias, this.value);
    }

    /**
     * 默认值设置
     * @return {@link List}
     */
    static List<ApiDocObjectSerialPo> defaultObjects() {
        List<ApiDocObjectSerialPo> objects = new ArrayList<>();
        Arrays.asList("boolean", "java.lang.Boolean")
            .forEach(it -> objects.add(new ApiDocObjectSerialPo(it, AliasType.BOOLEAN.getType(), "true")));

        Arrays.asList(
            "byte", "java.lang.Byte",
            "short", "java.lang.Short",
            "char", "java.lang.Character",
            "int", "java.lang.Integer"
        ).forEach(it -> objects.add(new ApiDocObjectSerialPo(it, AliasType.INT.getType(), "1024")));

        Arrays.asList(
            "float", "java.lang.Float",
            "double", "java.lang.Double"
        ).forEach(it -> objects.add(new ApiDocObjectSerialPo(it, AliasType.FLOAT.getType(), "102.4")));

        Arrays.asList(
            "long",
            "java.lang.Long",
            "java.math.BigInteger"
        ).forEach(it -> objects.add(new ApiDocObjectSerialPo(it, AliasType.STRING.getType(), "2048")));

        Arrays.asList(
            "java.util.Date",
            "java.time.LocalDateTime"
        ).forEach(it ->
            objects.add(
                new ApiDocObjectSerialPo(
                    it,
                    AliasType.STRING.getType(),
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                )
            )
        );
        objects.add(
            new ApiDocObjectSerialPo(
                "java.time.LocalDate",
                AliasType.STRING.getType(),
                LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            )
        );
        objects.add(
            new ApiDocObjectSerialPo(
                "java.time.LocalTime",
                AliasType.STRING.getType(),
                LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
            )
        );
        objects.add(new ApiDocObjectSerialPo("java.math.BigDecimal", AliasType.STRING.getType(), "204.8"));
        objects.add(new ApiDocObjectSerialPo("java.lang.String", AliasType.STRING.getType(), "String"));


        return objects;
    }
}
