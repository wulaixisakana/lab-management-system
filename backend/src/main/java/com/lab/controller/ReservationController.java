package com.lab.controller;

import com.lab.annotation.Log;
import com.lab.annotation.RequireRole;
import com.lab.common.Result;
import com.lab.entity.Reservation;
import com.lab.service.ReservationService;
import com.lab.util.JwtUtil;
import org.springframework.web.bind.annotation.*;

import com.lab.service.NotificationService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/reservation")
public class ReservationController {

    @Resource
    private ReservationService reservationService;

    @Resource
    private NotificationService notificationService;

    @GetMapping("/list")
    public Result<List<Reservation>> list(
            @RequestParam(required = false) String equipmentName,
            @RequestParam(required = false) String userName,
            @RequestParam(required = false) String status) {
        try {
            return Result.success(reservationService.findAll(equipmentName, userName, status));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/my")
    public Result<List<Reservation>> getMyReservations(HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization").substring(7);
            Integer userId = JwtUtil.getUserId(token);
            return Result.success(reservationService.findByUserId(userId));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public Result<Reservation> getById(@PathVariable Integer id) {
        try {
            return Result.success(reservationService.findById(id));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @Log(module = "预约", action = "新增预约")
    @PostMapping("/create")
    public Result<Void> create(@RequestBody Reservation reservation, HttpServletRequest request) {
        try {
            reservationService.create(reservation, request);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @RequireRole({"admin", "teacher"})
    @Log(module = "预约", action = "审批通过")
    @PostMapping("/{id}/approve")
    public Result<Void> approve(@PathVariable Integer id) {
        try {
            reservationService.approve(id);
            Reservation r = reservationService.findById(id);
            notificationService.send(r.getUserId(),
                    "预约已通过",
                    "您预约的设备「" + r.getEquipmentName() + "」已被审批通过",
                    "reservation");
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @RequireRole({"admin", "teacher"})
    @Log(module = "预约", action = "审批拒绝")
    @PostMapping("/{id}/reject")
    public Result<Void> reject(@PathVariable Integer id, @RequestBody(required = false) java.util.Map<String, String> body) {
        try {
            String reason = body != null ? body.get("reason") : null;
            reservationService.reject(id, reason);
            Reservation r = reservationService.findById(id);
            String msg = "您预约的设备「" + r.getEquipmentName() + "」已被拒绝";
            if (reason != null && !reason.isEmpty()) msg += "，原因：" + reason;
            notificationService.send(r.getUserId(), "预约已拒绝", msg, "reservation");
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/{id}/cancel")
    public Result<Void> cancel(@PathVariable Integer id, HttpServletRequest request) {
        try {
            reservationService.cancel(id, request);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @RequireRole({"admin"})
    @Log(module = "预约", action = "删除预约")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Integer id) {
        try {
            reservationService.deleteById(id);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
