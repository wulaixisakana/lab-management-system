package com.lab.service;

import com.lab.entity.User;
import com.lab.mapper.UserMapper;
import com.lab.util.JwtUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Service
public class UserService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private JwtUtil jwtUtil;

    private static final BCryptPasswordEncoder BCRYPT = new BCryptPasswordEncoder();
    private static final Pattern PHONE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w.-]+@[\\w.-]+\\.\\w+$");

    public Map<String, Object> login(String username, String password) {
        User user = userMapper.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        boolean matched;
        boolean needMigrate = false;
        if (user.getPassword().startsWith("$2a$") || user.getPassword().startsWith("$2b$")) {
            matched = BCRYPT.matches(password, user.getPassword());
        } else {
            matched = DigestUtils.md5DigestAsHex(password.getBytes()).equals(user.getPassword());
            needMigrate = matched;
        }
        if (!matched) {
            throw new RuntimeException("密码错误");
        }

        if (needMigrate) {
            User u = new User();
            u.setId(user.getId());
            u.setPassword(BCRYPT.encode(password));
            userMapper.update(u);
        }

        if ("disabled".equals(user.getStatus())) {
            throw new RuntimeException("账号已被禁用");
        }

        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("user", getUserInfo(user));
        return result;
    }

    public void register(String username, String password, String realName, String phone, String email) {
        if (username == null || username.trim().length() < 3 || username.trim().length() > 20) {
            throw new RuntimeException("用户名长度应为3-20个字符");
        }
        if (password == null || password.length() < 6) {
            throw new RuntimeException("密码长度不能少于6位");
        }
        if (realName == null || realName.trim().isEmpty()) {
            throw new RuntimeException("请输入真实姓名");
        }
        if (phone != null && !phone.isEmpty() && !PHONE_PATTERN.matcher(phone).matches()) {
            throw new RuntimeException("手机号格式不正确");
        }
        if (email != null && !email.isEmpty() && !EMAIL_PATTERN.matcher(email).matches()) {
            throw new RuntimeException("邮箱格式不正确");
        }

        User existUser = userMapper.findByUsername(username);
        if (existUser != null) {
            throw new RuntimeException("用户名已存在");
        }

        User user = new User();
        user.setUsername(username.trim());
        user.setPassword(BCRYPT.encode(password));
        user.setRealName(realName.trim());
        user.setPhone(phone);
        user.setEmail(email);
        user.setRole("student");
        user.setStatus("active");

        userMapper.insert(user);
    }

    public User getUserInfo(User user) {
        User userInfo = new User();
        userInfo.setId(user.getId());
        userInfo.setUsername(user.getUsername());
        userInfo.setRealName(user.getRealName());
        userInfo.setPhone(user.getPhone());
        userInfo.setEmail(user.getEmail());
        userInfo.setRole(user.getRole());
        userInfo.setStatus(user.getStatus());
        return userInfo;
    }

    public List<User> findAll() {
        return userMapper.findAll();
    }

    public User findById(Integer id) {
        return userMapper.findById(id);
    }

    public void update(User user) {
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(BCRYPT.encode(user.getPassword()));
        }
        userMapper.update(user);
    }

    public void changePassword(Integer userId, String oldPassword, String newPassword) {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        boolean matched;
        if (user.getPassword().startsWith("$2a$") || user.getPassword().startsWith("$2b$")) {
            matched = BCRYPT.matches(oldPassword, user.getPassword());
        } else {
            matched = DigestUtils.md5DigestAsHex(oldPassword.getBytes()).equals(user.getPassword());
        }
        if (!matched) {
            throw new RuntimeException("原密码错误");
        }
        if (newPassword == null || newPassword.length() < 6) {
            throw new RuntimeException("新密码长度不能少于6位");
        }
        User updateUser = new User();
        updateUser.setId(userId);
        updateUser.setPassword(BCRYPT.encode(newPassword));
        userMapper.update(updateUser);
    }

    public User findByUsername(String username) {
        return userMapper.findByUsername(username);
    }

    public void deleteById(Integer id) {
        userMapper.deleteById(id);
    }
}
