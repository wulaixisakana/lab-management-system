package com.lab.service;

import com.lab.entity.LabReservation;
import com.lab.entity.Laboratory;
import com.lab.entity.User;
import com.lab.mapper.LabReservationMapper;
import com.lab.mapper.LaboratoryMapper;
import com.lab.mapper.UserMapper;
import com.lab.util.JwtUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class LabReservationService {

    @Resource
    private LabReservationMapper labReservationMapper;

    @Resource
    private LaboratoryMapper laboratoryMapper;

    @Resource
    private UserMapper userMapper;

    public List<LabReservation> findAll(String laboratoryName, String userName, String status) {
        return labReservationMapper.findAll(laboratoryName, userName, status);
    }

    public List<LabReservation> findByUserId(Integer userId) {
        return labReservationMapper.findByUserId(userId);
    }

    public LabReservation findById(Integer id) {
        return labReservationMapper.findById(id);
    }

    public void create(LabReservation reservation, HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        Integer userId = JwtUtil.getUserId(token);
        String username = JwtUtil.getUsername(token);
        User user = userMapper.findById(userId);

        validateTime(reservation.getStartTime(), reservation.getEndTime());

        Laboratory lab = laboratoryMapper.findById(reservation.getLaboratoryId());
        if (lab == null) {
            throw new RuntimeException("实验室不存在");
        }
        if (!"available".equals(lab.getStatus())) {
            throw new RuntimeException("实验室当前不可用");
        }
        if (lab.getCapacity() != null && reservation.getParticipantCount() != null
                && reservation.getParticipantCount() > lab.getCapacity()) {
            throw new RuntimeException("参与人数超过实验室容量（最多" + lab.getCapacity() + "人）");
        }

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        List<LabReservation> conflicts = labReservationMapper.findConflictReservations(
                reservation.getLaboratoryId(),
                reservation.getStartTime().format(fmt),
                reservation.getEndTime().format(fmt));
        if (!conflicts.isEmpty()) {
            throw new RuntimeException("该时间段实验室已被预约");
        }

        reservation.setUserId(userId);
        reservation.setUserName(user.getRealName() != null ? user.getRealName() : username);
        reservation.setLaboratoryName(lab.getName());
        reservation.setStatus("pending");
        labReservationMapper.insert(reservation);
    }

    public void approve(Integer id) {
        LabReservation reservation = labReservationMapper.findById(id);
        if (reservation == null) {
            throw new RuntimeException("预约记录不存在");
        }
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        List<LabReservation> conflicts = labReservationMapper.findConflictReservations(
                reservation.getLaboratoryId(),
                reservation.getStartTime().format(fmt),
                reservation.getEndTime().format(fmt));
        for (LabReservation r : conflicts) {
            if (!r.getId().equals(id) && !"cancelled".equals(r.getStatus()) && !"rejected".equals(r.getStatus())) {
                throw new RuntimeException("该时间段存在其他预约冲突");
            }
        }
        labReservationMapper.updateStatus(id, "approved");
    }

    public void reject(Integer id, String reason) {
        if (reason != null && !reason.isEmpty()) {
            labReservationMapper.updateStatusWithReason(id, "rejected", reason);
        } else {
            labReservationMapper.updateStatus(id, "rejected");
        }
    }

    public void cancel(Integer id, HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        Integer userId = JwtUtil.getUserId(token);
        String role = JwtUtil.getRole(token);
        LabReservation reservation = labReservationMapper.findById(id);
        if (reservation == null) {
            throw new RuntimeException("预约记录不存在");
        }
        if (!reservation.getUserId().equals(userId) && !"admin".equals(role) && !"teacher".equals(role)) {
            throw new RuntimeException("无权取消此预约");
        }
        if (!"pending".equals(reservation.getStatus()) && !"approved".equals(reservation.getStatus())) {
            throw new RuntimeException("当前状态无法取消");
        }
        labReservationMapper.updateStatus(id, "cancelled");
    }

    public void deleteById(Integer id) {
        labReservationMapper.deleteById(id);
    }

    private void validateTime(LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime == null || endTime == null) {
            throw new RuntimeException("请选择预约开始和结束时间");
        }
        if (startTime.isBefore(LocalDateTime.now())) {
            throw new RuntimeException("开始时间不能早于当前时间");
        }
        if (!endTime.isAfter(startTime)) {
            throw new RuntimeException("结束时间必须晚于开始时间");
        }
        if (Duration.between(startTime, endTime).toHours() > 8) {
            throw new RuntimeException("单次预约时长不能超过8小时");
        }
    }
}
