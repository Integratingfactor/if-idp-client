package com.integratingfactor.idp.lib.client.util;

import java.util.logging.Logger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import com.integratingfactor.idp.lib.client.config.IdpClientAuthProperties;

@Configuration
@PropertySource("classpath:idp_client.properties")
public class IdpOauthClientTestConfig {
    private static Logger LOG = Logger.getLogger(IdpOauthClientTestConfig.class.getName());

    @Bean
    public IdpOauthClient IdpOauthClient() {
        LOG.info("Creating instance of idpOauthClient");
        return new IdpOauthClient();
    }

    @Bean
    public IdpClientAuthProperties idpClientAuthProperties() {
        LOG.info("Creating instance of IdpClientAuthProperties");
        return new IdpClientAuthProperties();
    }
}
