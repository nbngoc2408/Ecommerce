package com.example.demo.controller;

import com.example.demo.entity.Customer;
import com.example.demo.exception.CustomerException;
import com.example.demo.exception.EmailFailureException;
import com.example.demo.exception.EmailNotFoundException;
import com.example.demo.exception.UserNotVerifiedException;
import com.example.demo.request.LoginBody;
import com.example.demo.request.PasswordResetBody;
import com.example.demo.request.RegistrationBody;
import com.example.demo.response.LoginResponse;
import com.example.demo.service.CustomersService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class Authentication {

    private final CustomersService customersService;

    @Autowired
    public Authentication(CustomersService customersService) {
        this.customersService = customersService;
    }

    @PostMapping("/register")
    public ResponseEntity<Customer> registerUser(@Valid @RequestBody RegistrationBody customer) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(customersService.registerCustomer(customer));
        } catch (CustomerException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (EmailFailureException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginUser(@Valid @RequestBody LoginBody loginBody) {
        String jwt = null;
        try {
            jwt = customersService.loginCustomer(loginBody);
        } catch (UserNotVerifiedException e) {
            LoginResponse response = new LoginResponse();
            response.setSuccess(false);
            String reason = "USER_NOT_VERIFIED";
            if (e.isNewEmailSent()) {
                reason += "_EMAIL_RESENT";
            }
            response.setFailureReason(reason);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        } catch (EmailFailureException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        if (jwt == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } else {
            LoginResponse response = new LoginResponse();
            response.setJwt(jwt);
            response.setSuccess(true);
            return ResponseEntity.ok(response);
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<String> verifyEmail(@RequestParam String token) {
        if(customersService.verifyCustomer(token)) {
            return ResponseEntity.status(HttpStatus.OK).build();
        }
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    @GetMapping("/me")
    public Customer getUserLogin(@AuthenticationPrincipal Customer customers) {
//        SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return customers;
    }

    @PostMapping("/forgot")
    public ResponseEntity<Object> forgotPassword(@RequestParam String email) {
        try {
            customersService.forgotPassword(email);
            return ResponseEntity.ok().build();
        } catch (EmailFailureException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (EmailNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping("/reset")
    public ResponseEntity<Object> resetPassword(@Valid @RequestBody PasswordResetBody passwordResetBody) {
        customersService.resetPassword(passwordResetBody);
        return ResponseEntity.ok().build();
    }
}
