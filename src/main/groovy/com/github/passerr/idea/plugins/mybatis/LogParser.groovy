package com.github.passerr.idea.plugins.mybatis

import com.github.passerr.idea.plugins.NotificationThread
import com.intellij.notification.Notification
import com.intellij.notification.NotificationType

import static com.github.passerr.idea.plugins.mybatis.LogConstants.*

/**
 * mybatis日志解析器
 * @author xiehai1
 * @date 2018/11/08 11:49
 * @Copyright ( c ) gome inc Gome Co.,LTD
 */
class LogParser {
    private LogParser() {

    }
    /**
     * 日志解析为可执行的sql语句
     * @param log 日志内容
     * @return sql
     */
    static String toSql(String log) {
        String sqlLine = null, valueLine = null
        for (String line : log.split(BREAK_LINE)) {
            if (line.contains(PREFIX_SQL)) {
                sqlLine = line
            } else if (line.contains(PREFIX_PARAMS)) {
                valueLine = line
            } else if (line.contains(PREFIX_PARAMS_WITHOUT_SPACE)) {
                // 没有参数的sql 自动补齐一个空格
                valueLine = line + SPACE
            }
        }

        // 是否找到包含sql及参数的日志
        if (Objects.isNull(sqlLine)) {
            // 提示信息
            new NotificationThread(
                new Notification(
                    "Copy As Executable Sql",
                    "Copy As Executable Sql",
                    "selected log without \"Preparing:\" line, nothing will send to clipboard!",
                    NotificationType.WARNING
                )
            ).start()

            return EMPTY
        } else if (Objects.isNull(valueLine)) {
            // 提示信息
            new NotificationThread(
                new Notification(
                    "Copy As Executable Sql",
                    "Copy As Executable Sql",
                    "selected log without \"Parameters:\" line, nothing will send to clipboard!",
                    NotificationType.WARNING
                )
            ).start()

            return EMPTY
        }

        // 带占位符的sql
        int sqlPrefixIndex = sqlLine.indexOf(PREFIX_SQL)
        String originSql = sqlLine.substring(sqlPrefixIndex + PREFIX_SQL.length(), sqlLine.length())
        // 参数列表
        int paramPrefixIndex = valueLine.indexOf(PREFIX_PARAMS)
        String paramValues = valueLine.substring(paramPrefixIndex + PREFIX_PARAMS.length(), valueLine.length())

        List<String> originSqlSections = originSql.split(PARAM_PLACEHOLDER)
        List<String> paramValuesSections = paramValues.split(PARAM_SEPARATOR)
        int i = 0
        StringBuilder sb = new StringBuilder()
        while (originSqlSections.size() > i && paramValuesSections.size() > i) {
            sb.append(originSqlSections.get(i))
            sb.append(parseParam(paramValuesSections.get(i)))
            i++
        }

        while (originSqlSections.size() > i) {
            sb.append(originSqlSections.get(i))
            i++
        }

        sb.toString()
    }

    /**
     * 解析参数值
     * @param paramValue 参数值字符串
     * @return 参数值
     */
    private static String parseParam(String paramValue) {
        // 如果是空字符串直接返回
        if (paramValue.size() == 0) {
            return EMPTY
        }

        // 如果是null 直接返回null
        if (paramValue.trim() == NULL) {
            return NULL
        }

        // 括号的索引
        int lastLeftBracketIndex = paramValue.lastIndexOf(LEFT_BRACKET)
        int lastRightBracketIndex = paramValue.lastIndexOf(RIGHT_BRACKET)
        // 参数值
        String param = paramValue.substring(0, lastLeftBracketIndex)
        // 参数类型
        String type = paramValue.substring(lastLeftBracketIndex + 1, lastRightBracketIndex)

        return NON_QUOTED_TYPES.contains(type) ? param : String.format("'%s'", param)
    }
}
