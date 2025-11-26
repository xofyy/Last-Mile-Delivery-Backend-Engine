package com.murat.delivery.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderRequest {
    @NotNull
    private Long restaurantId;

    @NotNull
    @Positive
    private BigDecimal totalAmount;
}
