package com.lab.controller;

import com.lab.annotation.Log;
import com.lab.annotation.RequireRole;
import com.lab.common.Result;
import com.lab.entity.User;
import com.lab.service.UserService;
import com.lab.util.JwtUtil;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    @Log(module = "用户", action = "登录")
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> params) {
        try {
            String username = params.get("username");
            String password = params.get("password");
            Map<String, Object> result = userService.login(username, password);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @Log(module = "用户", action = "注册")
    @PostMapping("/register")
    public Result<Void> register(@RequestBody Map<String, String> params) {
        try {
            userService.register(
                    params.get("username"),
                    params.get("password"),
                    params.get("realName"),
                    params.get("phone"),
                    params.get("email")
            );
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/info")
    public Result<User> getInfo(HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization").substring(7);
            Integer userId = JwtUtil.getUserId(token);
            User user = userService.findById(userId);
            return Result.success(userService.getUserInfo(user));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @RequireRole({"admin"})
    @GetMapping("/list")
    public Result<List<User>> list() {
        try {
            return Result.success(userService.findAll());
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @RequireRole({"admin"})
    @Log(module = "用户", action = "更新用户")
    @PostMapping("/update")
    public Result<Void> update(@RequestBody User user) {
        try {
            userService.update(user);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/profile")
    public Result<User> updateProfile(@RequestBody Map<String, String> params, HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization").substring(7);
            Integer userId = JwtUtil.getUserId(token);
            User user = userService.findById(userId);
            if (params.get("realName") != null) user.setRealName(params.get("realName"));
            if (params.get("phone") != null) user.setPhone(params.get("phone"));
            if (params.get("email") != null) user.setEmail(params.get("email"));
            user.setPassword(null);
            userService.update(user);
            return Result.success(userService.getUserInfo(userService.findById(userId)));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/changePassword")
    public Result<Void> changePassword(@RequestBody Map<String, String> params, HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization").substring(7);
            Integer userId = JwtUtil.getUserId(token);
            String oldPassword = params.get("oldPassword");
            String newPassword = params.get("newPassword");
            userService.changePassword(userId, oldPassword, newPassword);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @RequireRole({"admin"})
    @Log(module = "用户", action = "删除用户")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Integer id) {
        try {
            userService.deleteById(id);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
