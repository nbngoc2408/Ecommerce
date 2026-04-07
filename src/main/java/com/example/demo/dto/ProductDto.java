package com.example.demo.dto;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

/**
 * DTO for {@link com.example.demo.entity.Product}
 */
@Value
@Builder
public class ProductDto {
    Integer id;
    String sku;
    String description;
    BigDecimal price;
    Integer stock;
    int categoryId;
}