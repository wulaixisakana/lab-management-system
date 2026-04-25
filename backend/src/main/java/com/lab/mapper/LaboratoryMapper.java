package com.lab.mapper;

import com.lab.entity.Laboratory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface LaboratoryMapper {

    Laboratory findById(@Param("id") Integer id);

    List<Laboratory> findAll(@Param("name") String name, @Param("building") String building, @Param("status") String status);

    int insert(Laboratory laboratory);

    int update(Laboratory laboratory);

    int deleteById(@Param("id") Integer id);
}
