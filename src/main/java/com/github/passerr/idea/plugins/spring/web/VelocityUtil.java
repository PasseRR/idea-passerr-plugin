package com.github.passerr.idea.plugins.spring.web;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.log.NullLogChute;

import java.io.StringWriter;
import java.util.Map;

/**
 * velocity工具类
 * @author xiehai
 * @date 2021/07/09 11:39
 * @Copyright(c) tellyes tech. inc. co.,ltd
 */
public interface VelocityUtil {
    /**
     * 模版替换
     * @param template 模版
     * @param map      变量
     * @return 替换后文本
     */
    static String format(StringBuilder template, Map<?, ?> map) {
        VelocityContext context = new VelocityContext(map);
        StringWriter writer = new StringWriter();
        // 不打印velocity日志
        Velocity.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS, NullLogChute.class.getName());
        Velocity.init();
        Velocity.evaluate(context, writer, "eval", template.toString());

        return writer.toString();
    }
}
