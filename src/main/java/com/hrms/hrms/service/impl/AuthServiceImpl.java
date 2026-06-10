package com.hrms.hrms.service.impl;

import com.hrms.hrms.dto.LoginDTO;
import com.hrms.hrms.entity.Employee;
import com.hrms.hrms.entity.UserAccount;
import com.hrms.hrms.repository.DepartmentRepository;
import com.hrms.hrms.repository.EmployeeRepository;
import com.hrms.hrms.repository.UserAccountRepository;
import com.hrms.hrms.service.AuthService;
import com.hrms.hrms.vo.LoginVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.hrms.hrms.entity.Department;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    // SHA-256 加密，和Python端保持一致
    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256加密失败", e);
        }
    }

    @Override
    public LoginVO login(LoginDTO loginDTO) {
        // 1. 查询用户账号
        Optional<UserAccount> optUser = userAccountRepository
                .findByUsernameAndPassword(
                        loginDTO.getUsername(),
                        hashPassword(loginDTO.getPassword())
                );

        if (optUser.isEmpty()) {
            throw new RuntimeException("账号或密码错误");
        }

        UserAccount user = optUser.get();

        // 2. 查询关联的职员信息
        Employee employee = null;
        if (user.getEmpId() != null) {
            employee = employeeRepository.findById(user.getEmpId()).orElse(null);
        }

        // 3. 判断是否是部门负责人
        boolean isDeptManager = false;
        Integer deptId = null;
        if (employee != null && employee.getDeptId() != null) {
            deptId = employee.getDeptId();
            Optional<Department> dept = departmentRepository.findById(deptId);
            if (dept.isPresent() && employee.getEmpId().equals(dept.get().getManagerId())) {
                isDeptManager = true;
            }
        }

        // 4. 组装返回数据
        LoginVO vo = new LoginVO();
        vo.setUserId(user.getUserId());
        vo.setUsername(user.getUsername());
        vo.setRole(user.getRole());
        vo.setEmpId(user.getEmpId());
        vo.setEmpName(employee != null ? employee.getEmpName() : user.getUsername());
        vo.setDeptId(deptId);
        vo.setIsDeptManager(isDeptManager);

        return vo;
    }

    @Override
    public String getSecurityQuestion(String username) {
        Optional<UserAccount> user = userAccountRepository.findByUsername(username);
        if (user.isEmpty()) {
            throw new RuntimeException("账号不存在");
        }
        String question = user.get().getSecurityQuestion();
        if (question == null || question.isEmpty()) {
            throw new RuntimeException("该账号未设置密保问题");
        }
        return question;
    }

    @Override
    public void resetPassword(String username, String answer, String newPassword) {
        Optional<UserAccount> optUser = userAccountRepository.findByUsername(username);
        if (optUser.isEmpty()) {
            throw new RuntimeException("账号不存在");
        }
        UserAccount user = optUser.get();
        // 验证密保答案
        if (!hashPassword(answer).equals(user.getSecurityAnswer())) {
            throw new RuntimeException("密保答案错误");
        }
        // 重置密码
        user.setPassword(hashPassword(newPassword));
        userAccountRepository.save(user);
    }
}