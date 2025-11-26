package com.yourcompany.hrms.entity.user;

import com.yourcompany.hrms.entity.EmploymentType;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {

    @Email(message = "Email should be valid")
    private String email;

    private String fullName;

    private String phone;

    private String employeeCode;

    private String roleName;

    private Boolean isActive;

    private String username;

    private String department;

    private String designation;

    private LocalDate dateOfJoining;

    private EmploymentType employmentType;

    private String profileImageUrl;
}

