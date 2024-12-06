package com.example.demo.controller;

import com.example.demo.entity.Customers;
import com.example.demo.service.CustomersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping(path = "/customers")
public class CustomersController {
    private final CustomersService customersService;

    @Autowired
    public CustomersController(CustomersService customersService) {
        this.customersService = customersService;
    }

    @GetMapping()
    public List<Customers> findAll() {
        return customersService.findAll();
    }

    @PostMapping()
    public Customers saveCustomer(@RequestBody Customers customers) {
        return customersService.save(customers);
    }

    @PutMapping()
    public Customers updateCustomer(@RequestBody Customers customers) {
        return  customersService.update(customers);
    }

    @DeleteMapping("/{id}")
    public void deleteCustomer(@PathVariable Integer id) {
        customersService.deleteCustomerById(id);
    }
}