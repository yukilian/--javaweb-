package com.hrms.hrms.service;

import com.hrms.hrms.dto.DepartmentDTO;
import com.hrms.hrms.entity.Department;
import java.util.List;

public interface DepartmentService {
    List<Department> getAllDepartments();
    Department getDepartmentById(Integer id);
    Department addDepartment(DepartmentDTO dto);
    Department updateDepartment(Integer id, DepartmentDTO dto);
    void deleteDepartment(Integer id);
}