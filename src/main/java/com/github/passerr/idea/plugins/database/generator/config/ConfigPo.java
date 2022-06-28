package com.github.passerr.idea.plugins.database.generator.config;

import com.github.passerr.idea.plugins.base.constants.StringConstants;
import com.github.passerr.idea.plugins.base.utils.ResourceUtil;
import com.github.passerr.idea.plugins.database.generator.action.DialogConfigInfo;
import com.intellij.util.xmlb.annotations.Tag;
import com.intellij.util.xmlb.annotations.XCollection;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;
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
@Accessors(chain = true)
public class ConfigPo implements Serializable {
    boolean useServiceImpl;
    String url;
    String entity;
    String mapper;
    String mapperXml;
    String service;
    String serviceImpl;
    String controller;
    @Tag("types")
    @XCollection(elementTypes = TypeMappingPo.class)
    List<TypeMappingPo> types;
    DialogConfigInfo config;

    ConfigPo() {
        this.useServiceImpl = false;
        // 默认配置
        this.url = StringConstants.EMPTY;
        this.entity = ResourceUtil.readWithoutLr("/generator/entity.vm");
        this.mapper = ResourceUtil.readWithoutLr("/generator/mapper.vm");
        this.mapperXml = ResourceUtil.readWithoutLr("/generator/mapper-xml.vm");
        this.service = ResourceUtil.readWithoutLr("/generator/service.vm");
        this.serviceImpl = ResourceUtil.readWithoutLr("/generator/service-impl.vm");
        this.controller = ResourceUtil.readWithoutLr("/generator/controller.vm");
        this.types = TypeMappingPo.defaultMappings();
        this.config = new DialogConfigInfo();
    }

    /**
     * 深复制
     * @return {@link ConfigPo}
     */
    public ConfigPo deepCopy() {
        return SerializationUtils.clone(this);
    }

    /**
     * 从其他配置赋值
     * @param configPo {@link ConfigPo}
     */
    public void from(ConfigPo configPo) {
        this.useServiceImpl = configPo.useServiceImpl;
        this.url = configPo.url;
        this.entity = configPo.entity;
        this.mapper = configPo.mapper;
        this.mapperXml = configPo.mapperXml;
        this.service = configPo.service;
        this.serviceImpl = configPo.serviceImpl;
        this.controller = configPo.controller;
        this.types = configPo.types.stream().map(SerializationUtils::clone).collect(Collectors.toList());
        this.config = SerializationUtils.clone(configPo.config);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (o == null || this.getClass() != o.getClass()) {return false;}
        ConfigPo that = (ConfigPo) o;
        return
            this.useServiceImpl == that.useServiceImpl &&
                Objects.equals(this.url, that.url) &&
                Objects.equals(this.entity, that.entity) &&
                Objects.equals(this.mapper, that.mapper) &&
                Objects.equals(this.mapperXml, that.mapperXml) &&
                Objects.equals(this.service, that.service) &&
                Objects.equals(this.serviceImpl, that.serviceImpl) &&
                Objects.equals(this.controller, that.controller) &&
                Objects.equals(this.types, that.types) &&
                Objects.equals(this.config, that.config);
    }

    @Override
    public int hashCode() {
        return
            Objects.hash(
                this.useServiceImpl, this.url, this.entity, this.mapper, this.mapperXml,
                this.service, this.useServiceImpl, this.controller, this.types, this.config
            );
    }
}
