package com.github.passerr.idea.plugins.spring.web.po

import com.github.passerr.idea.plugins.spring.web.WebCopyConstants
import com.intellij.util.xmlb.annotations.AbstractCollection
import com.intellij.util.xmlb.annotations.Tag

/**
 * api文档设置持久化po
 * @date 2021/06/29 15:31
 * @Copyright (c) wisewe co.,ltd
 * @author xiehai
 */
class ApiDocSettingPo {
    @Tag("template")
    String template = WebCopyConstants.DEFAULT_TEMPLATE
    @Tag("all-ignore-types")
    @AbstractCollection(elementTag = "type")
    List<String> allIgnoreTypes = WebCopyConstants.ALL_IGNORE_TYPES
    @Tag("query-param-ignore-types")
    @AbstractCollection(elementTag = "type")
    List<String> queryParamIgnoreTypes = WebCopyConstants.QUERY_PARAM_IGNORE_TYPES
    @Tag("query-param-ignore-annotations")
    @AbstractCollection(elementTag = "annotation")
    List<String> queryParamIgnoreAnnotations = WebCopyConstants.QUERY_PARAM_IGNORE_ANNOTATIONS
    @Tag("aliases")
    @AbstractCollection
    List<ApiDocAliasPairPo> aliases = WebCopyConstants.DEFAULT_ALIAS_MAPPINGS.collect { key, value ->
        new ApiDocAliasPairPo(type: key, alias: value)
    }

    static ApiDocSettingPo deepCopy(ApiDocSettingPo source) {
        new ApiDocSettingPo(
            template: source.template,
            allIgnoreTypes: new ArrayList<String>(source.allIgnoreTypes),
            queryParamIgnoreTypes: new ArrayList<String>(source.queryParamIgnoreTypes),
            queryParamIgnoreAnnotations: new ArrayList<String>(source.queryParamIgnoreAnnotations),
            aliases: source.aliases.collect { it -> ApiDocAliasPairPo.deepCopy(it) }
        )
    }

    void update(ApiDocSettingPo target) {
        this.template = target.template
        this.allIgnoreTypes = target.allIgnoreTypes
        this.queryParamIgnoreTypes = target.queryParamIgnoreTypes
        this.queryParamIgnoreAnnotations = target.queryParamIgnoreAnnotations
        this.aliases = target.aliases
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        ApiDocSettingPo that = (ApiDocSettingPo) o

        if (aliases != that.aliases) return false
        if (allIgnoreTypes != that.allIgnoreTypes) return false
        if (queryParamIgnoreAnnotations != that.queryParamIgnoreAnnotations) return false
        if (queryParamIgnoreTypes != that.queryParamIgnoreTypes) return false
        if (template != that.template) return false

        return true
    }

    int hashCode() {
        int result
        result = (template != null ? template.hashCode() : 0)
        result = 31 * result + (allIgnoreTypes != null ? allIgnoreTypes.hashCode() : 0)
        result = 31 * result + (queryParamIgnoreTypes != null ? queryParamIgnoreTypes.hashCode() : 0)
        result = 31 * result + (queryParamIgnoreAnnotations != null ? queryParamIgnoreAnnotations.hashCode() : 0)
        result = 31 * result + (aliases != null ? aliases.hashCode() : 0)
        return result
    }
}
