package com.github.passerr.idea.plugins.database.generator.config;

import com.github.passerr.idea.plugins.base.ResourceUtil;
import com.github.passerr.idea.plugins.base.StringBuilderConverter;
import com.intellij.util.xmlb.annotations.OptionTag;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

/**
 * 配置持久化对象
 * @author xiehai
 * @date 2022/06/22 16:01
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class ConfigPo {
    @OptionTag(tag = "url", nameAttribute = "", converter = StringBuilderConverter.class)
    StringBuilder url;
    @OptionTag(tag = "author", nameAttribute = "", converter = StringBuilderConverter.class)
    StringBuilder author;
    @OptionTag(tag = "entity", nameAttribute = "", converter = StringBuilderConverter.class)
    StringBuilder entity;
    @OptionTag(tag = "mapper", nameAttribute = "", converter = StringBuilderConverter.class)
    StringBuilder mapper;
    @OptionTag(tag = "mapperXml", nameAttribute = "", converter = StringBuilderConverter.class)
    StringBuilder mapperXml;
    @OptionTag(tag = "service", nameAttribute = "", converter = StringBuilderConverter.class)
    StringBuilder service;
    @OptionTag(tag = "serviceImpl", nameAttribute = "", converter = StringBuilderConverter.class)
    StringBuilder serviceImpl;
    @OptionTag(tag = "controller", nameAttribute = "", converter = StringBuilderConverter.class)
    StringBuilder controller;

    public ConfigPo() {
        // 默认配置
        this.url = new StringBuilder();
        this.author = new StringBuilder("generator");
        this.entity = new StringBuilder(ResourceUtil.readAsString("/generator/entity.vm"));
        this.mapper = new StringBuilder(ResourceUtil.readAsString("/generator/mapper.vm"));
        this.mapperXml = new StringBuilder(ResourceUtil.readAsString("/generator/mapper-xml.vm"));
        this.service = new StringBuilder(ResourceUtil.readAsString("/generator/service.vm"));
        this.serviceImpl = new StringBuilder(ResourceUtil.readAsString("/generator/service-impl.vm"));
        this.controller = new StringBuilder(ResourceUtil.readAsString("/generator/controller.vm"));
    }

    /**
     * 深复制
     * @return {@link ConfigPo}
     */
    public ConfigPo deepCopy() {
        return
            new ConfigPo(
                new StringBuilder(this.url),
                new StringBuilder(this.author),
                new StringBuilder(this.entity),
                new StringBuilder(this.mapper),
                new StringBuilder(this.mapperXml),
                new StringBuilder(this.service),
                new StringBuilder(this.serviceImpl),
                new StringBuilder(this.controller)
            );
    }

    /**
     * 从其他配置赋值
     * @param configPo {@link ConfigPo}
     */
    public void from(ConfigPo configPo) {
        ConfigPo.copyStringBuilder(this.url, configPo.url);
        ConfigPo.copyStringBuilder(this.author, configPo.author);
        ConfigPo.copyStringBuilder(this.entity, configPo.entity);
        ConfigPo.copyStringBuilder(this.mapper, configPo.mapper);
        ConfigPo.copyStringBuilder(this.mapperXml, configPo.mapperXml);
        ConfigPo.copyStringBuilder(this.service, configPo.service);
        ConfigPo.copyStringBuilder(this.serviceImpl, configPo.serviceImpl);
        ConfigPo.copyStringBuilder(this.controller, configPo.controller);
    }

    static void copyStringBuilder(StringBuilder source, CharSequence value) {
        source.setLength(0);
        source.append(value);
    }
}