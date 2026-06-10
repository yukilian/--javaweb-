package com.hrms.hrms.service.impl;

import com.hrms.hrms.dto.DepartmentDTO;
import com.hrms.hrms.entity.Department;
import com.hrms.hrms.repository.DepartmentRepository;
import com.hrms.hrms.repository.EmployeeRepository;
import com.hrms.hrms.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DepartmentServiceImpl implements DepartmentService {

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    @Override
    public Department getDepartmentById(Integer id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("部门不存在"));
    }

    @Override
    public Department addDepartment(DepartmentDTO dto) {
        Department dept = new Department();
        dept.setDeptName(dto.getDeptName());
        dept.setDeptAddress(dto.getDeptAddress());
        dept.setDeptPhone(dto.getDeptPhone());
        dept.setManagerId(dto.getManagerId());
        return departmentRepository.save(dept);
    }

    @Override
    public Department updateDepartment(Integer id, DepartmentDTO dto) {
        Department dept = departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("部门不存在"));
        dept.setDeptName(dto.getDeptName());
        dept.setDeptAddress(dto.getDeptAddress());
        dept.setDeptPhone(dto.getDeptPhone());
        dept.setManagerId(dto.getManagerId());
        return departmentRepository.save(dept);
    }

    @Override
    @Transactional
    public void deleteDepartment(Integer id) {
        // 删除部门前先把该部门员工的DeptID设为null
        employeeRepository.findByDeptId(id)
                .forEach(emp -> {
                    emp.setDeptId(null);
                    employeeRepository.save(emp);
                });
        departmentRepository.deleteById(id);
    }
}