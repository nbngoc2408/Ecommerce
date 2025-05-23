package com.example.demo.repository;

import com.example.demo.entity.Customers;
import com.example.demo.entity.Order;
import com.example.demo.entity.Shipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, Integer> {

    List<Shipment> findByCustomer(Customers customer);

    List<Shipment> findByCustomer_Id(Integer id);

}
