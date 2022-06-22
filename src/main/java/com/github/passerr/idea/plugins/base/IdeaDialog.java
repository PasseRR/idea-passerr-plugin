package com.github.passerr.idea.plugins.base;

import com.intellij.openapi.ui.DialogWrapper;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;
import java.awt.Component;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * idea弹出框
 * @author xiehai
 * @date 2021/07/05 11:00
 * @Copyright(c) tellyes tech. inc. co.,ltd
 */
public class IdeaDialog<T> extends DialogWrapper {
    /**
     * 初始参数
     */
    @Getter
    protected T value;
    protected Consumer<T> action;
    protected Predicate<T> changePredicate;
    protected Function<IdeaDialog<T>, JComponent> componentFunction;

    /**
     * 弹出框构造
     * @param parent 父容器
     */
    public IdeaDialog(Component parent) {
        super(parent, true);
    }

    public void onChange() {
        if (Objects.nonNull(this.changePredicate)) {
            super.setOKActionEnabled(this.changePredicate.test(this.value));
        }
    }

    public IdeaDialog<T> title(String title) {
        super.setTitle(title);
        return this;
    }

    public IdeaDialog<T> value(T value) {
        this.value = value;
        return this;
    }


    public IdeaDialog<T> okAction(Consumer<T> action) {
        this.action = action;
        return this;
    }

    public IdeaDialog<T> changePredicate(Predicate<T> changePredicate) {
        this.changePredicate = changePredicate;
        return this;
    }

    public IdeaDialog<T> componentFunction(Function<IdeaDialog<T>, JComponent> componentFunction) {
        this.componentFunction = componentFunction;
        return this;
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return this.componentFunction.apply(this);
    }

    public IdeaDialog<T> doInit() {
        super.init();
        return this;
    }

    @Override
    protected void doOKAction() {
        this.action.accept(this.value);
        super.doOKAction();
    }
}
