package com.lab.mapper;

import com.lab.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper {

    User findByUsername(@Param("username") String username);

    User findById(@Param("id") Integer id);

    List<User> findAll();

    int insert(User user);

    int update(User user);

    int deleteById(@Param("id") Integer id);
}
