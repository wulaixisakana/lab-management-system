package com.lab.aspect;

import com.lab.annotation.Log;
import com.lab.entity.*;
import com.lab.mapper.UserMapper;
import com.lab.service.OperationLogService;
import com.lab.util.JwtUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Aspect
@Component
public class LogAspect {

    @Resource
    private OperationLogService operationLogService;

    @Resource
    private UserMapper userMapper;

    @Around("@annotation(log)")
    public Object around(ProceedingJoinPoint point, Log log) throws Throwable {
        Object result = point.proceed();
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String ip = request.getRemoteAddr();
                Integer userId = null;
                String userName = "未知";
                String auth = request.getHeader("Authorization");
                if (auth != null && auth.startsWith("Bearer ")) {
                    try {
                        String token = auth.substring(7);
                        userId = JwtUtil.getUserId(token);
                        User user = userMapper.findById(userId);
                        if (user != null && user.getRealName() != null) {
                            userName = user.getRealName();
                        } else {
                            userName = JwtUtil.getUsername(token);
                        }
                    } catch (Exception ignored) {}
                }
                if ("未知".equals(userName)) {
                    userName = getNameFromArgs(point.getArgs());
                }
                String detail = buildDetail(log.module(), log.action(), point.getArgs());
                operationLogService.log(userId, userName, log.module(), log.action(), detail, ip);
            }
        } catch (Exception ignored) {}
        return result;
    }

    private String buildDetail(String module, String action, Object[] args) {
        StringBuilder sb = new StringBuilder(action);
        try {
            for (Object arg : args) {
                if (arg instanceof Equipment) {
                    Equipment e = (Equipment) arg;
                    if (e.getName() != null) sb.append("：").append(e.getName());
                    return sb.toString();
                }
                if (arg instanceof Laboratory) {
                    Laboratory l = (Laboratory) arg;
                    if (l.getName() != null) sb.append("：").append(l.getName());
                    return sb.toString();
                }
                if (arg instanceof Reservation) {
                    Reservation r = (Reservation) arg;
                    if (r.getEquipmentName() != null) sb.append("：").append(r.getEquipmentName());
                    return sb.toString();
                }
                if (arg instanceof LabReservation) {
                    LabReservation r = (LabReservation) arg;
                    if (r.getLaboratoryName() != null) sb.append("：").append(r.getLaboratoryName());
                    return sb.toString();
                }
                if (arg instanceof User) {
                    User u = (User) arg;
                    if (u.getRealName() != null) sb.append("：").append(u.getRealName());
                    else if (u.getUsername() != null) sb.append("：").append(u.getUsername());
                    return sb.toString();
                }
                if (arg instanceof Map) {
                    Map<?, ?> map = (Map<?, ?>) arg;
                    Object name = map.get("realName");
                    if (name == null) name = map.get("username");
                    if (name != null && !name.toString().trim().isEmpty()) {
                        sb.append("：").append(name);
                        return sb.toString();
                    }
                }
            }
        } catch (Exception ignored) {}
        return sb.toString();
    }

    private String getNameFromArgs(Object[] args) {
        for (Object arg : args) {
            if (arg instanceof Map) {
                Map<?, ?> map = (Map<?, ?>) arg;
                Object realName = map.get("realName");
                if (realName != null && !realName.toString().trim().isEmpty()) {
                    return realName.toString();
                }
                Object username = map.get("username");
                if (username != null && !username.toString().trim().isEmpty()) {
                    return username.toString();
                }
            }
            if (arg instanceof User) {
                User user = (User) arg;
                if (user.getRealName() != null && !user.getRealName().trim().isEmpty()) {
                    return user.getRealName();
                }
                if (user.getUsername() != null && !user.getUsername().trim().isEmpty()) {
                    return user.getUsername();
                }
            }
        }
        return "未知";
    }
}
