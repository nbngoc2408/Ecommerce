package com.example.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "payment_method")
public class PaymentMethod {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "method_id", nullable = false)
    private Integer id;

    @Size(max = 100)
    @Column(name = "method_name", length = 100)
    private String methodName;

    @Size(max = 255)
    @Column(name = "description")
    private String description;

}