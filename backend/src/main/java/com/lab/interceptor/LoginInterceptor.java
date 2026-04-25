package com.lab.interceptor;

import com.lab.common.Result;
import com.lab.util.JwtUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("Authorization");

        if (token == null || !token.startsWith("Bearer ")) {
            sendError(response, 401, "未登录或登录已过期");
            return false;
        }

        token = token.substring(7);
        try {
            if (JwtUtil.validateToken(token)) {
                return true;
            }
        } catch (Exception e) {
            // Token验证失败
        }

        sendError(response, 401, "未登录或登录已过期");
        return false;
    }

    private void sendError(HttpServletResponse response, int code, String message) throws Exception {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        String json = String.format("{\"code\":%d,\"message\":\"%s\",\"data\":null}", code, message);
        response.getWriter().write(json);
    }
}
