package com.lab.service;

import com.lab.entity.Equipment;
import com.lab.entity.Reservation;
import com.lab.mapper.EquipmentMapper;
import com.lab.mapper.ReservationMapper;
import com.lab.util.JwtUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ReservationService {

    @Resource
    private ReservationMapper reservationMapper;

    @Resource
    private EquipmentMapper equipmentMapper;

    public List<Reservation> findAll(String equipmentName, String userName, String status) {
        return reservationMapper.findAll(equipmentName, userName, status);
    }

    public List<Reservation> findByUserId(Integer userId) {
        return reservationMapper.findByUserId(userId);
    }

    public Reservation findById(Integer id) {
        return reservationMapper.findById(id);
    }

    @Transactional
    public void create(Reservation reservation, HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        Integer userId = JwtUtil.getUserId(token);
        String username = JwtUtil.getUsername(token);

        Equipment equipment = equipmentMapper.findById(reservation.getEquipmentId());
        if (equipment == null) {
            throw new RuntimeException("设备不存在");
        }

        if (!"available".equals(equipment.getStatus())) {
            throw new RuntimeException("设备当前不可用");
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        List<Reservation> conflicts = reservationMapper.findConflictReservations(
                reservation.getEquipmentId(),
                reservation.getStartTime().format(formatter),
                reservation.getEndTime().format(formatter)
        );

        if (!conflicts.isEmpty()) {
            throw new RuntimeException("该时间段已被预约");
        }

        reservation.setUserId(userId);
        reservation.setUserName(username);
        reservation.setEquipmentName(equipment.getName());
        reservation.setStatus("pending");
        reservationMapper.insert(reservation);
    }

    @Transactional
    public void approve(Integer id) {
        Reservation reservation = reservationMapper.findById(id);
        if (reservation == null) {
            throw new RuntimeException("预约记录不存在");
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        List<Reservation> conflicts = reservationMapper.findConflictReservations(
                reservation.getEquipmentId(),
                reservation.getStartTime().format(formatter),
                reservation.getEndTime().format(formatter)
        );

        for (Reservation r : conflicts) {
            if (!r.getId().equals(id) && !"cancelled".equals(r.getStatus()) && !"rejected".equals(r.getStatus())) {
                throw new RuntimeException("该时间段存在其他预约");
            }
        }

        reservation.setStatus("approved");
        reservationMapper.update(reservation);
    }

    @Transactional
    public void reject(Integer id) {
        Reservation reservation = reservationMapper.findById(id);
        if (reservation == null) {
            throw new RuntimeException("预约记录不存在");
        }
        reservation.setStatus("rejected");
        reservationMapper.update(reservation);
    }

    @Transactional
    public void cancel(Integer id, HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        Integer userId = JwtUtil.getUserId(token);
        String role = JwtUtil.getRole(token);

        Reservation reservation = reservationMapper.findById(id);
        if (reservation == null) {
            throw new RuntimeException("预约记录不存在");
        }

        if (!reservation.getUserId().equals(userId) && !"admin".equals(role) && !"teacher".equals(role)) {
            throw new RuntimeException("无权取消此预约");
        }

        if ("approved".equals(reservation.getStatus()) || "pending".equals(reservation.getStatus())) {
            reservation.setStatus("cancelled");
            reservationMapper.update(reservation);
        } else {
            throw new RuntimeException("当前状态无法取消");
        }
    }

    public void deleteById(Integer id) {
        reservationMapper.deleteById(id);
    }
}
