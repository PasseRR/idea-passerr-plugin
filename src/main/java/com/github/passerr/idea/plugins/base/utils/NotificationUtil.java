package com.github.passerr.idea.plugins.base.utils;

import com.intellij.notification.Notification;

/**
 * 消息提示
 * @author xiehai
 * @date 2022/06/23 15:38
 */
public interface NotificationUtil {
    /**
     * 消息提示 默认持续4秒
     * @param notification {@link Notification}
     */
    static void notify(Notification notification) {
        NotificationUtil.notify(notification, 4);
    }

    /**
     * 消息提示
     * @param notification {@link Notification}
     * @param elapse       持续时间
     */
    static void notify(Notification notification, int elapse) {
        AsyncUtil.run(new NotificationThread(notification, elapse));
    }
}
