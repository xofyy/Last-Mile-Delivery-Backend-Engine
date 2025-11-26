package com.murat.delivery.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderShippedEvent implements Serializable {
    private Long orderId;
    private String userEmail;
    private String message;
}
