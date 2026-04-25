package com.lab.mapper;

import com.lab.entity.Equipment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface EquipmentMapper {

    Equipment findById(@Param("id") Integer id);

    List<Equipment> findAll(@Param("name") String name, @Param("category") String category, @Param("status") String status);

    int insert(Equipment equipment);

    int update(Equipment equipment);

    int deleteById(@Param("id") Integer id);
}
