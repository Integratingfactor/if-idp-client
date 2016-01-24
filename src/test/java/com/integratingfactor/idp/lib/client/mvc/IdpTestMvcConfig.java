package com.integratingfactor.idp.lib.client.mvc;

import java.util.logging.Logger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebMvc
public class IdpTestMvcConfig {
    private static Logger LOG = Logger.getLogger(IdpTestMvcConfig.class.getName());

    @Bean
    public IdpTestMvcHandler idpOpenIdConnectMvcHandler() {
        LOG.info("Creating instance of IdpOpenIdConnectMvcHandler");
        return new IdpTestMvcHandler();
    }
}
