package com.github.passerr.idea.plugins.spring.web

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
    name = "com.github.passerr.idea.plugins.spring.web.ApiDocSettings",
    storages = @Storage("com.github.passerr.idea.plugins.spring.web.ApiDocSettings.xml")
)
class ApiDocSettings implements PersistentStateComponent<ApiDocSettings> {
    protected String template
    protected Set<String> allIgnoreTypes = WebCopyConstants.ALL_IGNORE_TYPES
    protected Set<String> queryParamIgnoreTypes = WebCopyConstants.QUERY_PARAM_IGNORE_TYPES
    protected Set<String> queryParamIgnoreAnnotations = WebCopyConstants.QUERY_PARAM_IGNORE_ANNOTATIONS
    protected Map<String, String> aliases = WebCopyConstants.DEFAULT_ALIAS_MAPPINGS

    @Override
    ApiDocSettings getState() {
        this
    }

    @Override
    void loadState(ApiDocSettings state) {
        XmlSerializerUtil.copyBean(state, this)
    }

    /**
     * 别名获取
     * @param type 类型全称
     * @return 别名
     */
    String alias(String type) {
        this.aliases.getOrDefault(type, WebCopyConstants.DEFAULT_ALIAS)
    }

    static ApiDocSettings getInstance() {
        ServiceManager.getService(ApiDocSettings)
    }
}
