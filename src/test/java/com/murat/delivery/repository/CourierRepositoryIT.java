package com.murat.delivery.repository;

import com.murat.delivery.entity.Courier;
import com.murat.delivery.entity.CourierStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CourierRepositoryIT {

    // Use PostGIS image compatible with our production setup
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgis = new PostgreSQLContainer<>(
            DockerImageName.parse("postgis/postgis:16-3.4-alpine")
                    .asCompatibleSubstituteFor("postgres"));

    @Autowired
    private CourierRepository courierRepository;

    private GeometryFactory geometryFactory = new GeometryFactory();

    @BeforeEach
    void setUp() {
        courierRepository.deleteAll();
    }

    @Test
    void findAvailableCouriersWithinRadius_ShouldReturnOnlyCouriersInsideRadius() {
        // Center point (Restaurant)
        Point center = geometryFactory.createPoint(new Coordinate(29.0, 40.0));
        center.setSRID(4326);

        // Courier 1: Inside (approx 4.9km)
        Courier insideCourier = new Courier();
        insideCourier.setFullName("Inside Courier");
        insideCourier.setStatus(CourierStatus.AVAILABLE);
        Point p1 = geometryFactory.createPoint(new Coordinate(29.0, 40.044));
        p1.setSRID(4326);
        insideCourier.setLocation(p1);
        insideCourier.setCreatedAt(LocalDateTime.now());
        insideCourier.setLastUpdated(LocalDateTime.now());
        courierRepository.save(insideCourier);

        // Courier 2: Outside (approx 5.1km)
        Courier outsideCourier = new Courier();
        outsideCourier.setFullName("Outside Courier");
        outsideCourier.setStatus(CourierStatus.AVAILABLE);
        Point p2 = geometryFactory.createPoint(new Coordinate(29.0, 40.046));
        p2.setSRID(4326);
        outsideCourier.setLocation(p2);
        outsideCourier.setCreatedAt(LocalDateTime.now());
        outsideCourier.setLastUpdated(LocalDateTime.now());
        courierRepository.save(outsideCourier);

        // Courier 3: Busy (Inside but BUSY)
        Courier busyCourier = new Courier();
        busyCourier.setFullName("Busy Courier");
        busyCourier.setStatus(CourierStatus.BUSY);
        busyCourier.setLocation(p1); // Same location as insideCourier
        busyCourier.setCreatedAt(LocalDateTime.now());
        busyCourier.setLastUpdated(LocalDateTime.now());
        courierRepository.save(busyCourier);

        // Search radius 5000 meters (5km)
        List<Courier> result = courierRepository.findAvailableCouriersWithinRadius(center, 5000.0);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFullName()).isEqualTo("Inside Courier");
    }

    @Test
    void findAvailableCouriersWithinRadius_ShouldExcludeStaleCouriers() {
        Point center = geometryFactory.createPoint(new Coordinate(29.0, 40.0));
        center.setSRID(4326);

        // Stale Courier: Inside radius but last updated 10 mins ago
        Courier staleCourier = new Courier();
        staleCourier.setFullName("Stale Courier");
        staleCourier.setStatus(CourierStatus.AVAILABLE);
        Point p1 = geometryFactory.createPoint(new Coordinate(29.0, 40.044));
        p1.setSRID(4326);
        staleCourier.setLocation(p1);
        staleCourier.setCreatedAt(LocalDateTime.now());
        staleCourier.setLastUpdated(LocalDateTime.now().minusMinutes(10));
        courierRepository.save(staleCourier);

        // Fresh Courier: Inside radius and updated recently
        Courier freshCourier = new Courier();
        freshCourier.setFullName("Fresh Courier");
        freshCourier.setStatus(CourierStatus.AVAILABLE);
        freshCourier.setLocation(p1);
        freshCourier.setCreatedAt(LocalDateTime.now());
        freshCourier.setLastUpdated(LocalDateTime.now());
        courierRepository.save(freshCourier);

        List<Courier> result = courierRepository.findAvailableCouriersWithinRadius(center, 5000.0);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFullName()).isEqualTo("Fresh Courier");
    }
}
