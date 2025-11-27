package com.murat.delivery.service;

import com.murat.delivery.entity.Courier;
import com.murat.delivery.entity.CourierStatus;
import com.murat.delivery.entity.Order;
import com.murat.delivery.entity.Restaurant;
import com.murat.delivery.repository.CourierRepository;
import com.murat.delivery.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderAssignmentServiceTest {

    @Mock
    private CourierRepository courierRepository;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderAssignmentService orderAssignmentService;

    private Order order;
    private Restaurant restaurant;
    private Courier courier1;
    private GeometryFactory geometryFactory;

    @BeforeEach
    void setUp() {
        geometryFactory = new GeometryFactory();

        restaurant = new Restaurant();
        restaurant.setId(1L);
        restaurant.setLocation(geometryFactory.createPoint(new Coordinate(29.0, 40.0)));

        order = new Order();
        order.setId(1L);
        order.setRestaurant(restaurant);

        courier1 = new Courier();
        courier1.setId(1L);
        courier1.setStatus(CourierStatus.AVAILABLE);
        courier1.setLocation(geometryFactory.createPoint(new Coordinate(29.001, 40.001)));
    }

    @Test
    void assignCourierToOrder_ShouldAssignNearestCourier() {
        // Mock the repository to return a list containing our courier
        when(courierRepository.findAvailableCouriersWithinRadius(any(Point.class), anyDouble()))
                .thenReturn(Arrays.asList(courier1));

        orderAssignmentService.assignCourierToOrder(order);

        verify(courierRepository).save(courier1);
        verify(orderRepository).save(order);
        assertEquals(courier1, order.getCourier());
        assertEquals(CourierStatus.BUSY, courier1.getStatus());
    }

    @Test
    void assignCourierToOrder_ShouldThrowException_WhenNoCourierAvailable() {
        when(courierRepository.findAvailableCouriersWithinRadius(any(Point.class), anyDouble()))
                .thenReturn(Collections.emptyList());

        assertThrows(RuntimeException.class, () -> orderAssignmentService.assignCourierToOrder(order));
    }
}
