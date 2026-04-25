package com.lab.mapper;

import com.lab.entity.Reservation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ReservationMapper {

    Reservation findById(@Param("id") Integer id);

    List<Reservation> findByUserId(@Param("userId") Integer userId);

    List<Reservation> findAll(@Param("equipmentName") String equipmentName,
                             @Param("userName") String userName,
                             @Param("status") String status);

    List<Reservation> findConflictReservations(@Param("equipmentId") Integer equipmentId,
                                               @Param("startTime") String startTime,
                                               @Param("endTime") String endTime);

    int insert(Reservation reservation);

    int update(Reservation reservation);

    int deleteById(@Param("id") Integer id);
}
