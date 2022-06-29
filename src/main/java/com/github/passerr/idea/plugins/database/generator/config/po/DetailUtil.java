package com.github.passerr.idea.plugins.database.generator.config.po;

import com.github.passerr.idea.plugins.base.utils.GsonUtil;
import com.github.passerr.idea.plugins.base.utils.ResourceUtil;
import org.apache.commons.lang.StringUtils;

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

    /**
     * 本地文件读取
     * @param main     主文件
     * @param supplier 路径提供器
     * @param consumer 设置模板内容
     */
    static void doLocal(String main, Supplier<String> supplier, Consumer<String> consumer) {
        Optional.ofNullable(supplier.get())
            .filter(StringUtils::isNotBlank)
            .map(String::trim)
            .ifPresent(it -> consumer.accept(ResourceUtil.readWithoutLr(String.format("%s/../%s", main, it))));
    }
}
