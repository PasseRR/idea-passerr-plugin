package com.github.passerr.idea.plugins.database.generator.config.po;

import com.github.passerr.idea.plugins.base.utils.GsonUtil;
import com.github.passerr.idea.plugins.base.utils.NotificationUtil;
import com.github.passerr.idea.plugins.base.utils.ResourceUtil;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import org.apache.commons.lang.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * 模版远程配置工具
 * @author xiehai
 * @date 2022/06/29 15:50
 */
interface DetailUtil {
    /**
     * http相对仓库路径
     * @param file 资源文件路径
     * @return 补全后的路径
     */
    static String fullPath(String file) {
        return String.format("src/main/resources%s", file);
    }

    /**
     * 本地文件解析
     * @param main 主配置文件路径
     * @return 补全后的路径
     */
    static DetailPo fromLocal(String main) {
        DetailPo detailPo = GsonUtil.deserialize(ResourceUtil.readAsString(main), DetailPo.class);
        doLocal(main, detailPo::getEntity, detailPo::setEntity);
        doLocal(main, detailPo::getMapper, detailPo::setMapper);
        doLocal(main, detailPo::getMapperXml, detailPo::setMapperXml);
        doLocal(main, detailPo::getService, detailPo::setService);
        if (detailPo.isUseServiceImpl()) {
            doLocal(main, detailPo::getServiceImpl, detailPo::setServiceImpl);
        }
        doLocal(main, detailPo::getController, detailPo::setController);

        return detailPo;
    }

    static void doLocal(String main, Supplier<String> supplier, Consumer<String> consumer) {
        Optional.ofNullable(supplier.get())
            .filter(StringUtils::isNotBlank)
            .map(String::trim)
            .ifPresent(it -> consumer.accept(ResourceUtil.readWithoutLr(String.format("%s/../%s", main, it))));
    }

    static DetailPo fromHttp(String url) {
        return null;
    }

    static boolean urlContent(String contentUrl, StringBuilder sb) {
        try {
            URL url = new URL(contentUrl);
            URLConnection connection = url.openConnection();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String s;
                while (Objects.nonNull(s = reader.readLine())) {
                    sb.append(s);
                }

                return true;
            }
        } catch (IOException e) {
            NotificationUtil.notify(
                new Notification(
                    "generator setting",
                    "generator config sync",
                    "同步失败" + e.getMessage(),
                    NotificationType.ERROR
                )
            );
            return false;
        }
    }
}
