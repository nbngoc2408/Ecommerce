package com.example.demo.security;

import com.example.demo.entity.Customer;
import com.example.demo.service.CustomersService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authorization.AuthorizationEventPublisher;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.authorization.SpringAuthorizationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.messaging.access.intercept.AuthorizationChannelInterceptor;
import org.springframework.security.messaging.access.intercept.MessageMatcherDelegatingAuthorizationManager;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.Map;
import java.util.Objects;

@Configuration
@EnableWebSocket
@EnableWebSocketMessageBroker
public class WebSocketConfiguration implements WebSocketMessageBrokerConfigurer {

    private final ApplicationContext context;

    private final JWTRequestFilter jwtRequestFilter;

    private final CustomersService customersService;

    private static final AntPathMatcher MATHCHER = new AntPathMatcher();

    public WebSocketConfiguration(ApplicationContext context, JWTRequestFilter jwtRequestFilter, CustomersService customersService) {
        this.context = context;
        this.jwtRequestFilter = jwtRequestFilter;
        this.customersService = customersService;
    }


    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/websocket").setAllowedOriginPatterns("**").withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic");
        registry.setApplicationDestinationPrefixes("/app");
    }

    private AuthorizationManager<Message<?>> makeAuthorizationManager() {
        MessageMatcherDelegatingAuthorizationManager.Builder messages = new MessageMatcherDelegatingAuthorizationManager.Builder();
        messages
                .simpDestMatchers("/topic/user/**").authenticated()
                .anyMessage().permitAll();
        return messages.build();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        AuthorizationManager<Message<?>> authorizer = makeAuthorizationManager();
        AuthorizationChannelInterceptor authInterceptor = new AuthorizationChannelInterceptor(authorizer);
        AuthorizationEventPublisher publisher = new SpringAuthorizationEventPublisher(context);
        authInterceptor.setAuthorizationEventPublisher(publisher);
        registration.interceptors(jwtRequestFilter, authInterceptor, new RejectClientMessagesOnChannesChannelInterceptor(), new DestinationLevelAuthorizationChannelInterceptor());
    }

    private class RejectClientMessagesOnChannesChannelInterceptor implements ChannelInterceptor {

        private String[] paths = new String[] {
                "/topic/user/*/shipment",
        };

        @Override
        public Message<?> preSend(Message<?> message, MessageChannel channel) {
            if (Objects.equals(message.getHeaders().get("simpMessageType"), SimpMessageType.MESSAGE)) {
                String destination = (String) message.getHeaders().get(("simpDestination"));
                if (destination != null) {
                    for (String path : paths) {
                        if (MATHCHER.match(path, destination)) {
                            message = null;
                        }
                    }
                }
            }

            return message;
        }
    }

    private class DestinationLevelAuthorizationChannelInterceptor implements ChannelInterceptor {

        @Override
        public Message<?> preSend(Message<?> message, MessageChannel channel) {
            if (Objects.equals(message.getHeaders().get("simpMessageType"), SimpMessageType.SUBSCRIBE)) {
                String destination = (String) message.getHeaders().get(("simpDestination"));
                if (destination != null) {
                    Map<String, String> params = MATHCHER.extractUriTemplateVariables("/topic/user/{userId}/**", destination);
                    try {
                        Integer userId = Integer.valueOf(params.get("userId"));
                        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                        if (authentication != null) {
                            Customer customers = (Customer) authentication.getPrincipal();
                            if (!customersService.userHasPermissionToUser(customers, userId)) {
                                message = null;
                            }
                        } else {
                            message = null;
                        }
                    } catch (NumberFormatException ex) {
                        message = null;
                    }

                }
            }

            return message;
        }
    }
}
