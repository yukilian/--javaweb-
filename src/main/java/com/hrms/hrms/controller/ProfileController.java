package com.hrms.hrms.controller;

import com.hrms.hrms.common.Result;
import com.hrms.hrms.dto.PasswordDTO;
import com.hrms.hrms.dto.SecurityDTO;
import com.hrms.hrms.service.ProfileService;
import com.hrms.hrms.vo.EmployeeVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    @Autowired
    private ProfileService profileService;

    // 获取个人信息
    @GetMapping("/{empId}")
    public Result<EmployeeVO> getProfile(@PathVariable Integer empId) {
        try {
            return Result.success(profileService.getProfile(empId));
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    // 修改单个字段
    @PatchMapping("/{empId}/field")
    public Result<Void> updateField(@PathVariable Integer empId,
                                    @RequestBody Map<String, String> body) {
        try {
            profileService.updateField(empId, body.get("field"), body.get("value"));
            return Result.success();
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    // 修改密码
    @PutMapping("/{userId}/password")
    public Result<Void> changePassword(@PathVariable Integer userId,
                                       @RequestBody PasswordDTO dto) {
        try {
            profileService.changePassword(userId, dto);
            return Result.success();
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    // 编辑密保
    @PutMapping("/{userId}/security")
    public Result<Void> editSecurity(@PathVariable Integer userId,
                                     @RequestBody SecurityDTO dto) {
        try {
            profileService.editSecurity(userId, dto);
            return Result.success();
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    // 获取密保问题
    @GetMapping("/{userId}/security")
    public Result<String> getSecurityInfo(@PathVariable Integer userId) {
        try {
            return Result.success(profileService.getSecurityInfo(userId));
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
}