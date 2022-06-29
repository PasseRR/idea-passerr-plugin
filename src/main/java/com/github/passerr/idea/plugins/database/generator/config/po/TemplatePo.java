package com.github.passerr.idea.plugins.database.generator.config.po;

import com.github.passerr.idea.plugins.base.utils.PluginUtil;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

/**
 * 模板配置
 * @author xiehai
 * @date 2022/06/28 14:18
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TemplatePo {
    String name;
    String url;
    DetailPo detail = new DetailPo();
    private static final String SERVICE_TEMPLATE = "/generator/service/template.json";
    private static final String SERVICE_IMPL_TEMPLATE = "/generator/service-impl/template.json";

    static TemplatePo serviceTemplate() {
        TemplatePo po = new TemplatePo();
        po.setName("Mybatis Plus Service");
        po.setUrl(PluginUtil.gitTagUrl(DetailUtil.fullPath(SERVICE_TEMPLATE)));
        po.setDetail(DetailUtil.fromLocal(SERVICE_TEMPLATE));
        return po;
    }

    static TemplatePo serviceImplTemplate() {
        TemplatePo po = new TemplatePo();
        po.setName("Mybatis Plus Service With Impl");
        po.setUrl(PluginUtil.gitTagUrl(DetailUtil.fullPath(SERVICE_IMPL_TEMPLATE)));
        po.setDetail(DetailUtil.fromLocal(SERVICE_IMPL_TEMPLATE));
        return po;
    }
}
