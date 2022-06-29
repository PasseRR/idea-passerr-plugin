package com.github.passerr.idea.plugins.database.generator.template.po;

import com.github.passerr.idea.plugins.base.constants.StringConstants;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Optional;

/**
 * 配置工具类
 * @author xiehai
 * @date 2022/06/27 17:13
 */
interface DialogConfigUtil {
    /**
     * 两个包路径合并
     * @param a a包
     * @param b b包
     * @return 合并后的路径节点
     */
    static String[] merge(String a, String b) {
        String name = String.format(
            "%s.%s", Optional.ofNullable(a).orElse(StringConstants.EMPTY),
            Optional.ofNullable(b).orElse(StringConstants.EMPTY)
        );

        return
            Arrays.stream(name.split("\\."))
                .map(String::trim)
                .filter(it -> !it.isEmpty())
                .toArray(String[]::new);
    }

    /**
     * 两个包路径合并
     * @param a a包
     * @param b b包
     * @return 合并后的包名
     */
    static String mergePackage(String a, String b) {
        return String.join(StringConstants.POINT, DialogConfigUtil.merge(a, b));
    }

    /**
     * 路径加上包路径后的路径
     * @param base 基础路径
     * @param a    a包
     * @param b    b包
     * @return 合并后的路径
     */
    static String mergePath(String base, String a, String b) {
        return Paths.get(base, DialogConfigUtil.merge(a, b)).toString();
    }
}
