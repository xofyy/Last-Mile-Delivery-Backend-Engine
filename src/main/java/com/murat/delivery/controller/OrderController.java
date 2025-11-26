package com.murat.delivery.controller;

import com.murat.delivery.dto.OrderRequest;
import com.murat.delivery.dto.OrderResponse;
import com.murat.delivery.entity.Order;
import com.murat.delivery.entity.Restaurant;
import com.murat.delivery.entity.User;
import com.murat.delivery.mapper.OrderMapper;
import com.murat.delivery.repository.OrderRepository;
import com.murat.delivery.repository.RestaurantRepository;
import com.murat.delivery.repository.UserRepository;
import com.murat.delivery.service.OrderAssignmentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderRepository orderRepository;
    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;
    private final OrderAssignmentService orderAssignmentService;
    private final OrderMapper orderMapper;

    public OrderController(OrderRepository orderRepository,
            RestaurantRepository restaurantRepository,
            UserRepository userRepository,
            OrderAssignmentService orderAssignmentService,
            OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.restaurantRepository = restaurantRepository;
        this.userRepository = userRepository;
        this.orderAssignmentService = orderAssignmentService;
        this.orderMapper = orderMapper;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody OrderRequest orderRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Restaurant restaurant = restaurantRepository.findById(orderRequest.getRestaurantId())
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));

        Order order = orderMapper.toEntity(orderRequest);
        order.setUser(user);
        order.setRestaurant(restaurant);

        order = orderRepository.save(order);

        // Smart Assignment
        try {
            orderAssignmentService.assignCourierToOrder(order);
        } catch (Exception e) {
            // Log error but don't fail order creation, maybe assign later via scheduled
            // task
            System.err.println("Failed to assign courier: " + e.getMessage());
        }

        return ResponseEntity.ok(orderMapper.toResponse(order));
    }
}
