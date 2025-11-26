package com.yourcompany.hrms.user;

import com.yourcompany.hrms.entity.EmploymentType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @NotBlank(message = "Full name is required")
    private String fullName;

    private String phone;

    private String employeeCode;

    @NotBlank(message = "Role name is required")
    private String roleName;

    private String department;
    private String designation;

    private String profileImageUrl;

    private LocalDate dateOfJoining;
    private EmploymentType employmentType;
}

