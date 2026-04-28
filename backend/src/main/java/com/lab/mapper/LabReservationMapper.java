package com.lab.mapper;

import com.lab.entity.LabReservation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface LabReservationMapper {

    List<LabReservation> findAll(@Param("laboratoryName") String laboratoryName,
                                 @Param("userName") String userName,
                                 @Param("status") String status);

    List<LabReservation> findByUserId(@Param("userId") Integer userId);

    LabReservation findById(@Param("id") Integer id);

    List<LabReservation> findConflictReservations(@Param("laboratoryId") Integer laboratoryId,
                                                  @Param("startTime") String startTime,
                                                  @Param("endTime") String endTime);

    int insert(LabReservation reservation);

    int updateStatus(@Param("id") Integer id, @Param("status") String status);

    int updateStatusWithReason(@Param("id") Integer id, @Param("status") String status, @Param("rejectReason") String rejectReason);

    int deleteById(@Param("id") Integer id);
}
