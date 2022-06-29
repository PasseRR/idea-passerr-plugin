package com.github.passerr.idea.plugins.base.utils;

import com.github.passerr.idea.plugins.spring.web.Json5Generator;
import com.intellij.openapi.diagnostic.Logger;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * {@link Desktop}工具
 * @author xiehai
 * @date 2022/06/29 10:26
 */
public interface DesktopUtil {
    Logger LOG = Logger.getInstance(Json5Generator.class);
    Desktop DESKTOP = Desktop.getDesktop();

    /**
     * 跳转浏览器
     * @param url 访问url地址
     */
    static void browser(String url) {
        try {
            DESKTOP.browse(new URL(url).toURI());
        } catch (IOException | URISyntaxException e) {
            LOG.error(e.getMessage(), e);
        }
    }
}
