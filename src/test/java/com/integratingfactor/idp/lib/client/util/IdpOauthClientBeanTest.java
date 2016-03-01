package com.integratingfactor.idp.lib.client.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.integratingfactor.idp.lib.client.model.IdpTokenValidation;

@ContextConfiguration(classes = { IdpOauthClientTestConfig.class })
public class IdpOauthClientBeanTest extends AbstractTestNGSpringContextTests {

    @Autowired
    IdpOauthClient client;

    @Test
    public void testBeanCreation() {
        Assert.assertNotNull(client);
    }

    @Test
    public void testResourceOwnerPasswordGrantSuccess() throws Exception {
        OAuth2AccessToken token = client.getAccessToken();
        Assert.assertNotNull(token);
        System.out.println("Token: " + token);
        IdpTokenValidation validation = client.validateToken(token.getValue());
        Assert.assertNotNull(validation);
        System.out.println("Token validation: " + new ObjectMapper().writeValueAsString(validation));
        Assert.assertNotNull(validation.getUserId());
        Assert.assertFalse(validation.getAuthorities().isEmpty());
    }

}
