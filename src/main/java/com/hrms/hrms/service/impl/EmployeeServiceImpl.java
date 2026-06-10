package com.hrms.hrms.service.impl;

import com.hrms.hrms.dto.EmployeeDTO;
import com.hrms.hrms.entity.Department;
import com.hrms.hrms.entity.Employee;
import com.hrms.hrms.repository.AttendanceRepository;
import com.hrms.hrms.repository.DepartmentRepository;
import com.hrms.hrms.repository.EmployeeRepository;
import com.hrms.hrms.repository.SalaryRepository;
import com.hrms.hrms.repository.UserAccountRepository;
import com.hrms.hrms.service.EmployeeService;
import com.hrms.hrms.vo.EmployeeVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private SalaryRepository salaryRepository;

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private UserAccountRepository userAccountRepository;

    // 把Employee实体转换为EmployeeVO
    private EmployeeVO toVO(Employee emp) {
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
        // 查询部门名称
        if (emp.getDeptId() != null) {
            departmentRepository.findById(emp.getDeptId())
                    .ifPresent(dept -> vo.setDeptName(dept.getDeptName()));
        }
        return vo;
    }

    @Override
    public List<EmployeeVO> getAllEmployees(String keyword) {
        List<Employee> list = (keyword == null || keyword.isEmpty())
                ? employeeRepository.findAll()
                : employeeRepository.findByEmpNameContaining(keyword);
        return list.stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    public EmployeeVO getEmployeeById(Integer empId) {
        Employee emp = employeeRepository.findById(empId)
                .orElseThrow(() -> new RuntimeException("职员不存在"));
        return toVO(emp);
    }

    @Override
    public void addEmployee(EmployeeDTO dto) {
        if (employeeRepository.existsById(dto.getEmpId())) {
            throw new RuntimeException("职员编号已存在");
        }
        Employee emp = new Employee();
        emp.setEmpId(dto.getEmpId());
        emp.setEmpName(dto.getEmpName());
        emp.setGender(dto.getGender());
        emp.setBirthDate(dto.getBirthDate());
        emp.setDeptId(dto.getDeptId());
        emp.setPhone(dto.getPhone());
        emp.setAddress(dto.getAddress());
        emp.setHireDate(dto.getHireDate());
        emp.setPosition(dto.getPosition());
        emp.setEducation(dto.getEducation());
        employeeRepository.save(emp);
    }

    @Override
    public void updateEmployee(Integer empId, EmployeeDTO dto) {
        Employee emp = employeeRepository.findById(empId)
                .orElseThrow(() -> new RuntimeException("职员不存在"));
        emp.setEmpName(dto.getEmpName());
        emp.setGender(dto.getGender());
        emp.setBirthDate(dto.getBirthDate());
        emp.setDeptId(dto.getDeptId());
        emp.setPhone(dto.getPhone());
        emp.setAddress(dto.getAddress());
        emp.setHireDate(dto.getHireDate());
        emp.setPosition(dto.getPosition());
        emp.setEducation(dto.getEducation());
        employeeRepository.save(emp);
    }

    @Override
    @Transactional
    public void deleteEmployee(Integer empId) {
        if (!employeeRepository.existsById(empId)) {
            throw new RuntimeException("职员不存在");
        }
        // 按原系统逻辑，删除职员时同步删除相关数据
        attendanceRepository.deleteByEmpId(empId);
        salaryRepository.deleteByEmpId(empId);
        userAccountRepository.deleteByEmpId(empId);
        employeeRepository.deleteById(empId);
    }

    @Override
    public List<EmployeeVO> getEmployeesByDept(Integer deptId, String keyword) {
        List<Employee> list = (keyword == null || keyword.isEmpty())
                ? employeeRepository.findByDeptId(deptId)
                : employeeRepository.findByDeptIdAndEmpNameContaining(deptId, keyword);
        return list.stream().map(this::toVO).collect(Collectors.toList());
    }
}