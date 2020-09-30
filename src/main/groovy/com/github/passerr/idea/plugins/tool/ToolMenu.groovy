package com.github.passerr.idea.plugins.tool

import org.apache.commons.codec.digest.DigestUtils
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea

import java.nio.charset.StandardCharsets

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
    ENCODE("编/解码", null),
    ENCRYPT("加/解密", null),

    /**
     * sub menu
     */
    URL_DECODE("url解码", ConvertType.TEXT){
        @Override
        void handle(RSyntaxTextArea input, RSyntaxTextArea output) {
            output.setText(URLDecoder.decode(input.getText(), StandardCharsets.UTF_8.name()))
        }
    },
    URL_ENCODE("url编码", ConvertType.TEXT){
        @Override
        void handle(RSyntaxTextArea input, RSyntaxTextArea output) {
            output.setText(URLEncoder.encode(input.getText(), StandardCharsets.UTF_8.name()))
        }
    },
    MD5_ENCRYPTION("md5加密", ConvertType.TEXT){
        @Override
        void handle(RSyntaxTextArea input, RSyntaxTextArea output) {
            output.setText(DigestUtils.md5Hex(input.getText()))
        }
    },
    BASE64_DECRYPTION("base64解密", ConvertType.TEXT){
        @Override
        void handle(RSyntaxTextArea input, RSyntaxTextArea output) {
            try {
                output.setText(new String(input.getText().decodeBase64()))
            } catch (Exception ignore) {

            }
        }
    },
    BASE64_ENCRYPTION("base64加密", ConvertType.TEXT){
        @Override
        void handle(RSyntaxTextArea input, RSyntaxTextArea output) {
            output.setText(input.getText().bytes.encodeBase64().toString())
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
     * @param input 输入文本域
     * @param output 输出文本域
     */
    void handle(RSyntaxTextArea input, RSyntaxTextArea output) {
        // 默认对应类型格式化
        this.type.handle(input, output)
    }
}