package com.lab.controller;

import com.lab.common.Result;
import com.lab.entity.Reservation;
import com.lab.service.ReservationService;
import com.lab.util.JwtUtil;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/reservation")
@CrossOrigin
public class ReservationController {

    @Resource
    private ReservationService reservationService;

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

    @PostMapping("/create")
    public Result<Void> create(@RequestBody Reservation reservation, HttpServletRequest request) {
        try {
            reservationService.create(reservation, request);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/{id}/approve")
    public Result<Void> approve(@PathVariable Integer id) {
        try {
            reservationService.approve(id);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/{id}/reject")
    public Result<Void> reject(@PathVariable Integer id) {
        try {
            reservationService.reject(id);
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
