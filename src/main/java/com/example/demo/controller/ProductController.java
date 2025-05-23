package com.example.demo.controller;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.example.demo.entity.Product;
import com.example.demo.service.ProduceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
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

//    @PostMapping
//    public Product save(@RequestBody Product product) {
//        return produceService.save(product);
//    }
//
//    @PutMapping
//    public Product update(@RequestBody Product product) {
//        return produceService.update(product);
//    }
//
//    @DeleteMapping("/{id}")
//    public void deleteById(@PathVariable Integer id) {
//        produceService.deleteById(id);
//    }
}
