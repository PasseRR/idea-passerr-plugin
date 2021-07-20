package com.github.passerr.idea.plugins.spring.web;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

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
    static String format(String template, Map<?, ?> map) {
        VelocityContext context = new VelocityContext(map);
        StringWriter writer = new StringWriter();
        Velocity.evaluate(context, writer, "eval", template);

        return writer.toString();
    }
}
