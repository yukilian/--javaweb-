package com.hrms.hrms.service;

import com.hrms.hrms.dto.PasswordDTO;
import com.hrms.hrms.dto.SecurityDTO;
import com.hrms.hrms.vo.EmployeeVO;

public interface ProfileService {
    EmployeeVO getProfile(Integer empId);
    void updateField(Integer empId, String field, String value);
    void changePassword(Integer userId, PasswordDTO dto);
    void editSecurity(Integer userId, SecurityDTO dto);
    String getSecurityInfo(Integer userId);
}