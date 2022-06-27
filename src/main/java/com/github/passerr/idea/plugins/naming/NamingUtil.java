package com.github.passerr.idea.plugins.naming;

/**
 * 对外名称格式化工具
 * @author xiehai
 * @date 2022/06/27 17:00
 */
public interface NamingUtil {
    /**
     * 将pascal转为给定类型
     * @param target 目标命名类型
     * @param text   源帕斯卡名称
     * @return 目标命名
     */
    static String toggle(NamingStyle target, String text) {
        if (NamingStyle.PASCAL.match(text)) {
            return target.toggle(text);
        }

        return target.toggle(NamingNode.guess(text).pascal(text));
    }
}
