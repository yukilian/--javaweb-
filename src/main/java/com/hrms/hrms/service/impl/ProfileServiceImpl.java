package com.hrms.hrms.service.impl;

import com.hrms.hrms.dto.PasswordDTO;
import com.hrms.hrms.dto.SecurityDTO;
import com.hrms.hrms.entity.Employee;
import com.hrms.hrms.entity.UserAccount;
import com.hrms.hrms.repository.DepartmentRepository;
import com.hrms.hrms.repository.EmployeeRepository;
import com.hrms.hrms.repository.UserAccountRepository;
import com.hrms.hrms.service.ProfileService;
import com.hrms.hrms.vo.EmployeeVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProfileServiceImpl implements ProfileService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Override
    public EmployeeVO getProfile(Integer empId) {
        Employee emp = employeeRepository.findById(empId)
                .orElseThrow(() -> new RuntimeException("职员不存在"));
        EmployeeVO vo = new EmployeeVO();
        vo.setEmpId(emp.getEmpId());
        vo.setEmpName(emp.getEmpName());
        vo.setGender(emp.getGender());
        vo.setBirthDate(emp.getBirthDate());
        vo.setDeptId(emp.getDeptId());
        vo.setPhone(emp.getPhone());
        vo.setAddress(emp.getAddress());
        vo.setHireDate(emp.getHireDate());
        vo.setPosition(emp.getPosition());
        vo.setEducation(emp.getEducation());
        if (emp.getDeptId() != null) {
            departmentRepository.findById(emp.getDeptId())
                    .ifPresent(dept -> vo.setDeptName(dept.getDeptName()));
        }
        return vo;
    }

    @Override
    public void updateField(Integer empId, String field, String value) {
        Employee emp = employeeRepository.findById(empId)
                .orElseThrow(() -> new RuntimeException("职员不存在"));
        switch (field) {
            case "empName" -> emp.setEmpName(value);
            case "gender"  -> emp.setGender(value);
            case "phone"   -> emp.setPhone(value);
            case "address" -> emp.setAddress(value);
            case "birthDate" -> emp.setBirthDate(java.time.LocalDate.parse(value));
            default -> throw new RuntimeException("不支持修改的字段：" + field);
        }
        employeeRepository.save(emp);
    }

    @Override
    public void changePassword(Integer userId, PasswordDTO dto) {
        if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
            throw new RuntimeException("两次输入的新密码不一致");
        }
        if (dto.getNewPassword().length() < 4) {
            throw new RuntimeException("新密码长度不能少于4位");
        }
        UserAccount user = userAccountRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        if (!user.getPassword().equals(AuthServiceImpl.hashPassword(dto.getOldPassword()))) {
            throw new RuntimeException("当前密码不正确");
        }
        user.setPassword(AuthServiceImpl.hashPassword(dto.getNewPassword()));
        userAccountRepository.save(user);
    }

    @Override
    public void editSecurity(Integer userId, SecurityDTO dto) {
        UserAccount user = userAccountRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        // 有原答案时验证
        if (user.getSecurityAnswer() != null && !user.getSecurityAnswer().isEmpty()) {
            if (!AuthServiceImpl.hashPassword(dto.getOldAnswer())
                    .equals(user.getSecurityAnswer())) {
                throw new RuntimeException("原答案不正确");
            }
        }
        if (dto.getSecurityQuestion() == null || dto.getNewAnswer() == null) {
            throw new RuntimeException("密保问题和答案不能为空");
        }
        user.setSecurityQuestion(dto.getSecurityQuestion());
        user.setSecurityAnswer(AuthServiceImpl.hashPassword(dto.getNewAnswer()));
        userAccountRepository.save(user);
    }

    @Override
    public String getSecurityInfo(Integer userId) {
        UserAccount user = userAccountRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        return user.getSecurityQuestion() != null ? user.getSecurityQuestion() : "未设置";
    }
}