package com.github.passerr.idea.plugins.database.generator.template.po;

import com.github.passerr.idea.plugins.database.generator.config.ConfigPo;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

/**
 * 模板配置
 * @author xiehai
 * @date 2022/06/28 14:18
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TemplatePo implements Serializable {
    String name;
    String url;
    ConfigPo configPo;

    protected static TemplatePo serviceTemplate() {
        TemplatePo po = new TemplatePo();
        po.setName("MybatisPlus Service");
        po.setUrl("https://gitee.com/PasseRR/template1");
        return po;
    }

    protected static TemplatePo serviceImplTemplate() {
        TemplatePo po = new TemplatePo();
        po.setName("MybatisPlus Service With Impl");
        po.setUrl("https://gitee.com/PasseRR/template2");
        return po;
    }
}
