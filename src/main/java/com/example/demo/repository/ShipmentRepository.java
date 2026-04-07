package com.example.demo.repository;

import com.example.demo.entity.Customer;
import com.example.demo.entity.Shipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, Integer> {

    List<Shipment> findByOrder_Customer(Customer customer);

    List<Shipment> findByOrder_Customer_Id(Integer id);

}
