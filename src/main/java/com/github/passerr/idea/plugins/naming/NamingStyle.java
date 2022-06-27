package com.github.passerr.idea.plugins.naming;

import com.github.passerr.idea.plugins.base.constants.StringConstants;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 命名风格
 * @author xiehai
 * @date 2022/02/14 14:16
 */
@RequiredArgsConstructor
@Getter
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public enum NamingStyle {
    /**
     * 驼峰
     */
    CAMEL("驼峰(helloWorld)", 0B1) {
        @Override
        boolean match(String text) {
            return Character.isLowerCase(text.charAt(0)) && !text.contains(UNDER_SCORE) && !text.contains(MINUS);
        }

        @Override
        String pascal(String text) {
            return NamingStyle.upperFirstLetter(text);
        }

        @Override
        String toggle(String text) {
            return NamingStyle.lowerFirstLetter(text);
        }
    },
    /**
     * 帕斯卡
     */
    PASCAL("帕斯卡(HelloWorld)", 0B10) {
        @Override
        boolean match(String text) {
            return Character.isUpperCase(text.charAt(0)) && !text.contains(UNDER_SCORE) && !text.contains(MINUS);
        }
    },
    /**
     * 大写蛇形
     */
    UPPER_SNAKE("大写蛇形(HELLO_WORLD)", 0B100) {
        @Override
        boolean match(String text) {
            return text.chars().allMatch(it -> Character.isUpperCase(it) || it == UNDER_SCORE.charAt(0));
        }

        @Override
        String pascal(String text) {
            return
                Arrays.stream(text.split(UNDER_SCORE))
                    .map(String::toLowerCase)
                    .map(NamingStyle::upperFirstLetter)
                    .collect(Collectors.joining());
        }

        @Override
        String toggle(String text) {
            return NamingStyle.replaceUpperAsSpecial(text, UNDER_SCORE).toUpperCase();
        }
    },
    /**
     * 小写蛇形
     */
    LOWER_SNAKE("小写蛇形(hello_world)", 0B1000) {
        @Override
        boolean match(String text) {
            return text.chars().allMatch(it -> Character.isLowerCase(it) || it == UNDER_SCORE.charAt(0));
        }

        @Override
        String pascal(String text) {
            return
                Arrays.stream(text.split(UNDER_SCORE))
                    .map(NamingStyle::upperFirstLetter)
                    .collect(Collectors.joining());
        }

        @Override
        String toggle(String text) {
            return NamingStyle.replaceUpperAsSpecial(text, UNDER_SCORE).toLowerCase();
        }
    },
    /**
     * 大写串行
     */
    UPPER_KEBAB("大写串行(HELLO-WORLD)", 0B10000) {
        @Override
        boolean match(String text) {
            return text.chars().allMatch(it -> Character.isUpperCase(it) || it == MINUS.charAt(0));
        }

        @Override
        String pascal(String text) {
            return
                Arrays.stream(text.split(MINUS))
                    .map(String::toLowerCase)
                    .map(NamingStyle::upperFirstLetter)
                    .collect(Collectors.joining());
        }

        @Override
        String toggle(String text) {
            return NamingStyle.replaceUpperAsSpecial(text, MINUS).toUpperCase();
        }
    },
    /**
     * 小写串行
     */
    LOWER_KEBAB("小写串行(hello-world)", 0B100000) {
        @Override
        boolean match(String text) {
            return text.chars().allMatch(it -> Character.isLowerCase(it) || it == MINUS.charAt(0));
        }

        @Override
        String pascal(String text) {
            return
                Arrays.stream(text.split(MINUS))
                    .map(NamingStyle::upperFirstLetter)
                    .collect(Collectors.joining());
        }

        @Override
        String toggle(String text) {
            return NamingStyle.replaceUpperAsSpecial(text, MINUS).toLowerCase();
        }
    },
    /**
     * 未知命名规则
     */
    UNKNOWN("未知", 0B0) {
        @Override
        boolean match(String text) {
            return true;
        }
    };
    /**
     * 命名描述
     */
    final String name;
    /**
     * 位标识
     */
    final int bit;

    static final String UNDER_SCORE = StringConstants.UNDERSCORE;
    static final String MINUS = StringConstants.MINUS;

    /**
     * 是否匹配当前风格
     * @param text 命名文本
     * @return 是否匹配
     */
    abstract boolean match(String text);

    /**
     * 基础风格以帕斯卡为准再通过帕斯卡转换
     * @param text 当前命名
     * @return 转为帕斯卡后的命名
     */
    String pascal(String text) {
        return text;
    }

    /**
     * 从帕斯卡转为当前风格方法
     * @param text 帕斯卡命名
     * @return 当前命名风格
     */
    String toggle(String text) {
        return text;
    }

    /**
     * 大写字母替换为特殊字符串
     * @param text 当前命名
     * @return 替换后命名
     */
    static String replaceUpperAsSpecial(String text, String special) {
        String s = text.replaceAll("[A-Z]", special + "$0");
        return Character.isUpperCase(text.charAt(0)) ? s.substring(1) : s;
    }

    /**
     * 大写第一个字母
     * @param text 字符串
     * @return 大写后的字符串
     */
    static String upperFirstLetter(String text) {
        return NamingStyle.handleFirstLetter(text, Character::toUpperCase);
    }

    /**
     * 小写第一个字母
     * @param text 字符串
     * @return 小写后的字符串
     */
    static String lowerFirstLetter(String text) {
        return NamingStyle.handleFirstLetter(text, Character::toLowerCase);
    }

    /**
     * 对第一个字母做转换
     * @param text     字符串
     * @param function 转换方法
     * @return 转换后的字符串
     */
    static String handleFirstLetter(String text, Function<Character, Character> function) {
        char[] chars = text.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            // 直到第一个是字母为止
            if (Character.isLetter(chars[i])) {
                chars[i] = function.apply(chars[i]);
                break;
            }
        }
        return new String(chars);
    }
}
