package com.murat.delivery.repository;

import com.murat.delivery.entity.Courier;
import com.murat.delivery.entity.CourierStatus;
import org.locationtech.jts.geom.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourierRepository extends JpaRepository<Courier, Long> {
    List<Courier> findByStatus(CourierStatus status);

    @Query(value = "SELECT * FROM couriers c " +
            "WHERE c.status = 'AVAILABLE' " +
            "AND c.last_updated > NOW() - INTERVAL '5 minutes' " +
            "AND ST_DWithin(c.location, :point, :radius) " +
            "ORDER BY ST_Distance(c.location, :point) ASC", nativeQuery = true)
    List<Courier> findAvailableCouriersWithinRadius(@Param("point") Point point, @Param("radius") double radius);
}
