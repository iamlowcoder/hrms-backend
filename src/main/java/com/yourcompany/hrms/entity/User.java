package com.yourcompany.hrms.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", nullable = false, unique = true, length = 320)
    private String email;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "org_id")
    private Organization organization;

    @Column(name = "username", unique = true, length = 100)
    private String username;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "full_name", nullable = false, length = 255)
    private String fullName;

    @Column(name = "phone", length = 50)
    private String phone;

    @Column(name = "employee_code", nullable = false, unique = true, length = 100)
    private String employeeCode;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private boolean isActive = true;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id")
    private User createdBy;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;
    @Column(name = "department", length = 150)
    private String department;

    @Column(name = "designation", length = 150)
    private String designation;

    @Column(name = "doj")
    private LocalDate dateOfJoining;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    @Enumerated(EnumType.STRING)
    @Column(name = "employment_type", nullable = false, length = 50,
            columnDefinition = "varchar(50) default 'PROBATION'")
    @Builder.Default
    private EmploymentType employmentType = EmploymentType.PROBATION;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @PrePersist
    private void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}

