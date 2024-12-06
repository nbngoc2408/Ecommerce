package com.example.demo.service;

import com.example.demo.EcommerceApplication;
import com.example.demo.entity.VerificationToken;
import com.example.demo.exception.CustomerException;
import com.example.demo.exception.EmailFailureException;
import com.example.demo.exception.UserNotVerifiedException;
import com.example.demo.repository.VerificationTokenRepository;
import com.example.demo.request.LoginBody;
import com.example.demo.request.RegistrationBody;
import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class CustomersServiceTests {

    @RegisterExtension
    public static GreenMailExtension greenMailExtension = new GreenMailExtension((ServerSetupTest.SMTP))
            .withConfiguration(GreenMailConfiguration.aConfig().withUser("springboot", "secret"))
            .withPerMethodLifecycle(true);

    @Autowired
    private CustomersService customersService;

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    //rollback when test phase is done.
    @Test
    @Transactional
    public void testRegisterCustomer() throws MessagingException {
        RegistrationBody body = new RegistrationBody();
        body.setEmail("userA@junittest.com");
        body.setPassword("MySecretPassword");
        body.setFirstName("FirstName");
        body.setLastName("LastName");
        Assertions.assertThrows(CustomerException.class, () -> customersService.registerCustomer(body), "User email should already in use.");
        body.setEmail("UserServiceTest$testRegister@junittest.com");
        Assertions.assertDoesNotThrow(()->customersService.registerCustomer(body), "User should register successfully.");
        Assertions.assertEquals(body.getEmail(), greenMailExtension.getReceivedMessages()[0].getRecipients(Message.RecipientType.TO)[0].toString());
    }

    @Test
    @Transactional
    public void testLoginUser() throws UserNotVerifiedException, EmailFailureException {
        LoginBody loginBody = new LoginBody();
        loginBody.setEmail("userA_not_exists@junittest.com");
        loginBody.setPassword("BadPasswordUserA");
        Assertions.assertNull(customersService.loginCustomer(loginBody), "User email should not exists.");
        loginBody.setEmail("userA@junittest.com");
        Assertions.assertNull(customersService.loginCustomer(loginBody), "User password should not incorrect.");
        loginBody.setPassword("PasswordA123");
        Assertions.assertNotNull(customersService.loginCustomer(loginBody), "User password should login successfully.");
        loginBody.setEmail("userB@junittest.com");
        loginBody.setPassword("PasswordB123");
        try {
            customersService.loginCustomer(loginBody);
            Assertions.fail("Email should not be verified.");
        } catch (UserNotVerifiedException ex) {
            Assertions.assertTrue(ex.isNewEmailSent(), "Email verification should be sent.");
            Assertions.assertEquals(1, greenMailExtension.getReceivedMessages().length);
        }
        try {
            customersService.loginCustomer(loginBody);
            Assertions.fail("Email should not be verified.");
        } catch (UserNotVerifiedException ex) {
            Assertions.assertFalse(ex.isNewEmailSent(), "Email verification should not be resent. Please check mail.");
            Assertions.assertEquals(1, greenMailExtension.getReceivedMessages().length);
        }
    }

    @Test
    @Transactional
    public void testVerifyUser() throws EmailFailureException, UserNotVerifiedException {
        Assertions.assertFalse(customersService.verifyCustomer("Bad token"), "Token is incorrect or not exist.");
        LoginBody loginBody = new LoginBody();
        loginBody.setEmail("userB@junittest.com");
        loginBody.setPassword("PasswordB123");
        try {
            customersService.loginCustomer(loginBody);
            Assertions.fail("Email should not be verified.");
        } catch (UserNotVerifiedException ex) {
            List<VerificationToken> tokenList = verificationTokenRepository.findByCustomers_EmailIgnoreCaseOrderByIdDesc(loginBody.getEmail());
            String token = tokenList.get(0).getToken();
            Assertions.assertTrue(customersService.verifyCustomer(token), "User should verify success.");
        }
    }
}
