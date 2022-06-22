package com.github.passerr.idea.plugins.base;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.jetbrains.java.generate.velocity.VelocityFactory;

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
        StringWriter writer = new StringWriter();
        VelocityEngine velocity = VelocityFactory.getVelocityEngine();
        velocity.evaluate(
            new VelocityContext(map),
            writer,
            VelocityUtil.class.getName(),
            template.toString()
        );

        return writer.toString();
    }
}
