package com.github.passerr.idea.plugins.naming;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

/**
 * 命名风格状态配置
 * @author xiehai
 * @date 2022/02/14 15:52
 */
@State(
    name = "com.github.passerr.idea.plugins.naming.NamingStateComponent",
    storages = @Storage("com.github.passerr.idea.plugins.naming.NamingStateComponent.xml")
)
public class NamingStateComponent implements PersistentStateComponent<NamingStateComponent.NamingState> {
    private final NamingState namingState = new NamingState();

    @Override
    public @Nullable NamingStateComponent.NamingState getState() {
        return this.namingState;
    }

    @Override
    public void loadState(@NotNull NamingState state) {
        XmlSerializerUtil.copyBean(state, this.namingState);
    }

    static NamingStateComponent getInstance() {
        return ServiceManager.getService(NamingStateComponent.class);
    }

    @Data
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class NamingState {
        int state = Arrays.stream(NamingStyle.values()).map(NamingStyle::getBit).reduce(0, (a, b) -> a | b);
    }
}
