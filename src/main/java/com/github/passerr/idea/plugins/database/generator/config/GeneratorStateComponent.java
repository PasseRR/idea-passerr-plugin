package com.github.passerr.idea.plugins.database.generator.config;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;

/**
 * 代码生成器配置
 * @author xiehai
 * @date 2022/06/23 10:28
 */
@State(
    name = "com.github.passerr.idea.plugins.database.generator.config.GeneratorStateComponent",
    storages = @Storage("com.github.passerr.idea.plugins.database.generator.config.GeneratorStateComponent.xml")
)
public class GeneratorStateComponent implements PersistentStateComponent<ConfigPo> {
    private final ConfigPo configPo = new ConfigPo();

    @Override
    public ConfigPo getState() {
        return this.configPo;
    }

    @Override
    public void loadState(@NotNull ConfigPo state) {
        XmlSerializerUtil.copyBean(state, this.configPo);
    }

    public static GeneratorStateComponent getInstance() {
        return ApplicationManager.getApplication().getService(GeneratorStateComponent.class);
    }
}