package com.murat.delivery.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PredictionService {

    private final RestTemplate restTemplate;

    @Value("${app.ai-service-url:http://localhost:8001}")
    private String aiServiceUrl;

    public PredictionService() {
        this.restTemplate = new RestTemplate();
    }

    public String getPrediction(int hour, int day) {
        try {
            String url = String.format("%s/predict?hour=%d&day=%d", aiServiceUrl, hour, day);
            return restTemplate.getForObject(url, String.class);
        } catch (Exception e) {
            log.error("Failed to get prediction from AI service: {}", e.getMessage());
            return "{\"error\": \"AI Service Unavailable\"}";
        }
    }
}
