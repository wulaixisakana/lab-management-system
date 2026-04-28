package com.lab.controller;

import com.lab.annotation.RequireRole;
import com.lab.common.Result;
import com.lab.entity.OperationLog;
import com.lab.service.OperationLogService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/log")
public class OperationLogController {

    @Resource
    private OperationLogService operationLogService;

    @RequireRole({"admin"})
    @GetMapping("/list")
    public Result<List<OperationLog>> list(
            @RequestParam(required = false) String userName,
            @RequestParam(required = false) String module) {
        try {
            return Result.success(operationLogService.findAll(userName, module));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
