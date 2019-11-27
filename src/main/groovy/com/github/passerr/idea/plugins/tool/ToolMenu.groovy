package com.github.passerr.idea.plugins.tool

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea

/**
 * 菜单
 * @Copyright (c)tellyes tech. inc. co.,ltd
 * @date 2019/11/27 09:53
 * @author xiehai
 */
enum ToolMenu {
    /**
     * main menu
     */
    JSON("Json格式化", ConvertType.JSON),
    SQL("Mybatis日志转Sql", ConvertType.SQL),
    ENCODE("编码", null),

    /**
     * sub menu
     */
    BASE64_ENCRYPTION("base64加密", ConvertType.TEXT) {
        @Override
        void handle(RSyntaxTextArea textArea) {
            textArea.setText(textArea.getText().decodeBase64())
        }
    }

    String name
    ConvertType type

    ToolMenu(String name, ConvertType type) {
        this.name = name
        this.type = type
    }


    @Override
    String toString() {
        return this.name
    }

    /**
     * 默认文本域操作
     * @param textArea
     */
    void handle(RSyntaxTextArea textArea) {
        // 默认对应类型格式化
        this.type.handle(textArea)
    }
}