package com.yourcompany.hrms.user;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.yourcompany.hrms.entity.EmploymentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private Long id;
    private String email;
    private String fullName;
    private String phone;
    private String employeeCode;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private Long createdById;
    private String createdByFullName;
    private String roleName;
    private Long orgId = 1L;
    private String organizationName;
    private String username;
    private String department;
    private String designation;
    private LocalDate dateOfJoining;
    private EmploymentType employmentType;
    private LocalDateTime lastLogin;
    private String profileImageUrl;

}