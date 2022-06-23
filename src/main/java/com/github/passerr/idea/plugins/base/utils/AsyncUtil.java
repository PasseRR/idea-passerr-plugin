package com.github.passerr.idea.plugins.base.utils;

import com.intellij.openapi.application.ApplicationManager;

/**
 * 异步工具
 * @author xiehai
 * @date 2022/06/23 15:39
 */
public interface AsyncUtil {
    /**
     * 异步执行
     * @param runnable 执行过程
     */
    static void run(Runnable runnable) {
        ApplicationManager.getApplication().executeOnPooledThread(runnable);
    }
}
