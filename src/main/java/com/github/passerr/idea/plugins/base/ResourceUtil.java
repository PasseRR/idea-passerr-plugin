package com.github.passerr.idea.plugins.base;

import com.github.passerr.idea.plugins.base.constants.StringConstants;
import com.google.common.io.CharStreams;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * 资源文件读取
 * @author xiehai
 * @date 2021/07/05 09:32
 * @Copyright(c) tellyes tech. inc. co.,ltd
 */
public interface ResourceUtil {
    /**
     * 读取文件为字符串
     * @param path 文件路径
     * @return 文件内容
     */
    static String readAsString(String path) {
        try (InputStream resourceAsStream = ResourceUtil.class.getResourceAsStream(path)) {
            assert resourceAsStream != null;
            return CharStreams.toString(new InputStreamReader(resourceAsStream, StandardCharsets.UTF_8));
        } catch (Exception ignore) {
            return "";
        }
    }

    /**
     * 读取文件后替换控制符
     * @param path 文件路径
     * @return 文件内容
     */
    static String readWithoutLr(String path) {
        return ResourceUtil.readAsString(path).replace(StringConstants.CR_LF, StringConstants.LF);
    }
}
