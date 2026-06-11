package com.hrms.hrms.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "OperationLog")
public class OperationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LogID")
    private Integer logId;

    @Column(name = "Username")
    private String username;

    @Column(name = "Role")
    private String role;

    @Column(name = "Action")
    private String action;

    @Column(name = "TargetDesc")
    private String targetDesc;

    @Column(name = "IP")
    private String ip;

    @Column(name = "CreateTime")
    private LocalDateTime createTime;

    @Column(name = "Success")
    private Boolean success;
}