package com.github.passerr.idea.plugins.spring.web.po;

import com.github.passerr.idea.plugins.spring.web.AliasType;
import com.github.passerr.idea.plugins.spring.web.ResourceUtil;
import com.github.passerr.idea.plugins.spring.web.WebCopyConstants;
import com.intellij.util.xmlb.annotations.AbstractCollection;
import com.intellij.util.xmlb.annotations.OptionTag;
import com.intellij.util.xmlb.annotations.Tag;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 配置持久化po
 * @author xiehai
 * @date 2021/06/30 19:30
 * @Copyright(c) tellyes tech. inc. co.,ltd
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class ApiDocSettingPo {
    @OptionTag(tag = "template", nameAttribute = "", converter = StringBuilderConverter.class)
    StringBuilder template;
    @Tag("query-param-ignore-types")
    @AbstractCollection
    List<String> queryParamIgnoreTypes;
    @Tag("query-param-ignore-annotations")
    @AbstractCollection
    List<String> queryParamIgnoreAnnotations;
    @Tag("body-ignore-types")
    @AbstractCollection
    List<String> bodyIgnoreTypes;
    @Tag("objects")
    @AbstractCollection(elementTypes = ApiDocObjectSerialPo.class)
    List<ApiDocObjectSerialPo> objects;

    public ApiDocSettingPo() {
        this.template = new StringBuilder(ResourceUtil.readAsString("/api-doc-template.vm"));
        this.bodyIgnoreTypes = new ArrayList<>(WebCopyConstants.ALL_IGNORE_TYPES);
        this.queryParamIgnoreTypes = new ArrayList<>(WebCopyConstants.QUERY_PARAM_IGNORE_TYPES);
        this.queryParamIgnoreAnnotations = new ArrayList<>(WebCopyConstants.QUERY_PARAM_IGNORE_ANNOTATIONS);
        this.objects = ApiDocObjectSerialPo.defaultObjects();
    }

    public ApiDocSettingPo deepCopy() {
        return
            new ApiDocSettingPo(
                new StringBuilder(this.template),
                new ArrayList<>(this.bodyIgnoreTypes),
                new ArrayList<>(this.queryParamIgnoreTypes),
                new ArrayList<>(this.queryParamIgnoreAnnotations),
                this.objects.stream().map(ApiDocObjectSerialPo::deepCopy).collect(Collectors.toList())
            );
    }

    public void shallowCopy(ApiDocSettingPo source) {
        this.template.setLength(0);
        this.template.append(source.template);
        this.bodyIgnoreTypes.clear();
        this.bodyIgnoreTypes.addAll(source.getBodyIgnoreTypes());
        this.queryParamIgnoreTypes.clear();
        this.queryParamIgnoreTypes.addAll(source.getQueryParamIgnoreTypes());
        this.queryParamIgnoreAnnotations.clear();
        this.queryParamIgnoreAnnotations.addAll(source.getQueryParamIgnoreAnnotations());
        this.objects.clear();
        this.objects.addAll(source.getObjects());
    }

    public void setStringTemplate(String template) {
        this.template.setLength(0);
        this.template.append(template);
    }

    /**
     * 别名获取
     * @param type 类型全称
     * @return 别名
     */
    public String alias(String type) {
        return
            this.getObjects()
                .stream()
                .filter(it -> Objects.equals(it.getType(), type))
                .map(ApiDocObjectSerialPo::getAlias)
                .findFirst()
                // 未知别名类型
                .orElse(AliasType.UNKNOWN_ALIAS);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        ApiDocSettingPo that = (ApiDocSettingPo) o;
        return Objects.equals(template.toString(), that.template.toString()) &&
            Objects.equals(bodyIgnoreTypes, that.bodyIgnoreTypes) &&
            Objects.equals(queryParamIgnoreTypes, that.queryParamIgnoreTypes) &&
            Objects.equals(queryParamIgnoreAnnotations, that.queryParamIgnoreAnnotations) &&
            Objects.equals(objects, that.objects);
    }

    @Override
    public int hashCode() {
        return Objects.hash(template, bodyIgnoreTypes, queryParamIgnoreTypes, queryParamIgnoreAnnotations, objects);
    }
}
