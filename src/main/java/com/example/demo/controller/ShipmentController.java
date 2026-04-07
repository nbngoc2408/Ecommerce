package com.example.demo.controller;

import com.example.demo.entity.Customer;
import com.example.demo.entity.Shipment;
import com.example.demo.service.ShipmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/shipment")
@CrossOrigin("*")
public class ShipmentController {
    @Autowired
    private ShipmentService shipmentService;

    @GetMapping
    public List<Shipment> getShipments(@AuthenticationPrincipal Customer customers) {
        return shipmentService.findShipmentsByCustomer(customers);
    }
}
