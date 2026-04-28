package com.lab.mapper;

import com.lab.entity.Notification;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface NotificationMapper {

    List<Notification> findByUserId(@Param("userId") Integer userId);

    int countUnread(@Param("userId") Integer userId);

    int insert(Notification notification);

    int markAsRead(@Param("id") Integer id, @Param("userId") Integer userId);

    int markAllAsRead(@Param("userId") Integer userId);
}
