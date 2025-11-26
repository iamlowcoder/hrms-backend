package com.yourcompany.hrms.config;

import com.yourcompany.hrms.entity.Role;
import com.yourcompany.hrms.entity.RoleName;
import com.yourcompany.hrms.entity.user.User;
import com.yourcompany.hrms.repository.RoleRepository;
import com.yourcompany.hrms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Only run if no users exist
        if (userRepository.count() == 0) {
            // Ensure ADMIN role exists
            Role adminRole = roleRepository.findByName(RoleName.ADMIN)
                    .orElseGet(() -> {
                        Role role = Role.builder()
                                .name(RoleName.ADMIN)
                                .build();
                        return roleRepository.save(role);
                    });

            // Create default admin user
            User adminUser = User.builder()
                    .email("admin@company.com")
                    .password(passwordEncoder.encode("Admin@123"))
                    .fullName("System Administrator")
                    .employeeCode("ADMIN-001")
                    .isActive(true)
                    .role(adminRole)
                    .build();

            userRepository.save(adminUser);
            System.out.println("Default admin created");
            log.info("Default admin user created with email: admin@company.com");
        }
    }
}

