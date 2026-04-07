package com.example.demo.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class SKUGenerator {

    private  final String PRODUCT_PREFIX = "PROD-";
    private  final String CATEGORY_PREFIX = "CAT-";
    private  final String COLOR_PREFIX = "CLR-";
    private  final String SIZE_PREFIX = "SZ-";

    public  String generateSKU(String model, String productCategory, String color, String size, String brand) {
        StringBuilder sku = new StringBuilder();
        // Add category prefix and a unique identifier
        sku.append(generateCategoryCode(productCategory)).append("-");

        // Add color prefix and code
        sku.append(generateColorCode(color)).append("-");

        // Add size prefix and code
        sku.append(generateSizeCode(size)).append("-");

        //Brand
        sku.append(generateBrand(brand)).append("-");

        //Model
        if (model != null) {
            sku.append(model).append("-");
        }

        // Add a timestamp or random component for uniqueness
        sku.append(generateTimestamp()).append("-");

        return sku.toString().toUpperCase();
    }

    private  String generateCategoryCode(String category) {
        // In a real application, this would likely be a lookup from a category map
        return switch (category.toLowerCase()) {
            case "electronics" -> "EL";
            case "clothing" -> "CL";
            case "books" -> "BK";
            default -> "OTH";
        };
    }

    private String generateBrand(String brand) {
        return switch (brand.toLowerCase()) {
            case "tao bao" -> "TAO";
            default -> "OTH";
        };
    }

    private  String generateColorCode(String color) {
        // In a real application, this would likely be a lookup from a color map
        return switch (color.toLowerCase()) {
            case "red" -> "RD";
            case "blue" -> "BL";
            case "green" -> "GN";
            default -> "XX";
        };
    }

    private  String generateSizeCode(String size) {
        // In a real application, this would likely be a lookup from a size map
        return switch (size.toLowerCase()) {
            case "small" -> "SM";
            case "medium" -> "MD";
            case "large" -> "LG";
            default -> "XX";
        };
    }

    private  String generateTimestamp() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMMddHHmmss");
        return now.format(formatter);
    }

    private  String generateRandomComponent() {
        Random random = new Random();
        int randomNumber = random.nextInt(1000); // Generates a number between 0 and 999
        return String.format("%03d", randomNumber); // ensures leading zeros if less than 3 digits
    }
}
