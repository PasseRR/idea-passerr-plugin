package com.github.passerr.idea.plugins.database.generator.config.po;

import com.github.passerr.idea.plugins.base.constants.StringConstants;
import com.github.passerr.idea.plugins.base.utils.GsonUtil;
import com.github.passerr.idea.plugins.base.utils.NotificationUtil;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.JDBCType;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * 配置同步工具
 * @author xiehai
 * @date 2022/06/29 16:47
 */
public interface SyncUtil {
    Set<String> JDBC_TYPES = Collections.unmodifiableSet(
        Arrays.stream(JDBCType.values()).map(JDBCType::name).collect(Collectors.toSet())
    );

    /**
     * 配置同步
     * @param po {@link TemplatePo}
     */
    static void sync(TemplatePo po) {
        StringBuilder sb = new StringBuilder();
        if (StringUtils.isBlank(po.getUrl())) {
            NotificationUtil.notify(
                new Notification(
                    "generator setting",
                    "generator config sync",
                    "同步url为空",
                    NotificationType.INFORMATION
                )
            );
            return;
        }
        String main = po.getUrl().trim();
        doHttp(main, sb::append, true);
        DetailPo detailPo = GsonUtil.deserialize(sb.toString(), DetailPo.class);
        doHttp(main, detailPo::getEntity, detailPo::setEntity);
        doHttp(main, detailPo::getMapper, detailPo::setMapper);
        doHttp(main, detailPo::getMapperXml, detailPo::setMapperXml);
        doHttp(main, detailPo::getService, detailPo::setService);
        if (detailPo.isUseServiceImpl()) {
            doHttp(main, detailPo::getServiceImpl, detailPo::setServiceImpl);
        }
        doHttp(main, detailPo::getController, detailPo::setController);

        // 处理类型转换
        List<MappingPo> types = detailPo.getTypes();
        Set<String> definedTypes = new HashSet<>();

        // 移除非jdbc类型
        types.removeIf(it -> {
            if (!JDBC_TYPES.contains(it.getJdbcType())) {
                return true;
            }
            definedTypes.add(it.getJdbcType());
            return false;
        });

        MappingPo.defaultMappings()
            .stream()
            .filter(it -> !definedTypes.contains(it.getJdbcType()))
            .forEach(types::add);

        // 设置不覆盖作者
        if (Objects.nonNull(detailPo.getSettings()) && Objects.nonNull(po.getDetail().getSettings())) {
            detailPo.getSettings().setAuthor(po.getDetail().getSettings().getAuthor());
        }

        po.setDetail(detailPo);
    }

    /**
     * 执行http同步
     * @param main     主配置文件地址
     * @param supplier 模版文件地址
     * @param consumer 消费
     */
    static void doHttp(String main, Supplier<String> supplier, Consumer<String> consumer) {
        SyncUtil.doHttp(
            Optional.of(supplier.get())
                // 若是http绝对路径 直接访问，否则主文件的相对路径访问
                .filter(it -> it.startsWith("http://") || it.startsWith("https://"))
                .orElseGet(() -> String.format("%s/../%s", main, supplier.get()))
            ,
            consumer,
            false
        );
    }

    /**
     * 配置文件读取
     * @param contentUrl 文件路径
     * @param consumer   文件内容处理器
     * @param notify     请求失败是否提示
     */
    static void doHttp(String contentUrl, Consumer<String> consumer, boolean notify) {
        Optional.ofNullable(contentUrl)
            .map(String::trim)
            .ifPresent(it -> {
                try {
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(it).openStream()))) {
                        StringBuilder sb = new StringBuilder();
                        String s;
                        while (Objects.nonNull(s = reader.readLine())) {
                            sb.append(s).append(StringConstants.LF);
                        }

                        if (sb.length() > 0) {
                            consumer.accept(sb.toString());
                        }
                    }
                } catch (IOException e) {
                    if (notify) {
                        NotificationUtil.notify(
                            new Notification(
                                "generator setting",
                                "generator config sync",
                                "同步失败" + e.getMessage(),
                                NotificationType.ERROR
                            )
                        );
                    }
                }
            });
    }
}
