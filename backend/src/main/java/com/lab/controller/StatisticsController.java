package com.lab.controller;

import com.lab.common.Result;
import com.lab.mapper.EquipmentMapper;
import com.lab.mapper.LaboratoryMapper;
import com.lab.mapper.ReservationMapper;
import com.lab.mapper.LabReservationMapper;
import com.lab.mapper.AttendanceMapper;
import com.lab.mapper.UserMapper;
import com.lab.entity.Equipment;
import com.lab.entity.Reservation;
import com.lab.entity.LabReservation;
import com.lab.entity.Attendance;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/statistics")
public class StatisticsController {

    @Resource
    private EquipmentMapper equipmentMapper;

    @Resource
    private LaboratoryMapper laboratoryMapper;

    @Resource
    private ReservationMapper reservationMapper;

    @Resource
    private LabReservationMapper labReservationMapper;

    @Resource
    private AttendanceMapper attendanceMapper;

    @Resource
    private UserMapper userMapper;

    @GetMapping("/overview")
    public Result<Map<String, Object>> getOverview() {
        try {
            Map<String, Object> result = new HashMap<>();

            List<Equipment> equipments = equipmentMapper.findAll(null, null, null);
            result.put("equipmentCount", equipments.size());
            result.put("laboratoryCount", laboratoryMapper.findAll(null, null, null).size());
            List<Reservation> reservations = reservationMapper.findAll(null, null, null);
            List<LabReservation> labReservations = labReservationMapper.findAll(null, null, null);
            List<Attendance> attendances = attendanceMapper.findAll(null, null, null);
            LocalDate today = LocalDate.now();
            result.put("reservationCount", reservations.size() + labReservations.size());
            result.put("equipmentReservationCount", reservations.size());
            result.put("labReservationCount", labReservations.size());
            result.put("attendanceCount", attendances.stream()
                    .filter(a -> a.getCreateTime() != null && a.getCreateTime().toLocalDate().equals(today))
                    .count());
            result.put("userCount", userMapper.findAll().size());

            // 设备分类统计
            Map<String, Long> categoryStats = equipments.stream()
                    .filter(e -> e.getCategory() != null)
                    .collect(Collectors.groupingBy(Equipment::getCategory, Collectors.counting()));
            result.put("equipmentCategoryStats", categoryStats);

            // 设备状态统计
            Map<String, Long> equipmentStatusStats = equipments.stream()
                    .filter(e -> e.getStatus() != null)
                    .collect(Collectors.groupingBy(Equipment::getStatus, Collectors.counting()));
            result.put("equipmentStatusStats", equipmentStatusStats);

            // 预约状态统计（设备+实验室）
            long pendingReservationCount = reservations.stream().filter(r -> "pending".equals(r.getStatus())).count()
                    + labReservations.stream().filter(r -> "pending".equals(r.getStatus())).count();
            long todayReservationCount = reservations.stream()
                    .filter(r -> r.getCreateTime() != null && r.getCreateTime().toLocalDate().equals(today))
                    .count()
                    + labReservations.stream()
                    .filter(r -> r.getCreateTime() != null && r.getCreateTime().toLocalDate().equals(today))
                    .count();
            result.put("pendingReservationCount", pendingReservationCount);
            result.put("todayReservationCount", todayReservationCount);

            Map<String, Long> reservationStatusStats = reservations.stream()
                    .filter(r -> r.getStatus() != null)
                    .collect(Collectors.groupingBy(Reservation::getStatus, Collectors.counting()));
            // 合并实验室预约的状态统计
            labReservations.stream()
                    .filter(r -> r.getStatus() != null)
                    .collect(Collectors.groupingBy(LabReservation::getStatus, Collectors.counting()))
                    .forEach((k, v) -> reservationStatusStats.merge(k, v, Long::sum));
            result.put("reservationStatusStats", reservationStatusStats);

            // 最近7天预约趋势（设备+实验室合并）
            List<Map<String, Object>> reservationTrend = new ArrayList<>();
            for (int i = 6; i >= 0; i--) {
                LocalDate day = today.minusDays(i);
                String dateStr = day.toString();
                long eqCount = reservations.stream()
                        .filter(r -> r.getCreateTime() != null && r.getCreateTime().toLocalDate().equals(day))
                        .count();
                long labCount = labReservations.stream()
                        .filter(r -> r.getCreateTime() != null && r.getCreateTime().toLocalDate().equals(day))
                        .count();
                Map<String, Object> item = new HashMap<>();
                item.put("date", dateStr);
                item.put("eqCount", eqCount);
                item.put("labCount", labCount);
                reservationTrend.add(item);
            }
            result.put("reservationTrend", reservationTrend);

            // 最近7天考勤统计
            List<Map<String, Object>> attendanceTrend = new ArrayList<>();
            for (int i = 6; i >= 0; i--) {
                LocalDate day = today.minusDays(i);
                String dateStr = day.toString();
                long count = attendances.stream()
                        .filter(a -> a.getCreateTime() != null && a.getCreateTime().toLocalDate().equals(day))
                        .count();
                Map<String, Object> item = new HashMap<>();
                item.put("date", dateStr);
                item.put("count", count);
                attendanceTrend.add(item);
            }
            result.put("attendanceTrend", attendanceTrend);

            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
