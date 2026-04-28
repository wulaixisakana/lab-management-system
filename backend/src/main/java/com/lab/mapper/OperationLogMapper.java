package com.lab.mapper;

import com.lab.entity.OperationLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OperationLogMapper {

    List<OperationLog> findAll(@Param("userName") String userName, @Param("module") String module);

    int insert(OperationLog log);
}
