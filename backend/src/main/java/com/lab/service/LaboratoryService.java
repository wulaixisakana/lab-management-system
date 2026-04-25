package com.lab.service;

import com.lab.entity.Laboratory;
import com.lab.mapper.LaboratoryMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class LaboratoryService {

    @Resource
    private LaboratoryMapper laboratoryMapper;

    public List<Laboratory> findAll(String name, String building, String status) {
        return laboratoryMapper.findAll(name, building, status);
    }

    public Laboratory findById(Integer id) {
        return laboratoryMapper.findById(id);
    }

    public void save(Laboratory laboratory) {
        laboratoryMapper.insert(laboratory);
    }

    public void update(Laboratory laboratory) {
        laboratoryMapper.update(laboratory);
    }

    public void deleteById(Integer id) {
        laboratoryMapper.deleteById(id);
    }
}
