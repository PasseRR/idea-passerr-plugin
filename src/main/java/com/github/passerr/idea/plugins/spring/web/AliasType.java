package com.github.passerr.idea.plugins.spring.web;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

/**
 * 别名类型
 * @author xiehai
 * @date 2021/07/05 09:45
 * @Copyright(c) tellyes tech. inc. co.,ltd
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Getter
public enum AliasType {
    /**
     * 布尔类型
     */
    BOOLEAN("boolean") {
        @Override
        public Object deserialize(String value) {
            return Boolean.valueOf(value);
        }
    },
    /**
     * 整型
     */
    INT("int") {
        @Override
        public Object deserialize(String value) {
            try {
                return Integer.valueOf(value);
            } catch (NumberFormatException e) {
                return 1;
            }
        }
    },
    /**
     * 浮点型
     */
    FLOAT("float") {
        @Override
        public Object deserialize(String value) {
            try {
                return Double.valueOf(value);
            } catch (NumberFormatException e) {
                return 2.2;
            }
        }
    },
    /**
     * 字符串
     */
    STRING("string") {
        @Override
        public Object deserialize(String value) {
            return value;
        }
    };

    String type;

    /**
     * 位置别名类型
     */
    public static final String UNKNOWN_ALIAS = "object";

    public abstract Object deserialize(String value);
}
