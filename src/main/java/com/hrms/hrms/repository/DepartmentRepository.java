// DepartmentRepository.java
package com.hrms.hrms.repository;

import com.hrms.hrms.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Integer> {

    // 按部门名查询
    Optional<Department> findByDeptName(String deptName);
}