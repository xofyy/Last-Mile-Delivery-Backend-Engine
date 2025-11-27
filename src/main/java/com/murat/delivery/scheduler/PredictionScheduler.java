package com.murat.delivery.scheduler;

import com.murat.delivery.service.PredictionService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;
import java.time.LocalDateTime;

@Component
@Slf4j
public class PredictionScheduler {

    private final PredictionService predictionService;

    public PredictionScheduler(PredictionService predictionService) {
        this.predictionService = predictionService;
    }

    // Run every minute for demo purposes (in prod: @Scheduled(cron = "0 0 23 * *
    // ?"))
    @Scheduled(fixedRate = 60000)
    public void checkDemandPrediction() {
        LocalDateTime now = LocalDateTime.now();
        // Predict for tomorrow same time
        LocalDateTime targetTime = now.plusDays(1);

        int hour = targetTime.getHour();
        int day = targetTime.getDayOfWeek().getValue() - 1; // 0-6

        log.info("Requesting demand prediction for Hour: {}, Day: {}", hour, day);
        String prediction = predictionService.getPrediction(hour, day);
        log.info("Received Prediction: {}", prediction);

        // Logic to notify couriers would go here
    }
}
