package com.hrms.hrms.repository;

import com.hrms.hrms.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
    List<Employee> findByEmpNameContaining(String empName);
    List<Employee> findByDeptId(Integer deptId);
    List<Employee> findByDeptIdAndEmpNameContaining(Integer deptId, String empName);
}