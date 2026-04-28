package com.lab.service;

import com.lab.entity.Notification;
import com.lab.mapper.NotificationMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class NotificationService {

    @Resource
    private NotificationMapper notificationMapper;

    public List<Notification> findByUserId(Integer userId) {
        return notificationMapper.findByUserId(userId);
    }

    public int countUnread(Integer userId) {
        return notificationMapper.countUnread(userId);
    }

    public void send(Integer userId, String title, String content, String type) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setType(type);
        notificationMapper.insert(notification);
    }

    public void markAsRead(Integer id, Integer userId) {
        notificationMapper.markAsRead(id, userId);
    }

    public void markAllAsRead(Integer userId) {
        notificationMapper.markAllAsRead(userId);
    }
}
