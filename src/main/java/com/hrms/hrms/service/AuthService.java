package com.hrms.hrms.service;

import com.hrms.hrms.dto.LoginDTO;
import com.hrms.hrms.vo.LoginVO;

public interface AuthService {
    LoginVO login(LoginDTO loginDTO);
    void resetPassword(String username, String answer, String newPassword);
    String getSecurityQuestion(String username);
}