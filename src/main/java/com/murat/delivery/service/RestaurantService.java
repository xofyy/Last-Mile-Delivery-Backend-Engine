package com.murat.delivery.service;

import com.murat.delivery.entity.Restaurant;
import com.murat.delivery.repository.RestaurantRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;

    public RestaurantService(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    @Cacheable(value = "restaurants", key = "#id")
    public Restaurant getRestaurantById(Long id) {
        return restaurantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));
    }

    @Cacheable(value = "allRestaurants")
    public List<Restaurant> getAllRestaurants() {
        return restaurantRepository.findAll();
    }

    @CacheEvict(value = { "restaurants", "allRestaurants" }, allEntries = true)
    public Restaurant createRestaurant(Restaurant restaurant) {
        return restaurantRepository.save(restaurant);
    }
}
