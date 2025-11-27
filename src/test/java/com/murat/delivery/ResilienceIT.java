package com.murat.delivery;

import com.murat.delivery.entity.Order;
import com.murat.delivery.entity.Restaurant;
import com.murat.delivery.entity.User;
import com.murat.delivery.repository.CourierRepository;
import com.murat.delivery.repository.OrderRepository;
import com.murat.delivery.repository.RestaurantRepository;
import com.murat.delivery.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ResilienceIT {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CourierRepository courierRepository;

    @MockBean
    private OrderRepository orderRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private RestaurantRepository restaurantRepository;

    @Test
    @WithMockUser(username = "test@example.com")
    public void testRateLimiter() throws Exception {
        // Mock dependencies to avoid other errors
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(new User()));
        when(restaurantRepository.findById(any())).thenReturn(Optional.of(new Restaurant()));
        when(orderRepository.save(any())).thenReturn(new Order());

        // Send 5 requests (Limit is 5 in test profile)
        for (int i = 0; i < 5; i++) {
            mockMvc.perform(post("/api/orders")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"restaurantId\": 1, \"totalAmount\": 50.0}"))
                    .andExpect(status().isOk());
        }

        // The 6th request should fail with 429 Too Many Requests
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"restaurantId\": 1, \"totalAmount\": 50.0}"))
                .andExpect(status().is4xxClientError()); // Expecting 429
    }
}
