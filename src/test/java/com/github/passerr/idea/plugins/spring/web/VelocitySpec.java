package com.github.passerr.idea.plugins.spring.web;

import com.github.passerr.idea.plugins.base.utils.ResourceUtil;
import com.github.passerr.idea.plugins.base.utils.VelocityUtil;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * {@link VelocityUtil}
 * @author xiehai
 * @date 2021/07/20 14:59
 * @Copyright(c) tellyes tech. inc. co.,ltd
 */
public class VelocitySpec {
    @Test
    public void testComment() {
        String template = "## 这是一行注释\n" +
            "## 这是另一行注释\n" +
            "#[[##]]# 这个是标题";

        System.out.println(VelocityUtil.format(new StringBuilder(template), null));
    }

    @Test
    public void testXml() {
        String s = ResourceUtil.readAsString("/generator/mapper-xml.vm");
        Map<String, Object> map = new HashMap<String, Object>() {
            {
                this.put("mapper", new HashMap<String, Object>() {{
                    this.put("fullClassName", "com.hightop.abc.dao.TestMapper");
                }});
            }
        };

        System.out.println(VelocityUtil.format(new StringBuilder(s), map));
    }
}
