package com.github.passerr.idea.plugins.base.utils;

/**
 * {@link StringBuilder}工具
 * @author xiehai
 * @date 2022/06/24 09:35
 */
public interface StringBuilderUtil {
    /**
     * sb以字符串重新设置
     * @param sb       {@link StringBuilder}
     * @param sequence 字符串
     */
    static void reset(StringBuilder sb, CharSequence sequence) {
        sb.setLength(0);
        sb.append(sequence);
    }
}
