package com.murat.delivery.service;

import com.murat.delivery.entity.Courier;
import com.murat.delivery.entity.CourierStatus;
import com.murat.delivery.entity.Order;
import com.murat.delivery.entity.Restaurant;
import com.murat.delivery.repository.CourierRepository;
import com.murat.delivery.repository.OrderRepository;
import com.murat.delivery.util.GeoUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderAssignmentServiceTest {

    @Mock
    private CourierRepository courierRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private GeoUtils geoUtils;

    @InjectMocks
    private OrderAssignmentService orderAssignmentService;

    private Order order;
    private Restaurant restaurant;
    private Courier courier1;
    private Courier courier2;

    @BeforeEach
    void setUp() {
        restaurant = new Restaurant();
        restaurant.setId(1L);
        restaurant.setLatitude(40.0);
        restaurant.setLongitude(29.0);

        order = new Order();
        order.setId(1L);
        order.setRestaurant(restaurant);

        courier1 = new Courier();
        courier1.setId(1L);
        courier1.setStatus(CourierStatus.AVAILABLE);
        courier1.setLatitude(40.1);
        courier1.setLongitude(29.1);

        courier2 = new Courier();
        courier2.setId(2L);
        courier2.setStatus(CourierStatus.AVAILABLE);
        courier2.setLatitude(40.2);
        courier2.setLongitude(29.2);
    }

    @Test
    void assignCourierToOrder_ShouldAssignNearestCourier() {
        when(courierRepository.findByStatus(CourierStatus.AVAILABLE)).thenReturn(Arrays.asList(courier1, courier2));
        when(geoUtils.calculateDistance(40.0, 29.0, 40.1, 29.1)).thenReturn(10.0);
        when(geoUtils.calculateDistance(40.0, 29.0, 40.2, 29.2)).thenReturn(20.0);

        orderAssignmentService.assignCourierToOrder(order);

        verify(courierRepository).save(courier1);
        verify(orderRepository).save(order);
        assertEquals(courier1, order.getCourier());
        assertEquals(CourierStatus.BUSY, courier1.getStatus());
    }

    @Test
    void assignCourierToOrder_ShouldThrowException_WhenNoCourierAvailable() {
        when(courierRepository.findByStatus(CourierStatus.AVAILABLE)).thenReturn(Collections.emptyList());

        assertThrows(RuntimeException.class, () -> orderAssignmentService.assignCourierToOrder(order));
    }
}
