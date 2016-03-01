package com.integratingfactor.idp.lib.client.rbac;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.integratingfactor.idp.lib.client.config.IdpClientSecurityConfig;
import com.integratingfactor.idp.lib.client.filter.IdpApiAuthFilter;
import com.integratingfactor.idp.lib.client.util.IdpOauthClient;

@ContextConfiguration(classes = { IdpClientSecurityConfig.class, IdpRbacTestApiEndpointConfig.class })
@WebAppConfiguration
public class IdpRbacFilterTest extends AbstractTestNGSpringContextTests {

    private String USERS_ORG_ACCESS_TOKEN = null;

    @Autowired
    IdpRbacTestApiEndpoint endpoint;

    @Autowired
    IdpOauthClient oauthClient;

    @Autowired
    IdpRbacService aspect;

    @Autowired
    private IdpApiAuthFilter apiAuthFilter;

    @Autowired
    private FilterChainProxy springSecurityFilterChain;

    private MockMvc mockMvc;

    @BeforeClass
    public void init() {
        USERS_ORG_ACCESS_TOKEN = oauthClient.getAccessToken().getValue();
    }

    @BeforeMethod
    public void setup() {
        Assert.assertNotNull(aspect);
        Assert.assertNotNull(endpoint);
        this.mockMvc = MockMvcBuilders.standaloneSetup(endpoint)
                .addFilters(apiAuthFilter, springSecurityFilterChain)
                .build();
    }

    @Test
    public void testGetAuthorizedEndpoint() throws Exception {
        MvcResult response = this.mockMvc
                .perform(MockMvcRequestBuilders.get(IdpRbacTestApiEndpoint.API_ENDPOINT_AUTHORIZED)
                .header("Authorization", "Bearer " + USERS_ORG_ACCESS_TOKEN))
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andReturn();

        System.out.println(("Response Status: " + response.getResponse().getStatus()));
        System.out.println("Request response: " + response.getResponse().getContentAsString());
    }

    @Test
    public void testGetUnauthorizedEndpoint() throws Exception {
        MvcResult response = this.mockMvc
                .perform(MockMvcRequestBuilders.get(IdpRbacTestApiEndpoint.API_ENDPOINT_UNAUTHORIZED)
                .header("Authorization", "Bearer " + USERS_ORG_ACCESS_TOKEN))
                .andExpect(MockMvcResultMatchers.status().is(403)).andReturn();

        System.out.println(("Response Status: " + response.getResponse().getStatus()));
        System.out.println("Request response: " + response.getResponse().getContentAsString());
    }
}
