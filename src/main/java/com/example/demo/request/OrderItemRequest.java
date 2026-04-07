package com.example.demo.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class OrderItemRequest {
    private int orderItemId;
    private int quantity;
}
