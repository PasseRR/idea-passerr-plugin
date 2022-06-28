package com.github.passerr.idea.plugins.database.generator.template.po;

import com.intellij.util.xmlb.annotations.Tag;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 模板配置
 * @author xiehai
 * @date 2022/06/28 14:17
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TemplatesPo implements Serializable {
    @Tag("templates")
    List<TemplatePo> templates;

    public static TemplatesPo defaultTemplates() {
        TemplatesPo po = new TemplatesPo();
        po.addTemplate(TemplatePo.serviceTemplate());
        po.addTemplate(TemplatePo.serviceImplTemplate());
        return po;
    }

    public void addTemplate(TemplatePo templatePo) {
        if (Objects.isNull(this.templates)) {
            this.templates = new ArrayList<>();
        }

        this.templates.add(SerializationUtils.clone(templatePo));
    }

    public void from(TemplatesPo po) {
        this.templates.clear();
        this.templates.addAll(po.getTemplates().stream().map(SerializationUtils::clone).collect(Collectors.toList()));
    }
}
