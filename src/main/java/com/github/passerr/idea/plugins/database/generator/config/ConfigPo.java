package com.github.passerr.idea.plugins.database.generator.config;

import com.github.passerr.idea.plugins.base.StringBuilderConverter;
import com.github.passerr.idea.plugins.base.utils.ResourceUtil;
import com.github.passerr.idea.plugins.base.utils.StringBuilderUtil;
import com.intellij.util.xmlb.annotations.OptionTag;
import com.intellij.util.xmlb.annotations.Tag;
import com.intellij.util.xmlb.annotations.XCollection;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 配置持久化对象
 * @author xiehai
 * @date 2022/06/22 16:01
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class ConfigPo {
    boolean useServiceImpl;
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
    @Tag("types")
    @XCollection(elementTypes = TypeMappingPo.class)
    List<TypeMappingPo> types;

    public ConfigPo() {
        this.useServiceImpl = false;
        // 默认配置
        this.url = new StringBuilder();
        this.author = new StringBuilder("generator");
        this.entity = new StringBuilder(ResourceUtil.readWithoutLr("/generator/entity.vm"));
        this.mapper = new StringBuilder(ResourceUtil.readWithoutLr("/generator/mapper.vm"));
        this.mapperXml = new StringBuilder(ResourceUtil.readWithoutLr("/generator/mapper-xml.vm"));
        this.service = new StringBuilder(ResourceUtil.readWithoutLr("/generator/service.vm"));
        this.serviceImpl = new StringBuilder(ResourceUtil.readWithoutLr("/generator/service-impl.vm"));
        this.controller = new StringBuilder(ResourceUtil.readWithoutLr("/generator/controller.vm"));
        this.types = TypeMappingPo.defaultMappings();
    }

    /**
     * 深复制
     * @return {@link ConfigPo}
     */
    public ConfigPo deepCopy() {
        return
            new ConfigPo(
                this.useServiceImpl,
                new StringBuilder(this.url),
                new StringBuilder(this.author),
                new StringBuilder(this.entity),
                new StringBuilder(this.mapper),
                new StringBuilder(this.mapperXml),
                new StringBuilder(this.service),
                new StringBuilder(this.serviceImpl),
                new StringBuilder(this.controller),
                this.types.stream().map(TypeMappingPo::deepCopy).collect(Collectors.toList())
            );
    }

    /**
     * 从其他配置赋值
     * @param configPo {@link ConfigPo}
     */
    public void from(ConfigPo configPo) {
        this.useServiceImpl = configPo.useServiceImpl;
        StringBuilderUtil.reset(this.url, configPo.url);
        StringBuilderUtil.reset(this.author, configPo.author);
        StringBuilderUtil.reset(this.entity, configPo.entity);
        StringBuilderUtil.reset(this.mapper, configPo.mapper);
        StringBuilderUtil.reset(this.mapperXml, configPo.mapperXml);
        StringBuilderUtil.reset(this.service, configPo.service);
        StringBuilderUtil.reset(this.serviceImpl, configPo.serviceImpl);
        StringBuilderUtil.reset(this.controller, configPo.controller);
        this.types.clear();
        this.types.addAll(configPo.types);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (o == null || this.getClass() != o.getClass()) {return false;}
        ConfigPo that = (ConfigPo) o;
        return
            this.useServiceImpl == that.useServiceImpl &&
                Objects.equals(this.url.toString(), that.url.toString()) &&
                Objects.equals(this.author.toString(), that.author.toString()) &&
                Objects.equals(this.entity.toString(), that.entity.toString()) &&
                Objects.equals(this.mapper.toString(), that.mapper.toString()) &&
                Objects.equals(this.mapperXml.toString(), that.mapperXml.toString()) &&
                Objects.equals(this.service.toString(), that.service.toString()) &&
                Objects.equals(this.serviceImpl.toString(), that.serviceImpl.toString()) &&
                Objects.equals(this.controller.toString(), that.controller.toString()) &&
                Objects.equals(this.types, that.types);
    }

    @Override
    public int hashCode() {
        return
            Objects.hash(
                this.useServiceImpl, this.url, this.author, this.entity, this.mapper,
                this.mapperXml, this.service, this.useServiceImpl, this.controller, this.types
            );
    }
}
