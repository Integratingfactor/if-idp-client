package com.integratingfactor.idp.lib.client.filter;

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
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.integratingfactor.idp.lib.client.config.IdpClientSecurityConfig;

@ContextConfiguration(classes = { IdpClientSecurityConfig.class, IdpTestApiEndpointConfig.class })
@WebAppConfiguration
public class IdpApiRbacFilterTest extends AbstractTestNGSpringContextTests {

    public static final String VALID_ACCESS_TOKEN = "31f48969-3fe4-4219-92a2-395aee9e3bb9";

    @Autowired
    IdpTestApiEndpoint endpoint;

    @Autowired
    private IdpApiAuthFilter apiAuthFilter;

    @Autowired
    private FilterChainProxy springSecurityFilterChain;

    private MockMvc mockMvc;

    @BeforeMethod
    public void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(endpoint).addFilters(apiAuthFilter, springSecurityFilterChain)
                .build();
    }

    @Test
    public void testGetNoAccessToken() throws Exception {
        MvcResult response = this.mockMvc.perform(MockMvcRequestBuilders.get(IdpTestApiEndpoint.API_ENDPOINT))
                .andExpect(MockMvcResultMatchers.status().is(401))
                .andReturn();

        System.out.println(("Response Status: " + response.getResponse().getStatus()));
        System.out.println("Request response: " + response.getResponse().getContentAsString());
    }

    @Test
    public void testPostNoAccessToken() throws Exception {
        MvcResult response = this.mockMvc.perform(MockMvcRequestBuilders.post(IdpTestApiEndpoint.API_ENDPOINT))
                .andExpect(MockMvcResultMatchers.status().is(401)).andReturn();

        System.out.println(("Response Status: " + response.getResponse().getStatus()));
        System.out.println("Request response: " + response.getResponse().getContentAsString());
    }

    @Test
    public void testPutNoAccessToken() throws Exception {
        MvcResult response = this.mockMvc.perform(MockMvcRequestBuilders.put(IdpTestApiEndpoint.API_ENDPOINT))
                .andExpect(MockMvcResultMatchers.status().is(401)).andReturn();

        System.out.println(("Response Status: " + response.getResponse().getStatus()));
        System.out.println("Request response: " + response.getResponse().getContentAsString());
    }

    @Test
    public void testGetInvalidAccessToken() throws Exception {
        MvcResult response = this.mockMvc.perform(MockMvcRequestBuilders.get(IdpTestApiEndpoint.API_ENDPOINT)
                .header("Authorization", "Bearer some.invalid.token")).andExpect(MockMvcResultMatchers.status().is(401))
                .andReturn();

        System.out.println(("Response Status: " + response.getResponse().getStatus()));
        System.out.println("Request response: " + response.getResponse().getContentAsString());
    }

    @Test
    public void testGetValidAccessToken() throws Exception {
        MvcResult response = this.mockMvc.perform(MockMvcRequestBuilders.get(IdpTestApiEndpoint.API_ENDPOINT)
                .header("Authorization", "Bearer " + VALID_ACCESS_TOKEN))
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andReturn();

        System.out.println(("Response Status: " + response.getResponse().getStatus()));
        System.out.println("Request response: " + response.getResponse().getContentAsString());
    }

    @Test
    public void testPostValidAccessToken() throws Exception {
        MvcResult response = this.mockMvc.perform(MockMvcRequestBuilders.post(IdpTestApiEndpoint.API_ENDPOINT)
                .header("Authorization", "Bearer " + VALID_ACCESS_TOKEN))
                .andExpect(MockMvcResultMatchers.status().is(200)).andReturn();

        System.out.println(("Response Status: " + response.getResponse().getStatus()));
        System.out.println("Request response: " + response.getResponse().getContentAsString());
    }

    @Test
    public void testPutValidAccessToken() throws Exception {
        MvcResult response = this.mockMvc.perform(MockMvcRequestBuilders.put(IdpTestApiEndpoint.API_ENDPOINT)
                .header("Authorization", "Bearer " + VALID_ACCESS_TOKEN))
                .andExpect(MockMvcResultMatchers.status().is(200)).andReturn();

        System.out.println(("Response Status: " + response.getResponse().getStatus()));
        System.out.println("Request response: " + response.getResponse().getContentAsString());
    }

    @Test
    public void testDeleteValidAccessToken() throws Exception {
        MvcResult response = this.mockMvc.perform(MockMvcRequestBuilders.delete(IdpTestApiEndpoint.API_ENDPOINT)
                .header("Authorization", "Bearer " + VALID_ACCESS_TOKEN))
                .andExpect(MockMvcResultMatchers.status().is(200)).andReturn();

        System.out.println(("Response Status: " + response.getResponse().getStatus()));
        System.out.println("Request response: " + response.getResponse().getContentAsString());
    }
}
