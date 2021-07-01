package com.github.passerr.idea.plugins.spring.web.po;

import com.github.passerr.idea.plugins.spring.web.WebCopyConstants;
import com.intellij.util.xmlb.annotations.AbstractCollection;
import com.intellij.util.xmlb.annotations.Tag;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author xiehai
 * @date 2021/06/30 19:30
 * @Copyright(c) tellyes tech. inc. co.,ltd
 */
@Data
@EqualsAndHashCode
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class ApiDocSettingPo {
    @Tag("template")
    String template;
    @Tag("all-ignore-types")
    @AbstractCollection
    List<String> allIgnoreTypes;
    @Tag("query-param-ignore-types")
    @AbstractCollection
    List<String> queryParamIgnoreTypes;
    @Tag("query-param-ignore-annotations")
    @AbstractCollection
    List<String> queryParamIgnoreAnnotations;
    @Tag("aliases")
    @AbstractCollection(elementTypes = ApiDocAliasPairPo.class)
    List<ApiDocAliasPairPo> aliases;

    public ApiDocSettingPo() {
        this.template = WebCopyConstants.DEFAULT_TEMPLATE;
        this.allIgnoreTypes = new ArrayList<>(WebCopyConstants.ALL_IGNORE_TYPES);
        this.queryParamIgnoreTypes = new ArrayList<>(WebCopyConstants.QUERY_PARAM_IGNORE_TYPES);
        this.queryParamIgnoreAnnotations = new ArrayList<>(WebCopyConstants.QUERY_PARAM_IGNORE_ANNOTATIONS);
        this.aliases = WebCopyConstants.DEFAULT_ALIAS_MAPPINGS.entrySet()
            .stream()
            .map(it -> new ApiDocAliasPairPo(it.getKey(), it.getValue()))
            .collect(Collectors.toList());
    }

    public static ApiDocSettingPo deepCopy(ApiDocSettingPo source) {
        return
            new ApiDocSettingPo(
                source.template,
                new ArrayList<>(source.allIgnoreTypes),
                new ArrayList<>(source.queryParamIgnoreTypes),
                new ArrayList<>(source.queryParamIgnoreAnnotations),
                source.aliases.stream().map(ApiDocAliasPairPo::deepCopy).collect(Collectors.toList())
            );
    }

    public void update(ApiDocSettingPo target) {
        this.template = target.template;
        this.allIgnoreTypes = target.allIgnoreTypes;
        this.queryParamIgnoreTypes = target.queryParamIgnoreTypes;
        this.queryParamIgnoreAnnotations = target.queryParamIgnoreAnnotations;
        this.aliases = target.aliases;
    }
}
