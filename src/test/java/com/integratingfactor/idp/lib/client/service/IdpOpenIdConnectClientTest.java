package com.integratingfactor.idp.lib.client.service;

import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
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

import com.integratingfactor.idp.lib.client.config.IdpClientConfig;

@ContextConfiguration(classes = { IdpClientConfig.class })
@WebAppConfiguration
public class IdpOpenIdConnectClientTest extends AbstractTestNGSpringContextTests {

    @Autowired
    IdpOpenIdConnectClient client;

    private MockMvc mockMvc;

    @BeforeMethod
    public void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(client).build();
    }

    @Test
    public void testAuthenticateUserChecksForOriginatingParam() throws Exception {
        // send an authentication request without originating param
        MvcResult response = this.mockMvc.perform(MockMvcRequestBuilders.post(IdpOpenIdConnectClient.pathSuffix))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                // .andExpect(MockMvcResultMatchers.redirectedUrl("/oauth/confirm_access"))
                .andReturn();

        System.out.println(("Response Status: " + response.getResponse().getStatus()));
        System.out.println("Request response: " + response.getResponse().getContentAsString());
    }

    @Test
    public void testAuthenticateUserRedirectsToIdp() throws Exception {
        // send an authentication request with originating param
        MvcResult response = this.mockMvc
                .perform(MockMvcRequestBuilders.post(IdpOpenIdConnectClient.pathSuffix)
                        .param(IdpOpenIdConnectClient.originatingParam, "index"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                // .andExpect(MockMvcResultMatchers.redirectedUrl("/oauth/confirm_access"))
                .andReturn();

        System.out.println(("Response Status: " + response.getResponse().getStatus()));
        System.out.println("Redirected url: " + response.getResponse().getRedirectedUrl());
    }

    @Test
    public void testOpenIdConnectListnerReturnsToOriginator() throws Exception {
        // call the openid connect listener with a mock code grant response
        MvcResult response = this.mockMvc
                .perform(MockMvcRequestBuilders.get(IdpOpenIdConnectClient.pathSuffix)
.param("code",
 "a mock invalid code")
                        .sessionAttr(IdpOpenIdConnectClient.requestOriginator, "testPage"))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.view().name("testPage"))
                .andReturn();

        System.out.println(("Response Status: " + response.getResponse().getStatus()));
        System.out.println("Error message: " + response.getRequest().getAttribute("error_message"));
    }
}
