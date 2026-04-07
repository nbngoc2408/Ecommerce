package com.example.demo.service;

import com.example.demo.entity.Customer;
import com.example.demo.entity.VerificationToken;
import com.example.demo.exception.CustomerException;
import com.example.demo.exception.EmailFailureException;
import com.example.demo.exception.EmailNotFoundException;
import com.example.demo.exception.UserNotVerifiedException;
import com.example.demo.repository.CustomersRepository;
import com.example.demo.repository.VerificationTokenRepository;
import com.example.demo.request.LoginBody;
import com.example.demo.request.PasswordResetBody;
import com.example.demo.request.RegistrationBody;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.time.Instant;

@Service
public class CustomersService {
    private final CustomersRepository customersRepository;
    private final EncryptService encryptService;
    private final JWTService jwtService;
    private final EmailService emailService;
    private final VerificationTokenRepository verificationTokenRepository;

    @Autowired
    public CustomersService(CustomersRepository customersRepository, EncryptService encryptService, JWTService jwtService, EmailService emailService, VerificationTokenRepository verificationTokenRepository) {
        this.customersRepository = customersRepository;
        this.encryptService = encryptService;
        this.jwtService = jwtService;
        this.emailService = emailService;
        this.verificationTokenRepository = verificationTokenRepository;
    }

    public Customer registerCustomer(RegistrationBody register) throws CustomerException, EmailFailureException {
        if (customersRepository.findByEmail(register.getEmail()).isPresent()) {
            throw new CustomerException();
        } else {
            Customer customers = new Customer();
            customers.setFirstName(register.getFirstName());
            customers.setLastName(register.getLastName());
            customers.setEmail(register.getEmail());
            customers.setPassword(encryptService.encryptPassword(register.getPassword()));
            VerificationToken verificationToken = createVerificationToken(customers);
            emailService.sendVerificationEmail(verificationToken);
            return customersRepository.save(customers);
        }
    }

    private VerificationToken createVerificationToken(Customer customers) {
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(jwtService.generateVerificationJWT(customers));
        verificationToken.setCreatedTimestamp(Instant.now());
        verificationToken.setCustomer(customers);
        customers.getVerificationTokens().add(verificationToken);
        return verificationToken;
    }

    public List<Customer> findAll() {
        return customersRepository.findAll();
    }

    public Customer save(Customer customer) {
        return customersRepository.save(customer);
    }

    public Customer update(Customer customers) {
        return customersRepository.save(customers);
    }

    public void deleteCustomerById(Integer id) {
        Optional<Customer> customers = customersRepository.findById(id);
        customers.ifPresent(customersRepository::delete);
    }

    @Transactional
    public String loginCustomer(LoginBody loginBody) throws UserNotVerifiedException, EmailFailureException {
        Optional<Customer> customerOpt = customersRepository.findByEmailIgnoreCase(loginBody.getEmail());
        if (customerOpt.isPresent()) {
            Customer customers = customerOpt.get();
            if (encryptService.verifyPassword(loginBody.getPassword(), customers.getPassword())) {
                if (customers.getEmailVerified()) {
                    return jwtService.generateJWT(customers);
                } else {
                    java.util.List<VerificationToken> verificationTokenList = new java.util.ArrayList<>(customers.getVerificationTokens());
                    boolean resend = verificationTokenList.isEmpty() || verificationTokenList.get(0).getCreatedTimestamp().isBefore(Instant.now().minusSeconds(60 * 60));
                    if (resend) {
                        VerificationToken verificationToken = createVerificationToken(customers);
                        verificationTokenRepository.save(verificationToken);
                        emailService.sendVerificationEmail(verificationToken);
                    }
                    throw new UserNotVerifiedException(resend);
                }
            }
        }
        return null;
    }

    @Transactional
    public boolean verifyCustomer(String token) {
        Optional<VerificationToken> verificationTokenOpt = verificationTokenRepository.findByToken(token);
        if (verificationTokenOpt.isPresent()) {
            VerificationToken verificationToken = verificationTokenOpt.get();
            Customer customers = verificationToken.getCustomer();
            if (!customers.getEmailVerified()) {
                customers.setEmailVerified(true);
                customersRepository.save(customers);
                verificationTokenRepository.deleteByCustomer(customers);
                return true;
            }
        }
        return false;
    }

    @Transactional
    public void forgotPassword(String email) throws EmailFailureException, EmailNotFoundException {
        Optional<Customer> optionalCustomers = customersRepository.findByEmailIgnoreCase(email);
        if (optionalCustomers.isPresent()) {
            Customer customers = optionalCustomers.get();
            String token = jwtService.generateResetPassword(customers);
            emailService.sendResetPasswordEmail(customers, token);
        } else {
            throw new EmailNotFoundException();
        }
    }

    @Transactional
    public void resetPassword (PasswordResetBody passwordResetBody) {
        String email = jwtService.getEmailResetPassword(passwordResetBody.getToken());
        Optional<Customer> optCus = customersRepository.findByEmailIgnoreCase(email);
        if (optCus.isPresent()) {
            Customer customers = optCus.get();
            String passwordEncode = encryptService.encryptPassword(passwordResetBody.getPassword());
            customers.setPassword(passwordEncode);
            customersRepository.save(customers);
        }
    }

    public  boolean userHasPermissionToUser (Customer customers, Integer id) {
        return Objects.equals(customers.getId(), id);
    }

    public boolean verifyCustomerId(Customer customers, Integer id) {
        return !customers.getId().equals(id);
    }
}
