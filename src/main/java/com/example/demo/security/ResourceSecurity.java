package com.example.demo.security;

import com.example.demo.entity.Customer;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component("resourceSecurity")
public class ResourceSecurity {

    public boolean isOwner(Integer customerId, Authentication authentication) {
        if (authentication == null || customerId == null) {
            return false;
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof Customer customer) {
            return Objects.equals(customer.getId(), customerId);
        }
        return false;
    }
}
