package com.example.demo.controller;

import com.example.demo.entity.Customer;
import com.example.demo.entity.Shipment;
import com.example.demo.repository.ShipmentRepository;
import com.example.demo.request.DataChange;
import com.example.demo.service.CustomersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

//@CrossOrigin(origins = "*")
@RestController
@RequestMapping(path = "/customers")
public class CustomersController {

    private final ShipmentRepository shipmentRepository;

    private final CustomersService customersService;

    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public CustomersController(ShipmentRepository shipmentRepository, CustomersService customersService, SimpMessagingTemplate messagingTemplate) {
        this.shipmentRepository = shipmentRepository;
        this.customersService = customersService;
        this.messagingTemplate = messagingTemplate;
    }

    @GetMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public List<Customer> findAll() {
        return customersService.findAll();
    }

    @PostMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public Customer saveCustomer(@RequestBody Customer customers) {
        return customersService.save(customers);
    }

    @PutMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public Customer updateCustomer(@RequestBody Customer customers) {
        return  customersService.update(customers);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @resourceSecurity.isOwner(#id, authentication)")
    public void deleteCustomer(@PathVariable Integer id) {
        customersService.deleteCustomerById(id);
    }

    @GetMapping("/{customerId}/shipment")
    @PreAuthorize("hasRole('ADMIN') or @resourceSecurity.isOwner(#customerId, authentication)")
    public ResponseEntity<List<Shipment>> getShipment(@PathVariable Integer customerId) {
        return ResponseEntity.ok(shipmentRepository.findByOrder_Customer_Id(customerId));
    }

    @PutMapping("/{userId}/shipment")
    @PreAuthorize("hasRole('ADMIN') or @resourceSecurity.isOwner(#userId, authentication)")
    public ResponseEntity<Shipment> addNewShipment(@PathVariable Integer userId, @RequestBody Shipment shipment) {
        shipment.setId(null);
        Customer refCus = new Customer(); //case admin want to add new shipment for other customers
        refCus.setId(userId);
        com.example.demo.entity.Order order = new com.example.demo.entity.Order();
        order.setCustomer(refCus);
        shipment.setOrder(order);
        Shipment saveShipment = shipmentRepository.save(shipment);
        messagingTemplate.convertAndSend("/topic/user/" + userId + "/shipment",
                new DataChange<>(DataChange.ChangeType.INSERT, saveShipment));
        return ResponseEntity.ok(saveShipment);
    }

    @PatchMapping("/{userId}/shipment/{shipmentId}")
    @PreAuthorize("hasRole('ADMIN') or @resourceSecurity.isOwner(#userId, authentication)")
    public ResponseEntity<Shipment> updateShipment(@PathVariable Integer shipmentId, @PathVariable Integer userId, @RequestBody Shipment shipment) {
        if (Objects.equals(shipment.getId(), shipmentId)) {
            Optional<Shipment> opOriginalShipment = shipmentRepository.findById(shipmentId);
            if (opOriginalShipment.isPresent() && opOriginalShipment.get().getOrder() != null && opOriginalShipment.get().getOrder().getCustomer() != null && Objects.equals(opOriginalShipment.get().getOrder().getCustomer().getId(), userId)) {
                shipment.setOrder(opOriginalShipment.get().getOrder());
                Shipment saveShipment = shipmentRepository.save(shipment);
                messagingTemplate.convertAndSend("/topic/user/" + userId + "/shipment",
                        new DataChange<>(DataChange.ChangeType.UPDATE, saveShipment));
                return ResponseEntity.ok(saveShipment);
            }
        }

        return ResponseEntity.badRequest().build();
    }
}
