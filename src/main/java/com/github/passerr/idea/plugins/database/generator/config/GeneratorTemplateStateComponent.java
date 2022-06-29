package com.github.passerr.idea.plugins.database.generator.config;

import com.github.passerr.idea.plugins.database.generator.config.po.TemplatesPo;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * 代码生成模版配置
 * @author xiehai
 * @date 2022/06/23 10:28
 */
@State(
    name = "com.github.passerr.idea.plugins.database.generator.config.GeneratorTemplateStateComponent",
    storages = @Storage("com.github.passerr.idea.plugins.database.generator.config.GeneratorTemplateStateComponent.xml")
)
public class GeneratorTemplateStateComponent implements PersistentStateComponent<TemplatesPo> {
    private final TemplatesPo po = TemplatesPo.defaultTemplates();

    @Override
    public TemplatesPo getState() {
        return this.po;
    }

    @Override
    public void loadState(@NotNull TemplatesPo state) {
        XmlSerializerUtil.copyBean(state, this.po);
        this.po.setTemplates(new ArrayList<>(this.po.getTemplates()));
    }

    public static GeneratorTemplateStateComponent getInstance() {
        return ApplicationManager.getApplication().getService(GeneratorTemplateStateComponent.class);
    }
}