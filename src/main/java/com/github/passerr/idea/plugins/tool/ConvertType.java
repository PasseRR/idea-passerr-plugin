package com.github.passerr.idea.plugins.tool;

import com.github.passerr.idea.plugins.NotificationThread;
import com.github.passerr.idea.plugins.mybatis.LogParser;
import com.github.passerr.idea.plugins.mybatis.SqlFormatter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.intellij.notification.Notification;
import com.intellij.ui.JBColor;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang.StringUtils;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextArea;

import javax.swing.text.BadLocationException;
import java.awt.event.MouseEvent;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.intellij.notification.NotificationType.ERROR;

/**
 * 转换类型
 * @author xiehai1* @date 2018/11/08 18:33
 * @Copyright (c) gome inc Gome Co.,LTD
 */
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
@RequiredArgsConstructor
enum ConvertType {
    /**
     * 自动格式化json
     */
    @SuppressWarnings("deprecation")
    JSON(SyntaxConstants.SYNTAX_STYLE_JSON) {
        @Override
        void handle(RSyntaxTextArea input, RSyntaxTextArea output) {
            try {
                // 可以同时解析数组或者Object
                JsonParser parser = new JsonParser();
                JsonElement element = parser.parse(input.getText());
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                output.setText(gson.toJson(element));
                // 格式化成功后定位到第一行
                output.setCaretPosition(0);
            } catch (JsonSyntaxException ex) {
                String msg = ex.getMessage();
                Matcher matcher = ERROR_PATTERN.matcher(msg);
                if (matcher.find()) {
                    // 行索引从0开始
                    int lineIndex = (Integer.parseInt(matcher.group(1))) - 1;
                    // 设置错误行背景色
                    try {
                        input.addLineHighlight(lineIndex, JBColor.RED);
                    } catch (BadLocationException ignore) {
                    }
                    // 设置提示信息
                    input.setToolTipSupplier((RTextArea rt, MouseEvent me) -> {
                        int offset = 0;
                        try {
                            offset = input.getLineOfOffset(input.viewToModel(me.getPoint()));
                        } catch (BadLocationException ignore) {
                        }
                        return offset == lineIndex ? msg : null;
                    });
                    // 定位到失败行
                    input.setCaretPosition(lineIndex);
                }
                new NotificationThread(new Notification("Json Format", "Json Format", msg, ERROR)).start();
            }
        }
    },
    /**
     * mybatis日志转可执行sql
     */
    SQL(SyntaxConstants.SYNTAX_STYLE_SQL) {
        @Override
        void handle(RSyntaxTextArea input, RSyntaxTextArea output) {
            String sql = LogParser.toSql(input.getText());
            if (StringUtils.isEmpty(sql)) {
                // 设置错误行背景色
                // 默认设置第一行
                try {
                    input.addLineHighlight(0, JBColor.RED);
                } catch (BadLocationException ignore) {
                }
                // 设置提示信息
                input.setToolTipSupplier((RTextArea rt, MouseEvent me) -> {
                    int offset = 0;
                    try {
                        offset = input.getLineOfOffset(input.viewToModel(me.getPoint()));
                    } catch (BadLocationException ignore) {
                    }
                    return offset == 0 ? "the log you input which without \"Preparing:\" or \"Parameters:\"" : null;
                });
            } else {
                output.setText(SqlFormatter.format(sql));
                // 格式化成功后定位到第一行
                output.setCaretPosition(0);
            }
        }
    },
    /**
     * 文本
     */
    TEXT(SyntaxConstants.SYNTAX_STYLE_NONE);

    String style;

    /**
     * json错误信息正则匹配
     */
    private static final Pattern ERROR_PATTERN = Pattern.compile("line (\\w+) column");

    /**
     * 对文本进行格式化
     * @param input  输入文本域
     * @param output 输出文本域
     */
    void handle(RSyntaxTextArea input, RSyntaxTextArea output) {
        // 默认不做任何处理
    }
}
