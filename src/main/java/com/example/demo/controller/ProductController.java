package com.example.demo.controller;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.example.demo.dto.ProductDto;
import com.example.demo.entity.Product;
import com.example.demo.service.ProduceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping(path = "/product")
public class ProductController {
    private final ProduceService produceService;

    @Autowired
    public ProductController(ProduceService produceService) {
        this.produceService = produceService;
    }

    @GetMapping
    public ResponseEntity<List<Product>> findAll() {
        List<Product> listProduct = new ArrayList<>();
        try {
            listProduct = produceService.findAll();
        } catch (JWTVerificationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(listProduct);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductDto> save(@RequestBody ProductDto product) {
        ProductDto savedProduct = produceService.save(product);
        if (savedProduct != null) {
            return ResponseEntity.ok(savedProduct);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Product update(@RequestBody Product product) {
        return produceService.update(product);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteById(@PathVariable Integer id) {
        produceService.deleteById(id);
    }
}
