package com.murat.delivery.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.locationtech.jts.geom.Point;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "couriers")
public class Courier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;

    @Enumerated(EnumType.STRING)
    private CourierStatus status;

    @Column(columnDefinition = "geometry(Point,4326)")
    private Point location;

    @UpdateTimestamp
    private LocalDateTime lastUpdated;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
