package com.hrms.hrms.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "useraccount")
public class UserAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UserID")
    private Integer userId;

    @Column(name = "Username")
    private String username;

    @Column(name = "Password")
    private String password;

    @Column(name = "Role")
    private String role;

    @Column(name = "EmpID")
    private Integer empId;

    @Column(name = "SecurityQuestion")
    private String securityQuestion;

    @Column(name = "SecurityAnswer")
    private String securityAnswer;
}