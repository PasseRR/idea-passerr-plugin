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
}
