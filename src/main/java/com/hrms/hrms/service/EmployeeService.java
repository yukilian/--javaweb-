package com.hrms.hrms.service;

import com.hrms.hrms.dto.EmployeeDTO;
import com.hrms.hrms.vo.EmployeeVO;
import java.util.List;

public interface EmployeeService {
    List<EmployeeVO> getAllEmployees(String keyword);
    EmployeeVO getEmployeeById(Integer empId);
    void addEmployee(EmployeeDTO dto);
    void updateEmployee(Integer empId, EmployeeDTO dto);
    void deleteEmployee(Integer empId);
    List<EmployeeVO> getEmployeesByDept(Integer deptId, String keyword);
}