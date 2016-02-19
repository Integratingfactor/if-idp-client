package com.integratingfactor.idp.lib.client.filter;

import java.util.logging.Logger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebMvc
public class IdpTestApiEndpointConfig {
    private static Logger LOG = Logger.getLogger(IdpTestApiEndpointConfig.class.getName());

    @Bean
    public IdpTestApiEndpoint apiEndpoint() {
        LOG.info("Creating instance of IdpTestApiEndpoint");
        return new IdpTestApiEndpoint();
    }
}
