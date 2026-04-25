package com.lab.mapper;

import com.lab.entity.Attendance;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AttendanceMapper {

    Attendance findById(@Param("id") Integer id);

    Attendance findTodayCheckIn(@Param("userId") Integer userId);

    List<Attendance> findByUserId(@Param("userId") Integer userId);

    List<Attendance> findAll(@Param("userName") String userName, @Param("startDate") String startDate, @Param("endDate") String endDate);

    int insert(Attendance attendance);

    int update(Attendance attendance);
}
