package com.integratingfactor.idp.lib.client.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.integratingfactor.idp.lib.client.config.IdpClientConfig;
import com.integratingfactor.idp.lib.client.model.UserProfile;

@ContextConfiguration(classes = { IdpClientConfig.class })
@WebAppConfiguration
public class IdpOpenIdConnectMvcHandlerTest extends AbstractTestNGSpringContextTests {

    @Autowired
    IdpOpenIdConnectMvcHandler client;

    private MockMvc mockMvc;

    @BeforeMethod
    public void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(client).build();
    }

    @Test
    public void testAuthenticateUserChecksForOriginatingParam() throws Exception {
        // send an authentication request without originating param
        MvcResult response = this.mockMvc.perform(MockMvcRequestBuilders.post(IdpOpenIdConnectClient.pathSuffixLogin))
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
                .perform(MockMvcRequestBuilders.post(IdpOpenIdConnectClient.pathSuffixLogin)
                        .param(IdpOpenIdConnectMvcHandler.originatingParam, "index"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                // .andExpect(MockMvcResultMatchers.redirectedUrl("/oauth/confirm_access"))
                .andReturn();

        System.out.println(("Response Status: " + response.getResponse().getStatus()));
        System.out.println("Redirected url: " + response.getResponse().getRedirectedUrl());
    }

    @Test
    public void testOpenIdConnectListenerReturnsToOriginator() throws Exception {
        // call the openid connect listener with a mock code grant response
        MvcResult response = this.mockMvc
                .perform(MockMvcRequestBuilders.get(IdpOpenIdConnectClient.pathSuffixLogin)
.param("code",
 "a mock invalid code")
                        .sessionAttr(IdpOpenIdConnectClient.IdpRequestOriginatorKey, "/target"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.view().name("redirect:/target"))
                .andReturn();

        System.out.println(("Response Status: " + response.getResponse().getStatus()));
        System.out.println("Error message: " + response.getRequest().getAttribute("error_message"));
    }

    static final String[] keys = { "access_token", "token_type", "expires_in", "scope", "id_token" };
    static final String[] values = { "ccb0f673-6d27-4aaf-ba59-63dcb617a3aa", "bearer", "59", "openid",
            "eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJodHRwOi8vbG9jYWxob3N0OjgwODAvb2F1dGgvYXV0aG9yaXplIiwic3ViIjoidXNlciIsImF1ZCI6InRlc3Qub3BlbmlkLmltcGxpY2l0LmNsaWVudCIsImV4cCI6MTQ1MzI1ODM4MiwiaWF0IjoxNDUzMjU4MzIyLCJhenAiOiJ0ZXN0Lm9wZW5pZC5pbXBsaWNpdC5jbGllbnQiLCJhdXRoX3RpbWUiOjE0NTMyNTgzMjJ9.WAe0j4I00VzPSIibSC76uByMYxYV0LhkZzz3tCAbYMY" };
    @Test
    public void testOpenIdConnectListenerGetsUserFromTokenId() throws Exception {
        MvcResult response = this.mockMvc
                .perform(MockMvcRequestBuilders.get(IdpOpenIdConnectClient.pathSuffixLogin)
.param(keys[0], values[0])
                        .param(keys[1], values[1]).param(keys[2], values[2]).param(keys[3], values[3])
                        .param(keys[4], values[4])
                        .sessionAttr(IdpOpenIdConnectClient.IdpRequestOriginatorKey, "/target"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.view().name("redirect:/target")).andReturn();

        UserProfile user = (UserProfile) response.getRequest().getSession()
                .getAttribute(IdpOpenIdConnectClient.IdpUserProfileKey);
        Assert.assertNotNull(user);
        System.out.println("User: " + user);
        Assert.assertEquals(user.getUserId(), "user");
    }
}
