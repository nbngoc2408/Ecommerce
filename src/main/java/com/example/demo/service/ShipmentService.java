package com.example.demo.service;

import com.example.demo.entity.Customers;
import com.example.demo.entity.Shipment;
import com.example.demo.repository.ShipmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShipmentService {
    @Autowired
    private ShipmentRepository shipmentRepository;

    public List<Shipment> findShipmentsByCustomer(Customers customers) {
        return shipmentRepository.findByCustomer(customers);
    }
}
