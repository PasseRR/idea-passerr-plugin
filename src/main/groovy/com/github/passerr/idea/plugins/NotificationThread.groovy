package com.github.passerr.idea.plugins

import com.intellij.notification.Notification
import com.intellij.notification.Notifications

import java.util.concurrent.TimeUnit

/**
 * 消息通知
 * @author xiehai1
 * @date 2018/11/08 17:31
 * @Copyright ( c ) gome inc Gome Co.,LTD
 */
class NotificationThread extends Thread {
    private Notification notification
    private int sleepTime

    NotificationThread(Notification notification) {
        // 默认4秒关闭弹窗
        this(notification, 4)
    }

    NotificationThread(Notification notification, int sleepTime) {
        assert sleepTime > 0
        this.notification = notification
        // 默认4秒关闭弹窗
        this.sleepTime = sleepTime
    }

    @Override
    void run() {
        Notifications.Bus.notify(this.notification)
        TimeUnit.SECONDS.sleep(this.sleepTime)
        this.notification.expire()
    }
}
