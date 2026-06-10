package com.hrms.hrms.controller;

import com.hrms.hrms.common.Result;
import com.hrms.hrms.dto.LoginDTO;
import com.hrms.hrms.service.AuthService;
import com.hrms.hrms.vo.LoginVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    // 登录接口
    @PostMapping("/login")
    public Result<LoginVO> login(@RequestBody LoginDTO loginDTO) {
        try {
            LoginVO vo = authService.login(loginDTO);
            return Result.success(vo);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    // 获取密保问题
    @GetMapping("/security-question")
    public Result<String> getSecurityQuestion(@RequestParam String username) {
        try {
            String question = authService.getSecurityQuestion(username);
            return Result.success(question);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    // 找回密码
    @PostMapping("/reset-password")
    public Result<Void> resetPassword(@RequestBody Map<String, String> body) {
        try {
            authService.resetPassword(
                    body.get("username"),
                    body.get("answer"),
                    body.get("newPassword")
            );
            return Result.success();
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
}