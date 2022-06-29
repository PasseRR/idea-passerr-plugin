package com.github.passerr.idea.plugins.database.generator.template.po;

import com.github.passerr.idea.plugins.base.constants.StringConstants;
import com.intellij.util.xmlb.annotations.Tag;
import com.intellij.util.xmlb.annotations.XCollection;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.Objects;

/**
 * 模版配置详情
 * @author xiehai
 * @date 2022/06/22 16:01
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Accessors(chain = true)
public class DetailPo {
    boolean useServiceImpl;
    String entity = StringConstants.EMPTY;
    String mapper = StringConstants.EMPTY;
    String mapperXml = StringConstants.EMPTY;
    String service = StringConstants.EMPTY;
    String serviceImpl = StringConstants.EMPTY;
    String controller = StringConstants.EMPTY;
    @Tag("types")
    @XCollection(elementTypes = MappingPo.class)
    List<MappingPo> types;
    SettingPo settings;

    DetailPo() {
        this.useServiceImpl = true;
        this.types = MappingPo.defaultMappings();
        this.settings = new SettingPo();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (o == null || this.getClass() != o.getClass()) {return false;}
        DetailPo that = (DetailPo) o;
        return
            this.useServiceImpl == that.useServiceImpl &&
                Objects.equals(this.entity, that.entity) &&
                Objects.equals(this.mapper, that.mapper) &&
                Objects.equals(this.mapperXml, that.mapperXml) &&
                Objects.equals(this.service, that.service) &&
                Objects.equals(this.serviceImpl, that.serviceImpl) &&
                Objects.equals(this.controller, that.controller) &&
                Objects.equals(this.types, that.types) &&
                Objects.equals(this.settings, that.settings);
    }

    @Override
    public int hashCode() {
        return
            Objects.hash(
                this.useServiceImpl, this.entity, this.mapper, this.mapperXml,
                this.service, this.useServiceImpl, this.controller, this.types, this.settings
            );
    }
}
