package com.yourcompany.hrms.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "organizations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Organization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = 1L;

    @Column(nullable = false, unique = true, length = 255)
    private String name;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(length = 20)
    private String phone;

    @Column(columnDefinition = "TEXT")
    private String address;

    private String city;
    private String country;

//    @Enumerated(EnumType.STRING)
//    @Column(name = "subscription_plan", nullable = false)
//    private SubscriptionPlan subscriptionPlan;

//    @Enumerated(EnumType.STRING)
//    @Column(nullable = false)
//    private Status status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
