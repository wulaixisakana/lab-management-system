package com.lab.controller;

import com.lab.common.Result;
import com.lab.mapper.EquipmentMapper;
import com.lab.mapper.LaboratoryMapper;
import com.lab.mapper.ReservationMapper;
import com.lab.mapper.AttendanceMapper;
import com.lab.mapper.UserMapper;
import com.lab.entity.Equipment;
import com.lab.entity.Reservation;
import com.lab.entity.Attendance;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/statistics")
@CrossOrigin
public class StatisticsController {

    @Resource
    private EquipmentMapper equipmentMapper;

    @Resource
    private LaboratoryMapper laboratoryMapper;

    @Resource
    private ReservationMapper reservationMapper;

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
            result.put("reservationCount", reservationMapper.findAll(null, null, null).size());
            result.put("attendanceCount", attendanceMapper.findAll(null, null, null).size());
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

            // 预约状态统计
            List<Reservation> reservations = reservationMapper.findAll(null, null, null);
            Map<String, Long> reservationStatusStats = reservations.stream()
                    .filter(r -> r.getStatus() != null)
                    .collect(Collectors.groupingBy(Reservation::getStatus, Collectors.counting()));
            result.put("reservationStatusStats", reservationStatusStats);

            // 最近7天预约趋势
            List<Map<String, Object>> reservationTrend = new ArrayList<>();
            LocalDate today = LocalDate.now();
            for (int i = 6; i >= 0; i--) {
                LocalDate day = today.minusDays(i);
                String dateStr = day.toString();
                long count = reservations.stream()
                        .filter(r -> r.getCreateTime() != null && r.getCreateTime().toLocalDate().equals(day))
                        .count();
                Map<String, Object> item = new HashMap<>();
                item.put("date", dateStr);
                item.put("count", count);
                reservationTrend.add(item);
            }
            result.put("reservationTrend", reservationTrend);

            // 最近7天考勤统计
            List<Attendance> attendances = attendanceMapper.findAll(null, null, null);
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
