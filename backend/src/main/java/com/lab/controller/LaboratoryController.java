package com.lab.controller;

import com.lab.common.Result;
import com.lab.entity.Laboratory;
import com.lab.service.LaboratoryService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/laboratory")
@CrossOrigin
public class LaboratoryController {

    @Resource
    private LaboratoryService laboratoryService;

    @GetMapping("/list")
    public Result<List<Laboratory>> list(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String building,
            @RequestParam(required = false) String status) {
        try {
            return Result.success(laboratoryService.findAll(name, building, status));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public Result<Laboratory> getById(@PathVariable Integer id) {
        try {
            return Result.success(laboratoryService.findById(id));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/save")
    public Result<Void> save(@RequestBody Laboratory laboratory) {
        try {
            laboratoryService.save(laboratory);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/update")
    public Result<Void> update(@RequestBody Laboratory laboratory) {
        try {
            laboratoryService.update(laboratory);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Integer id) {
        try {
            laboratoryService.deleteById(id);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
