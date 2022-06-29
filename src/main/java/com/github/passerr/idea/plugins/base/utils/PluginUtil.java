package com.github.passerr.idea.plugins.base.utils;

import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManagerCore;
import com.intellij.openapi.extensions.PluginId;

/**
 * 插件工具
 * @author xiehai
 * @date 2022/06/29 10:50
 */
public interface PluginUtil {
    IdeaPluginDescriptor PLUGIN_DESCRIPTOR =
        PluginManagerCore.getPlugin(PluginId.getId("com.github.passerr.idea.plugins"));

    /**
     * 获得当前插件版本号
     * @return 版本号
     */
    static String version() {
        return PLUGIN_DESCRIPTOR.getVersion();
    }

    /**
     * 获得当前插件url
     * @return url地址
     */
    static String url() {
        return PLUGIN_DESCRIPTOR.getUrl();
    }

    /**
     * 获得git分支指定路径
     * @param path 路径
     * @return 全路径
     */
    static String gitTagUrl(String path) {
        return String.format("%s/tree/%s/%s", url(), version(), path);
    }
}
