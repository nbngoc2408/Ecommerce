package com.example.demo.dto;

import lombok.Builder;
import lombok.Value;

/**
 * DTO for {@link com.example.demo.entity.Category}
 */
@Value
@Builder
public class CategoryDto {
    int categoryId;
    String name;
}