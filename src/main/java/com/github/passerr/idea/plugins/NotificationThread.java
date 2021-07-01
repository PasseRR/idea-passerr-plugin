package com.github.passerr.idea.plugins;

import com.intellij.notification.Notification;
import com.intellij.notification.Notifications;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.util.concurrent.TimeUnit;

/**
 * 消息通知
 * @author xiehai1
 * @date 2018/11/08 17:31
 * @Copyright (c) gome inc Gome Co.,LTD
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationThread extends Thread {
    Notification notification;
    int sleepTime;

    NotificationThread(Notification notification) {
        // 默认4秒关闭弹窗
        this(notification, 4);
    }

    NotificationThread(Notification notification, int sleepTime) {
        assert sleepTime > 0;
        this.notification = notification;
        // 默认4秒关闭弹窗
        this.sleepTime = sleepTime;
    }

    @Override
    public void run() {
        Notifications.Bus.notify(this.notification);
        try {
            TimeUnit.SECONDS.sleep(this.sleepTime);
        } catch (InterruptedException ignore) {
        }
        this.notification.expire();
    }
}
