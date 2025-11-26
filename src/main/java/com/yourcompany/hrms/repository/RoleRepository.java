package com.yourcompany.hrms.repository;

import com.yourcompany.hrms.entity.Role;
import com.yourcompany.hrms.entity.RoleName;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName name);
}

