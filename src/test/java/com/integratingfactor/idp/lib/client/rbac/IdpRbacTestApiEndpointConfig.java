package com.integratingfactor.idp.lib.client.rbac;

import java.util.logging.Logger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebMvc
@EnableAspectJAutoProxy
public class IdpRbacTestApiEndpointConfig {
    private static Logger LOG = Logger.getLogger(IdpRbacTestApiEndpointConfig.class.getName());

    @Bean
    public IdpRbacTestApiEndpoint idpRbacTestApi() {
        LOG.info("Creating instance of IdpRbacTestApiEndpoint");
        return new IdpRbacTestApiEndpoint();
    }
}
