package com.lab.controller;

import com.lab.common.Result;
import com.lab.entity.Attendance;
import com.lab.service.AttendanceService;
import com.lab.util.JwtUtil;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/attendance")
@CrossOrigin
public class AttendanceController {

    @Resource
    private AttendanceService attendanceService;

    @GetMapping("/list")
    public Result<List<Attendance>> list(
            @RequestParam(required = false) String userName,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        try {
            return Result.success(attendanceService.findAll(userName, startDate, endDate));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/my")
    public Result<List<Attendance>> getMyAttendance(HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization").substring(7);
            Integer userId = JwtUtil.getUserId(token);
            return Result.success(attendanceService.findByUserId(userId));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/today")
    public Result<Attendance> getTodayStatus(HttpServletRequest request) {
        try {
            return Result.success(attendanceService.getTodayStatus(request));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/checkin")
    public Result<Attendance> checkIn(HttpServletRequest request) {
        try {
            return Result.success(attendanceService.checkIn(request));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/checkout")
    public Result<Attendance> checkOut(HttpServletRequest request) {
        try {
            return Result.success(attendanceService.checkOut(request));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
