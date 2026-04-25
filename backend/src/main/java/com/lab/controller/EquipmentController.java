package com.lab.controller;

import com.lab.common.Result;
import com.lab.entity.Equipment;
import com.lab.service.EquipmentService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/equipment")
@CrossOrigin
public class EquipmentController {

    @Resource
    private EquipmentService equipmentService;

    @GetMapping("/list")
    public Result<List<Equipment>> list(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String status) {
        try {
            return Result.success(equipmentService.findAll(name, category, status));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public Result<Equipment> getById(@PathVariable Integer id) {
        try {
            return Result.success(equipmentService.findById(id));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/save")
    public Result<Void> save(@RequestBody Equipment equipment) {
        try {
            equipmentService.save(equipment);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/update")
    public Result<Void> update(@RequestBody Equipment equipment) {
        try {
            equipmentService.update(equipment);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Integer id) {
        try {
            equipmentService.deleteById(id);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
