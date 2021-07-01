package com.github.passerr.idea.plugins.spring.web;

import com.github.passerr.idea.plugins.spring.web.po.ApiDocAliasPairPo;
import com.github.passerr.idea.plugins.spring.web.po.ApiDocSettingPo;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;

import java.util.Objects;

/**
 * 配置持久化组件
 * @author xiehai
 * @date 2021/06/30 19:28
 * @Copyright(c) tellyes tech. inc. co.,ltd
 */
@State(
    name = "com.github.passerr.idea.plugins.spring.web.ApiDocStateComponent",
    storages = @Storage("com.github.passerr.idea.plugins.spring.web.ApiDocStateComponent.xml")
)
public class ApiDocStateComponent implements PersistentStateComponent<ApiDocSettingPo> {
    private final ApiDocSettingPo apiDocSettingPo = new ApiDocSettingPo();

    @Override
    public ApiDocSettingPo getState() {
        return this.apiDocSettingPo;
    }

    @Override
    public void loadState(ApiDocSettingPo state) {
        XmlSerializerUtil.copyBean(state, this.apiDocSettingPo);
    }

    /**
     * 别名获取
     * @param type 类型全称
     * @return 别名
     */
    String alias(String type) {
        return
            this.apiDocSettingPo.getAliases()
                .stream()
                .filter(it -> Objects.equals(it.getType(), type))
                .map(ApiDocAliasPairPo::getAlias)
                .findFirst()
                .orElse(WebCopyConstants.DEFAULT_ALIAS);
    }

    static ApiDocStateComponent getInstance() {
        return ServiceManager.getService(ApiDocStateComponent.class);
    }
}