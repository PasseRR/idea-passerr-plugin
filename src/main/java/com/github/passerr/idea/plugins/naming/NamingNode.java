package com.github.passerr.idea.plugins.naming;

import java.util.Objects;

/**
 * 双向循环链表
 * @author xiehai
 * @date 2022/02/14 15:26
 */
class NamingNode {
    private NamingNode next;
    private NamingStyle style;
    private static final NamingNode HEAD = new NamingNode();

    static {
        // 构建循环链表
        NamingNode node = HEAD;
        NamingStyle[] values = NamingStyle.values();
        for (int i = 0, len = values.length; i < len; i++) {
            node.style = values[i];
            node.next = i == len - 1 ? HEAD : new NamingNode();
            node = node.next;
        }
    }

    NamingNode() {
    }

    /**
     * 命名转换
     * @param text 命名
     * @param bits 当前配置支持风格位标识
     * @return 转换后的命名
     */
    static String convert(String text, int bits) {
        NamingStyle current = null, target;
        for (NamingNode node = HEAD; ; node = node.next) {
            if (Objects.nonNull(current) && (node.style.bit & bits) > 0) {
                target = node.style;
                break;
            }

            if (Objects.isNull(current) && node.style.match(text)) {
                current = node.style;
            }
        }

        return target.toggle(current.pascal(text));
    }
}
