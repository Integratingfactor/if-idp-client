package com.integratingfactor.idp.lib.client.kms;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import com.integratingfactor.idp.lib.client.config.IdpClientAuthProperties;

@Configuration
@PropertySource("classpath:idp_client.properties")
public class IdpKmsClientTestConfig {

    @Bean
    public IdpKmsClientImpl idpKmsClientImpl() {
        return new IdpKmsClientImpl();
    }

    @Bean
    public IdpClientAuthProperties idpClientAuthProperties() {
        return new IdpClientAuthProperties();
    }
}
