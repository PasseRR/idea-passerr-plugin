package com.github.passerr.idea.plugins.mybatis;

import com.github.passerr.idea.plugins.base.constants.StringConstants;

import java.util.Arrays;
import java.util.List;

/**
 * 日志常量
 * @author xiehai1
 * @date 2018/11/08 13:12
 * @Copyright (c) gome inc Gome Co.,LTD
 */
interface LogConstants {
    /**
     * 空格
     */
    String SPACE = StringConstants.SPACE;
    /**
     * sql语句前缀
     */
    String PREFIX_SQL = "Preparing: ";
    /**
     * 参数占位符
     */
    String PARAM_PLACEHOLDER = "\\?";
    /**
     * 换行符
     */
    String BREAK_LINE = "\n";
    /**
     * 无sql参数前缀
     */
    String PREFIX_PARAMS_WITHOUT_SPACE = "Parameters:";
    /**
     * sql参数前缀
     */
    String PREFIX_PARAMS = PREFIX_PARAMS_WITHOUT_SPACE + SPACE;
    /**
     * 参数值分隔符
     */
    String PARAM_SEPARATOR = ", ";
    /**
     * 左括号
     */
    String LEFT_BRACKET = "(";
    /**
     * 右括号
     */
    String RIGHT_BRACKET = ")";
    /**
     * 空字符串
     */
    String EMPTY = StringConstants.EMPTY;
    /**
     * null值
     */
    String NULL = "null";
    /**
     * 不需要加单引号的类型
     */
    List<String> NON_QUOTED_TYPES = Arrays.asList("Integer", "Long", "Double", "Float", "Boolean");
}
