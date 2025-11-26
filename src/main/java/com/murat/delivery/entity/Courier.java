package com.murat.delivery.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "couriers")
public class Courier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fullName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CourierStatus status;

    private Double latitude;

    private Double longitude;

    private LocalDateTime lastUpdated;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
