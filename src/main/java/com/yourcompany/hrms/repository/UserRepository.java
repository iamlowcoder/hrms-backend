package com.yourcompany.hrms.repository;

import com.yourcompany.hrms.entity.RoleName;
import com.yourcompany.hrms.entity.user.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    List<User> findByCreatedById(Long createdById);

    @Query("SELECT u FROM User u WHERE u.role.name = :roleName")
    List<User> findAllByRoleName(@Param("roleName") RoleName roleName);

    @Query("SELECT u FROM User u WHERE " +
           "(:search IS NULL OR :search = '' OR " +
           "LOWER(u.fullName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<User> findAllWithSearch(@Param("search") String search, Pageable pageable);

    Optional<User> findTopByEmployeeCodeStartingWithOrderByEmployeeCodeDesc(String prefix);


    Optional<Object> findByUsername(String username);

    Optional<User> findByEmployeeCode(String employeeCode);

    @Query("""
    SELECT u FROM User u
    JOIN FETCH u.organization
    JOIN FETCH u.role
    LEFT JOIN FETCH u.createdBy
    WHERE u.email = :email
""")
    Optional<User> findByEmailWithAllRelations(@Param("email") String email);



}

