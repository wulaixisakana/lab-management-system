package com.lab.aspect;

import com.lab.annotation.RequireRole;
import com.lab.util.JwtUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

@Aspect
@Component
public class RoleAspect {

    @Around("@annotation(requireRole)")
    public Object checkRole(ProceedingJoinPoint point, RequireRole requireRole) throws Throwable {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            throw new RuntimeException("无权限访问");
        }
        HttpServletRequest request = attributes.getRequest();
        String auth = request.getHeader("Authorization");
        if (auth == null || !auth.startsWith("Bearer ")) {
            throw new RuntimeException("未登录");
        }
        String token = auth.substring(7);
        String role = JwtUtil.getRole(token);
        String[] allowedRoles = requireRole.value();

        if (!Arrays.asList(allowedRoles).contains(role)) {
            throw new RuntimeException("无权限访问，需要角色：" + Arrays.toString(allowedRoles));
        }
        return point.proceed();
    }
}
