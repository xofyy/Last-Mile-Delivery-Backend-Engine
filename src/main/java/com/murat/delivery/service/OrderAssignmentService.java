package com.murat.delivery.service;

import com.murat.delivery.entity.Courier;
import com.murat.delivery.entity.CourierStatus;
import com.murat.delivery.entity.Order;
import com.murat.delivery.entity.Restaurant;
import com.murat.delivery.repository.CourierRepository;
import com.murat.delivery.repository.OrderRepository;
import com.murat.delivery.util.GeoUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderAssignmentService {

    private final CourierRepository courierRepository;
    private final OrderRepository orderRepository;
    private final GeoUtils geoUtils;

    public OrderAssignmentService(CourierRepository courierRepository,
            OrderRepository orderRepository,
            GeoUtils geoUtils) {
        this.courierRepository = courierRepository;
        this.orderRepository = orderRepository;
        this.geoUtils = geoUtils;
    }

    @Transactional
    public void assignCourierToOrder(Order order) {
        Restaurant restaurant = order.getRestaurant();
        List<Courier> availableCouriers = courierRepository.findByStatus(CourierStatus.AVAILABLE);

        if (availableCouriers.isEmpty()) {
            throw new RuntimeException("No available couriers found");
        }

        Courier nearestCourier = null;
        double minDistance = Double.MAX_VALUE;

        for (Courier courier : availableCouriers) {
            double distance = geoUtils.calculateDistance(
                    restaurant.getLatitude(), restaurant.getLongitude(),
                    courier.getLatitude(), courier.getLongitude());

            if (distance < minDistance) {
                minDistance = distance;
                nearestCourier = courier;
            }
        }

        if (nearestCourier != null) {
            nearestCourier.setStatus(CourierStatus.BUSY);
            courierRepository.save(nearestCourier);

            order.setCourier(nearestCourier);
            orderRepository.save(order);
        }
    }
}
