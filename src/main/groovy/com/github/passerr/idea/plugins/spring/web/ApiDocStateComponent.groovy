package com.github.passerr.idea.plugins.spring.web

import com.github.passerr.idea.plugins.spring.web.po.ApiDocSettingPo
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil

/**
 * api文档设置
 * @date 2021/06/29 09:01
 * @Copyright (c) wisewe co.,ltd
 * @author xiehai
 */
@State(
    name = "com.github.passerr.idea.plugins.spring.web.ApiDocStateComponent",
    storages = @Storage("com.github.passerr.idea.plugins.spring.web.ApiDocStateComponent.xml")
)
class ApiDocStateComponent implements PersistentStateComponent<ApiDocSettingPo> {
    private ApiDocSettingPo apiDocSettingPo = new ApiDocSettingPo()

    @Override
    ApiDocSettingPo getState() {
        return this.apiDocSettingPo
    }

    @Override
    void loadState(ApiDocSettingPo state) {
        XmlSerializerUtil.copyBean(state, this.apiDocSettingPo)
    }

    /**
     * 别名获取
     * @param type 类型全称
     * @return 别名
     */
    String alias(String type) {
        this.apiDocSettingPo.aliases.find { it -> it.type == type }.alias ?: WebCopyConstants.DEFAULT_ALIAS
    }

    static ApiDocStateComponent getInstance() {
        ServiceManager.getService(ApiDocStateComponent)
    }
}
