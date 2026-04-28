package com.lab.controller;

import com.lab.annotation.Log;
import com.lab.annotation.RequireRole;
import com.lab.common.Result;
import com.lab.entity.LabReservation;
import com.lab.service.LabReservationService;
import com.lab.service.NotificationService;
import com.lab.util.JwtUtil;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/labReservation")
public class LabReservationController {

    @Resource
    private LabReservationService labReservationService;

    @Resource
    private NotificationService notificationService;

    @GetMapping("/list")
    public Result<List<LabReservation>> list(
            @RequestParam(required = false) String laboratoryName,
            @RequestParam(required = false) String userName,
            @RequestParam(required = false) String status) {
        try {
            return Result.success(labReservationService.findAll(laboratoryName, userName, status));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/my")
    public Result<List<LabReservation>> my(HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization").substring(7);
            Integer userId = JwtUtil.getUserId(token);
            return Result.success(labReservationService.findByUserId(userId));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @Log(module = "实验室", action = "新增预约")
    @PostMapping("/create")
    public Result<Void> create(@RequestBody LabReservation reservation, HttpServletRequest request) {
        try {
            labReservationService.create(reservation, request);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @RequireRole({"admin", "teacher"})
    @Log(module = "实验室", action = "审批通过")
    @PostMapping("/{id}/approve")
    public Result<Void> approve(@PathVariable Integer id) {
        try {
            labReservationService.approve(id);
            LabReservation r = labReservationService.findById(id);
            notificationService.send(r.getUserId(),
                    "实验室预约已通过",
                    "您预约的实验室「" + r.getLaboratoryName() + "」已被审批通过",
                    "reservation");
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @RequireRole({"admin", "teacher"})
    @Log(module = "实验室", action = "审批拒绝")
    @PostMapping("/{id}/reject")
    public Result<Void> reject(@PathVariable Integer id, @RequestBody(required = false) java.util.Map<String, String> body) {
        try {
            String reason = body != null ? body.get("reason") : null;
            labReservationService.reject(id, reason);
            LabReservation r = labReservationService.findById(id);
            String msg = "您预约的实验室「" + r.getLaboratoryName() + "」已被拒绝";
            if (reason != null && !reason.isEmpty()) msg += "，原因：" + reason;
            notificationService.send(r.getUserId(), "实验室预约已拒绝", msg, "reservation");
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/{id}/cancel")
    public Result<Void> cancel(@PathVariable Integer id, HttpServletRequest request) {
        try {
            labReservationService.cancel(id, request);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @RequireRole({"admin"})
    @Log(module = "实验室", action = "删除预约")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Integer id) {
        try {
            labReservationService.deleteById(id);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
