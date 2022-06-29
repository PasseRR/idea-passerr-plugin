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
     * git blob路径
     * @param path 文件路径
     * @return blob全路径
     */
    static String gitBlobUrl(String path) {
        return String.format("%s/tree/%s/%s", PluginUtil.url(), PluginUtil.version(), path);
    }

    /**
     * git raw路径
     * @param path 文件路径
     * @return raw全路径
     */
    static String gitRawUrl(String path) {
        return String.format("%s/raw/%s/%s", PluginUtil.url(), PluginUtil.version(), path);
    }
}
