package com.example.demo.service;

import com.example.demo.entity.Customers;
import com.example.demo.entity.VerificationToken;
import com.example.demo.exception.CustomerException;
import com.example.demo.exception.EmailFailureException;
import com.example.demo.exception.UserNotVerifiedException;
import com.example.demo.repository.CustomersRepository;
import com.example.demo.repository.VerificationTokenRepository;
import com.example.demo.request.LoginBody;
import com.example.demo.request.RegistrationBody;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

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

    public Customers registerCustomer(RegistrationBody register) throws CustomerException, EmailFailureException {
        if (customersRepository.findByEmail(register.getEmail()).isPresent()) {
            throw new CustomerException();
        } else {
            Customers customers = new Customers();
            customers.setFirstName(register.getFirstName());
            customers.setLastName(register.getLastName());
            customers.setEmail(register.getEmail());
            customers.setPassword(encryptService.encryptPassword(register.getPassword()));
            VerificationToken verificationToken = createVerificationToken(customers);
            emailService.sendVerificationEmail(verificationToken);
            return customersRepository.save(customers);
        }
    }

    private VerificationToken createVerificationToken(Customers customers) {
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(jwtService.generateVerificationJWT(customers));
        verificationToken.setCreatedTimestamp(new Timestamp(System.currentTimeMillis()));
        verificationToken.setCustomers(customers);
        customers.getVerificationTokens().add(verificationToken);
        return verificationToken;
    }

    public List<Customers> findAll() {
        return customersRepository.findAll();
    }

    public Customers save(Customers customer) {
        return customersRepository.save(customer);
    }

    public Customers update(Customers customers) {
        return customersRepository.save(customers);
    }

    public void deleteCustomerById(Integer id) {
        Optional<Customers> customers = customersRepository.findById(id);
        customers.ifPresent(customersRepository::delete);
    }

    @Transactional
    public String loginCustomer(LoginBody loginBody) throws UserNotVerifiedException, EmailFailureException {
        Optional<Customers> customerOpt = customersRepository.findByEmailIgnoreCase(loginBody.getEmail());
        if (customerOpt.isPresent()) {
            Customers customers = customerOpt.get();
            if (encryptService.verifyPassword(loginBody.getPassword(), customers.getPassword())) {
                if (customers.getEmailVerified()) {
                    return jwtService.generateJWT(customers);
                } else {
                    List<VerificationToken> verificationTokenList = customers.getVerificationTokens();
                    boolean resend = verificationTokenList.isEmpty() || verificationTokenList.get(0).getCreatedTimestamp().before(new Timestamp(System.currentTimeMillis() - 60 * 60 * 1000));
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
            Customers customers = verificationToken.getCustomers();
            if (!customers.getEmailVerified()) {
                customers.setEmailVerified(true);
                customersRepository.save(customers);
                verificationTokenRepository.deleteByCustomers(customers);
                return true;
            }
        }
        return false;
    }
}
