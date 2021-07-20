package com.github.passerr.idea.plugins.spring.web;

import org.junit.Test;

/**
 * {@link VelocityUtil}
 * @author xiehai
 * @date 2021/07/20 14:59
 * @Copyright(c) tellyes tech. inc. co.,ltd
 */
public class VelocitySpec {
    @Test
    public void testComment(){
        String template = "## 这是一行注释\n" +
            "## 这是另一行注释\n" +
            "#[[##]]# 这个是标题";

        System.out.println(VelocityUtil.format(new StringBuilder(template), null));
    }
}
