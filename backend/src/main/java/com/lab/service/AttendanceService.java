package com.lab.service;

import com.lab.entity.Attendance;
import com.lab.entity.User;
import com.lab.mapper.AttendanceMapper;
import com.lab.mapper.UserMapper;
import com.lab.util.JwtUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AttendanceService {

    @Resource
    private AttendanceMapper attendanceMapper;

    @Resource
    private UserMapper userMapper;

    public List<Attendance> findAll(String userName, String startDate, String endDate) {
        return attendanceMapper.findAll(userName, startDate, endDate);
    }

    public List<Attendance> findByUserId(Integer userId) {
        return attendanceMapper.findByUserId(userId);
    }

    public Attendance checkIn(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        Integer userId = JwtUtil.getUserId(token);
        String username = JwtUtil.getUsername(token);

        Attendance todayCheckIn = attendanceMapper.findTodayCheckIn(userId);
        if (todayCheckIn != null && todayCheckIn.getCheckOutTime() == null) {
            throw new RuntimeException("今日已签到，尚未签退");
        }

        User user = userMapper.findById(userId);

        Attendance attendance = new Attendance();
        attendance.setUserId(userId);
        attendance.setUserName(user.getRealName());
        attendance.setCheckInTime(LocalDateTime.now());
        attendance.setStatus("present");

        attendanceMapper.insert(attendance);
        return attendance;
    }

    public Attendance checkOut(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        Integer userId = JwtUtil.getUserId(token);

        Attendance todayCheckIn = attendanceMapper.findTodayCheckIn(userId);
        if (todayCheckIn == null) {
            throw new RuntimeException("今日未签到");
        }

        if (todayCheckIn.getCheckOutTime() != null) {
            throw new RuntimeException("今日已签退");
        }

        LocalDateTime checkOutTime = LocalDateTime.now();
        Duration duration = Duration.between(todayCheckIn.getCheckInTime(), checkOutTime);
        long minutes = duration.toMinutes();

        todayCheckIn.setCheckOutTime(checkOutTime);
        todayCheckIn.setDuration((int) minutes);
        attendanceMapper.update(todayCheckIn);

        return todayCheckIn;
    }

    public Attendance getTodayStatus(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        Integer userId = JwtUtil.getUserId(token);
        return attendanceMapper.findTodayCheckIn(userId);
    }
}
