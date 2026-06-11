package com.cornea.management.controller;

import com.cornea.management.dto.ApiResponse;
import com.cornea.management.dto.LoginRequest;
import com.cornea.management.dto.LoginResponse;
import com.cornea.management.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final UserService userService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@RequestBody LoginRequest request) throws SQLException {
        if (request.getUsername() == null || request.getUsername().isBlank()) {
            throw new IllegalArgumentException("用户名不能为空");
        }
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new IllegalArgumentException("密码不能为空");
        }
        LoginResponse resp = userService.login(request.getUsername().trim(), request.getPassword());
        log.info("Login successful: username={}", request.getUsername());
        return ApiResponse.success("登录成功", resp);
    }

    @PostMapping("/logout")
    public ApiResponse<String> logout(HttpServletRequest request) throws SQLException {
        String token = request.getHeader("Authorization");
        if (token != null && !token.isBlank()) {
            userService.logout(token.trim());
        }
        return ApiResponse.success("已退出登录");
    }
}
