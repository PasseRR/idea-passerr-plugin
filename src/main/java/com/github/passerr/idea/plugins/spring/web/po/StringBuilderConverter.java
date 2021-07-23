package com.github.passerr.idea.plugins.spring.web.po;

import com.intellij.util.xmlb.Converter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * {@link StringBuilder}转换器
 * @author xiehai
 * @date 2021/07/02 16:24
 * @Copyright(c) tellyes tech. inc. co.,ltd
 */
public class StringBuilderConverter extends Converter<StringBuilder> {
    @Nullable
    @Override
    public StringBuilder fromString(@NotNull String value) {
        return new StringBuilder(value);
    }

    @NotNull
    @Override
    public String toString(@NotNull StringBuilder stringBuilder) {
        return stringBuilder.toString();
    }
}