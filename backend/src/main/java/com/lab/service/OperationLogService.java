package com.lab.service;

import com.lab.entity.OperationLog;
import com.lab.mapper.OperationLogMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class OperationLogService {

    @Resource
    private OperationLogMapper operationLogMapper;

    public List<OperationLog> findAll(String userName, String module) {
        return operationLogMapper.findAll(userName, module);
    }

    public void log(Integer userId, String userName, String module, String action, String detail, String ip) {
        OperationLog log = new OperationLog();
        log.setUserId(userId);
        log.setUserName(userName);
        log.setModule(module);
        log.setAction(action);
        log.setDetail(detail);
        log.setIp(ip);
        operationLogMapper.insert(log);
    }
}
