package com.github.passerr.idea.plugins.tool

import org.fife.ui.rsyntaxtextarea.SyntaxConstants

/**
 * 转换类型
 * @author xiehai1
 * @date 2018/11/08 18:33
 * @Copyright ( c ) gome inc Gome Co.,LTD
 */
enum ConvertType {
    /**
     * 自动格式化json
     */
    JSON(SyntaxConstants.SYNTAX_STYLE_JSON),
    /**
     * mybatis日志转可执行sql
     */
    SQL(SyntaxConstants.SYNTAX_STYLE_SQL)

    String style

    ConvertType(String style) {
        this.style = style
    }
}
