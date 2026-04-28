package com.lab.controller;

import com.lab.common.Result;
import com.lab.entity.Notification;
import com.lab.service.NotificationService;
import com.lab.util.JwtUtil;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/notification")
public class NotificationController {

    @Resource
    private NotificationService notificationService;

    @GetMapping("/list")
    public Result<Map<String, Object>> list(HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization").substring(7);
            Integer userId = JwtUtil.getUserId(token);
            List<Notification> notifications = notificationService.findByUserId(userId);
            int unreadCount = notificationService.countUnread(userId);
            Map<String, Object> result = new HashMap<>();
            result.put("notifications", notifications);
            result.put("unreadCount", unreadCount);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/{id}/read")
    public Result<Void> markAsRead(@PathVariable Integer id, HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization").substring(7);
            Integer userId = JwtUtil.getUserId(token);
            notificationService.markAsRead(id, userId);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/readAll")
    public Result<Void> markAllAsRead(HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization").substring(7);
            Integer userId = JwtUtil.getUserId(token);
            notificationService.markAllAsRead(userId);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
