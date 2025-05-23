package com.example.demo.controller;

import com.example.demo.entity.Customers;
import com.example.demo.entity.Shipment;
import com.example.demo.repository.ShipmentRepository;
import com.example.demo.request.DataChange;
import com.example.demo.service.CustomersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    @GetMapping("/{customerId}/shipment")
    public ResponseEntity<List<Shipment>> getShipment(@AuthenticationPrincipal Customers customers , @PathVariable Integer customerId) {
        if (customersService.verifyCustomerId(customers, customerId)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return ResponseEntity.ok(shipmentRepository.findByCustomer_Id(customerId));
    }

    @PutMapping("/{userId}/shipment")
    public ResponseEntity<Shipment> addNewShipment(@PathVariable Integer userId, @RequestBody Shipment shipment
            , @AuthenticationPrincipal Customers customers) {
        if (customersService.verifyCustomerId(customers, userId)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        shipment.setId(null);
        Customers refCus = new Customers(); //case admin want to add new shipment for other customers
        refCus.setId(userId);
        shipment.setCustomer(refCus);
        Shipment saveShipment = shipmentRepository.save(shipment);
        messagingTemplate.convertAndSend("/topic/user/" + userId + "/shipment",
                new DataChange<>(DataChange.ChangeType.INSERT, saveShipment));
        return ResponseEntity.ok(saveShipment);
    }

    @PatchMapping("/{userId}/shipment/{shipmentId}")
    public ResponseEntity<Shipment> updateShipment(@PathVariable Integer shipmentId, @PathVariable Integer userId, @RequestBody Shipment shipment
            , @AuthenticationPrincipal Customers customers) {
        if (customersService.verifyCustomerId(customers, userId)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        if (Objects.equals(shipment.getId(), shipmentId)) {
            Optional<Shipment> opOriginalShipment = shipmentRepository.findById(shipmentId);
            if (opOriginalShipment.isPresent() && Objects.equals(opOriginalShipment.get().getCustomer().getId(), userId)) {
                shipment.setCustomer(opOriginalShipment.get().getCustomer());
                Shipment saveShipment = shipmentRepository.save(shipment);
                messagingTemplate.convertAndSend("/topic/user/" + userId + "/shipment",
                        new DataChange<>(DataChange.ChangeType.UPDATE, saveShipment));
                return ResponseEntity.ok(saveShipment);
            }
        }

        return ResponseEntity.badRequest().build();
    }
}