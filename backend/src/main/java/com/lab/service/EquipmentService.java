package com.lab.service;

import com.lab.entity.Equipment;
import com.lab.mapper.EquipmentMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class EquipmentService {

    @Resource
    private EquipmentMapper equipmentMapper;

    public List<Equipment> findAll(String name, String category, String status) {
        return equipmentMapper.findAll(name, category, status);
    }

    public Equipment findById(Integer id) {
        return equipmentMapper.findById(id);
    }

    public void save(Equipment equipment) {
        equipmentMapper.insert(equipment);
    }

    public void update(Equipment equipment) {
        equipmentMapper.update(equipment);
    }

    public void deleteById(Integer id) {
        equipmentMapper.deleteById(id);
    }
}
