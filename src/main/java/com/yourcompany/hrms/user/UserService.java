package com.yourcompany.hrms.user;

import com.yourcompany.hrms.entity.*;
import com.yourcompany.hrms.exception.ResourceNotFoundException;
import com.yourcompany.hrms.repository.OrganizationRepository;
import com.yourcompany.hrms.repository.RoleRepository;
import com.yourcompany.hrms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final OrganizationRepository organizationRepository;



    @Transactional
    public UserResponse createUser(RegisterRequest request, String currentUserEmail) {
        // Get current user
        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Current user not found"));

        // Validate role creation permissions
        RoleName requestedRole = RoleName.valueOf(request.getRoleName().toUpperCase());
        RoleName currentUserRole = currentUser.getRole().getName();

        if (currentUserRole == RoleName.HR && requestedRole != RoleName.EMPLOYEE) {
            throw new IllegalArgumentException("HR can only create EMPLOYEE users");
        }
        if (currentUserRole == RoleName.ADMIN &&
            (requestedRole == RoleName.ADMIN || requestedRole == RoleName.HR)) {
            // Only ADMIN can create ADMIN or HR - this is allowed
        } else if (currentUserRole != RoleName.ADMIN &&
                   (requestedRole == RoleName.ADMIN || requestedRole == RoleName.HR)) {
            throw new IllegalArgumentException("Only ADMIN can create ADMIN or HR users");
        }

        // Check if email already exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists: " + request.getEmail());
        }

        Role role = roleRepository.findByName(requestedRole)
                .orElseThrow(() -> new IllegalArgumentException("Invalid role: " + request.getRoleName()));

        // Generate employee code if not provided
        final String employeeCode;
        if (request.getEmployeeCode() == null || request.getEmployeeCode().trim().isEmpty()) {
            employeeCode = generateEmployeeCodeForRole(requestedRole);
        } else {
            employeeCode = request.getEmployeeCode();
            // Check if provided employee code already exists
            if (userRepository.findAll().stream()
                    .anyMatch(u -> u.getEmployeeCode().equals(employeeCode))) {
                throw new IllegalArgumentException("Employee code already exists: " + employeeCode);
            }
        }



        EmploymentType employmentType =
                request.getEmploymentType() != null ? request.getEmploymentType() : EmploymentType.PROBATION;

//        Organization organization = organizationRepository.findById(request.getOrgId())
//                .orElseThrow(() -> new IllegalArgumentException("Invalid organization ID: " + request.getOrgId()));

        // Default role hard codej
        Organization organization = organizationRepository.findById(1L)
                .orElseThrow(() -> new IllegalArgumentException("Default organization not found (ID = 1). Please create it first."));





        // Create user
        User user = User.builder()
                .organization(organization)
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .department(request.getDepartment())
                .designation(request.getDesignation())
                .dateOfJoining(request.getDateOfJoining())
                .employeeCode(employeeCode)
                .employmentType(employmentType)
                .isActive(true)
                .createdBy(currentUser)
                .role(role)
                .profileImageUrl(request.getProfileImageUrl())
                .build();


        User savedUser = userRepository.save(user);
        return toUserResponse(savedUser);
    }

    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUsers(String search, Pageable pageable) {
        Page<User> users = userRepository.findAllWithSearch(search, pageable);
        return users.map(this::toUserResponse);
    }

    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id, String currentUserEmail) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        // Check if current user is ADMIN/HR or same user
        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Current user not found"));

        RoleName currentUserRole = currentUser.getRole().getName();
        boolean isAdminOrHr = currentUserRole == RoleName.ADMIN || currentUserRole == RoleName.HR;
        boolean isSameUser = currentUser.getId().equals(id);

        if (!isAdminOrHr && !isSameUser) {
            throw new IllegalArgumentException("Access denied: You can only view your own profile");
        }

        return toUserResponse(user);
    }

//    @Transactional
//    public UserResponse updateUser(Long id, UpdateUserRequest request, String currentUserEmail) {
//        User user = userRepository.findById(id)
//                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
//
//        User currentUser = userRepository.findByEmail(currentUserEmail)
//                .orElseThrow(() -> new UsernameNotFoundException("Current user not found"));
//
//        RoleName currentUserRole = currentUser.getRole().getName();
//        boolean isAdminOrHr = currentUserRole == RoleName.ADMIN || currentUserRole == RoleName.HR;
//        boolean isSameUser = currentUser.getId().equals(id);
//
//        if (!isAdminOrHr && !isSameUser) {
//            throw new IllegalArgumentException("Access denied: You can only update your own profile");
//        }
//
//        // If same user (not admin/hr), limit what can be updated
//        if (isSameUser && !isAdminOrHr) {
//            // Same user can only update: fullName, phone
//            if (request.getFullName() != null) {
//                user.setFullName(request.getFullName());
//            }
//            if (request.getPhone() != null) {
//                user.setPhone(request.getPhone());
//            }
//        } else {
//            // ADMIN/HR can update all fields except password
//            if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
//                if (userRepository.findByEmail(request.getEmail()).isPresent()) {
//                    throw new IllegalArgumentException("Email already exists: " + request.getEmail());
//                }
//                user.setEmail(request.getEmail());
//            }
//            if (request.getFullName() != null) {
//                user.setFullName(request.getFullName());
//            }
//            if (request.getPhone() != null) {
//                user.setPhone(request.getPhone());
//            }
//            if (request.getEmployeeCode() != null) {
//                // Check if employee code already exists
//                Optional<User> existingUser = userRepository.findAll().stream()
//                        .filter(u -> u.getEmployeeCode().equals(request.getEmployeeCode()) && !u.getId().equals(id))
//                        .findFirst();
//                if (existingUser.isPresent()) {
//                    throw new IllegalArgumentException("Employee code already exists: " + request.getEmployeeCode());
//                }
//                user.setEmployeeCode(request.getEmployeeCode());
//            }
//            if (request.getRoleName() != null) {
//                RoleName newRole = RoleName.valueOf(request.getRoleName().toUpperCase());
//                Role role = roleRepository.findByName(newRole)
//                        .orElseThrow(() -> new IllegalArgumentException("Invalid role: " + request.getRoleName()));
//                user.setRole(role);
//            }
//            if (request.getIsActive() != null) {
//                user.setActive(request.getIsActive());
//            }
//        }
//
//        User updatedUser = userRepository.save(user);
//        return toUserResponse(updatedUser);
//    }
@Transactional
public UserResponse updateUser(Long id, UpdateUserRequest request, String currentUserEmail) {

    User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

    User currentUser = userRepository.findByEmail(currentUserEmail)
            .orElseThrow(() -> new UsernameNotFoundException("Current user not found"));

    RoleName currentUserRole = currentUser.getRole().getName();
    boolean isAdminOrHr = currentUserRole == RoleName.ADMIN || currentUserRole == RoleName.HR;
    boolean isSameUser = currentUser.getId().equals(id);

    if (!isAdminOrHr && !isSameUser) {
        throw new IllegalArgumentException("Access denied: You can only update your own profile");
    }

    // -----------------------------------------------------------------------------------------
    // CASE 1: Same user (non admin/hr) → can update ONLY fullName + phone
    // -----------------------------------------------------------------------------------------
    if (isSameUser && !isAdminOrHr) {

        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }

        // DO NOT allow updating username, dept, role, etc.
    }
    // -----------------------------------------------------------------------------------------
    // CASE 2: ADMIN / HR → can update everything (except password)
    // -----------------------------------------------------------------------------------------
    else {

        // EMAIL (unique)
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.findByEmail(request.getEmail()).isPresent()) {
                throw new IllegalArgumentException("Email already exists: " + request.getEmail());
            }
            user.setEmail(request.getEmail());
        }

        // USERNAME (unique)
        if (request.getUsername() != null && !request.getUsername().equals(user.getUsername())) {
            if (userRepository.findByUsername(request.getUsername()).isPresent()) {
                throw new IllegalArgumentException("Username already exists: " + request.getUsername());
            }
            user.setUsername(request.getUsername());
        }

        // FULL NAME
        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }

        // PHONE
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }

        // DEPARTMENT
        if (request.getDepartment() != null) {
            user.setDepartment(request.getDepartment());
        }

        // DESIGNATION
        if (request.getDesignation() != null) {
            user.setDesignation(request.getDesignation());
        }

        // DATE OF JOINING
        if (request.getDateOfJoining() != null) {
            user.setDateOfJoining(request.getDateOfJoining());
        }

        // EMPLOYMENT TYPE
        if (request.getEmploymentType() != null) {
            user.setEmploymentType(request.getEmploymentType());
        }

        // EMPLOYEE CODE (unique)
        if (request.getEmployeeCode() != null) {
            Optional<User> existingUser = userRepository.findByEmployeeCode(request.getEmployeeCode());
            if (existingUser.isPresent() && !existingUser.get().getId().equals(id)) {
                throw new IllegalArgumentException("Employee code already exists: " + request.getEmployeeCode());
            }
            user.setEmployeeCode(request.getEmployeeCode());
        }

        // ROLE
        if (request.getRoleName() != null) {
            RoleName newRole = RoleName.valueOf(request.getRoleName().toUpperCase());
            Role role = roleRepository.findByName(newRole)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid role: " + request.getRoleName()));
            user.setRole(role);
        }

        // ACTIVE STATUS
        if (request.getIsActive() != null) {
            user.setActive(request.getIsActive());
        }

        // PROFILE IMAGE URL (if updating via DTO)
        if (request.getProfileImageUrl() != null) {
            user.setProfileImageUrl(request.getProfileImageUrl());
        }
    }

    User updatedUser = userRepository.save(user);
    return toUserResponse(updatedUser);
}


    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        user.setActive(false);
        userRepository.save(user);
    }

    public String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        throw new IllegalStateException("No authenticated user found");
    }

//    private String generateEmployeeCode() {
//        Optional<User> lastUser = userRepository.findTopByEmployeeCodeStartingWithOrderByEmployeeCodeDesc("EMP-");
//
//        if (lastUser.isEmpty()) {
//            return "EMP-1001";
//        }
//
//        String lastCode = lastUser.get().getEmployeeCode();
//        if (lastCode.startsWith("EMP-")) {
//            try {
//                int lastNumber = Integer.parseInt(lastCode.substring(4));
//                return "EMP-" + (lastNumber + 1);
//            } catch (NumberFormatException e) {
//                // If parsing fails, start from 1001
//                return "EMP-1001";
//            }
//        }
//
//        return "EMP-1001";
//    }
private String generateEmployeeCodeForRole(RoleName roleName) {

    String prefix;

    switch (roleName) {
        case ADMIN -> prefix = "ADM-";
        case HR -> prefix = "HR-";
        default -> prefix = "EMP-";  // For EMPLOYEE and all others
    }

    // Fetch the last code starting with prefix
    Optional<User> lastUser = userRepository
            .findTopByEmployeeCodeStartingWithOrderByEmployeeCodeDesc(prefix);

    // If none found → first code
    if (lastUser.isEmpty()) {
        return prefix + "1001";
    }

    String lastCode = lastUser.get().getEmployeeCode();

    try {
        int lastNumber = Integer.parseInt(lastCode.substring(prefix.length()));
        return prefix + (lastNumber + 1);
    } catch (NumberFormatException e) {
        return prefix + "1001"; // If parsing fails
    }
}


    private UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .orgId(user.getOrganization().getId())
                .organizationName(user.getOrganization().getName())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .employeeCode(user.getEmployeeCode())
                .department(user.getDepartment())
                .designation(user.getDesignation())
                .dateOfJoining(user.getDateOfJoining())
                .employmentType(user.getEmploymentType())
                .lastLogin(user.getLastLogin())
                .isActive(user.isActive())
                .createdAt(user.getCreatedAt())
                .createdById(user.getCreatedBy() != null ? user.getCreatedBy().getId() : null)
                .createdByFullName(user.getCreatedBy() != null ? user.getCreatedBy().getFullName() : null)
                .roleName(user.getRole().getName().name())
                .profileImageUrl(user.getProfileImageUrl())
                .build();
    }
}


