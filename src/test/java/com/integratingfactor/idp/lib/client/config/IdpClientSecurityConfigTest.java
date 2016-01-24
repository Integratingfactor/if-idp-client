package com.integratingfactor.idp.lib.client.config;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.csrf.CsrfToken;
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

import com.integratingfactor.idp.lib.client.filter.IdpOpenIdConnectAuthenticationEntryPoint;
import com.integratingfactor.idp.lib.client.filter.IdpOpenIdConnectAuthenticationFilter;
import com.integratingfactor.idp.lib.client.model.IdpTokenValidation;
import com.integratingfactor.idp.lib.client.mvc.IdpTestMvcConfig;
import com.integratingfactor.idp.lib.client.mvc.IdpTestMvcHandler;
import com.integratingfactor.idp.lib.client.util.IdpOpenIdConnectClient;

@ContextConfiguration(classes = { IdpClientSecurityConfig.class, IdpTestMvcConfig.class })
@WebAppConfiguration
public class IdpClientSecurityConfigTest extends AbstractTestNGSpringContextTests {
    @Autowired
    IdpClientSecurityConfig securityConfig;

    @Autowired
    private FilterChainProxy springSecurityFilterChain;

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    IdpTestMvcHandler endpoint;

    private MockMvc mockMvc;

    @Autowired
    @InjectMocks
    IdpOpenIdConnectAuthenticationFilter authenticationFilter;

    @Autowired
    @InjectMocks
    IdpOpenIdConnectAuthenticationEntryPoint authenticationEntryPoint;

    @Mock
    IdpOpenIdConnectClient client;

    static final String TestIdpUrl = "https://test.idp.url";

    static IdpTokenValidation user;

    static {
        user = new IdpTokenValidation();
        user.setUserId(UUID.randomUUID().toString());
        Set<String> authorities = new HashSet<String>();
        authorities.add("USER");
        user.setAuthorities(authorities);
    }

    @BeforeMethod
    public void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(endpoint).addFilters(springSecurityFilterChain).build();
        MockitoAnnotations.initMocks(this);
        Mockito.when(client.getAuthorizationUri()).thenReturn(TestIdpUrl);
    }

    @Test
    public void securityConfigurationLoads() {
        Assert.assertNotNull(authManager);
        Assert.assertNotNull(springSecurityFilterChain);
    }

    @Test
    public void testOpenidConnectRedirectHandlerForInvalid() throws Exception {
        // mock openid client to return null user for the request
        Mockito.when(client.getValidatedUser(Mockito.anyMap())).thenReturn(null);

        MvcResult response = this.mockMvc.perform(MockMvcRequestBuilders.get(IdpOpenIdConnectClient.pathSuffixLogin))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.UNAUTHORIZED.value()))
                .andReturn();

        Mockito.verify(client).getValidatedUser(Mockito.anyMap());

        System.out.println(("Response Status: " + response.getResponse().getStatus()));
    }

    @Test
    public void testOpenidConnectRedirectHandlerForValid() throws Exception {
        // mock openid client to return valid user for the request
        Mockito.when(client.getValidatedUser(Mockito.anyMap())).thenReturn(user);

        MvcResult response = this.mockMvc
                .perform(MockMvcRequestBuilders.get(IdpOpenIdConnectClient.pathSuffixLogin)
        /*
         * .sessionAttr(IdpOpenIdConnectClient.IdpRequestOriginatorKey,
         * "/target")
         */).andExpect(MockMvcResultMatchers.status().is3xxRedirection()).andReturn();

        System.out.println(("Response Status: " + response.getResponse().getStatus()));
        System.out.println("Redirected url: " + response.getResponse().getRedirectedUrl());
    }

    @Test
    public void testAuthenticationEntryPointForGet() throws Exception {
        // mock openid client to return null user for the request
        Mockito.when(client.getValidatedUser(Mockito.anyMap())).thenReturn(null);

        MvcResult response = this.mockMvc
                .perform(MockMvcRequestBuilders.get(IdpOpenIdConnectClient.pathSuffixLogin + "/some/random/url"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl(TestIdpUrl)).andReturn();

        // openid client should not be called when its not IDP redirect
        Mockito.verify(client, Mockito.times(0)).getValidatedUser(Mockito.anyMap());

        System.out.println(("Response Status: " + response.getResponse().getStatus()));
        System.out.println("Redirected url: " + response.getResponse().getRedirectedUrl());
    }

    @Test
    public void testAuthenticationEntryPointForPost() throws Exception {
        // mock openid client to return null user for the request
        Mockito.when(client.getValidatedUser(Mockito.anyMap())).thenReturn(user);

        // perform a login to get valid csrf token
        MvcResult login = this.mockMvc.perform(MockMvcRequestBuilders.get(IdpOpenIdConnectClient.pathSuffixLogin))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection()).andReturn();

        CsrfToken csrf = (CsrfToken) login.getRequest().getAttribute("_csrf");
        System.out.println("CSRF token: " + csrf.getToken());

        // now perform another get by reusing the session and this time should
        // not go to filter
        this.mockMvc.perform(MockMvcRequestBuilders.post(IdpOpenIdConnectClient.pathSuffixLogin)
                .param("_csrf", csrf.getToken()).session((MockHttpSession) login.getRequest().getSession()));

        // openid client should not be called 2nd time for POST method
        Mockito.verify(client, Mockito.times(1)).getValidatedUser(Mockito.anyMap());
    }

    @Test
    public void testAuthenticationEntryPointForGetWithAuthenticatedSession() throws Exception {
        // mock openid client to return valid user for the request
        Mockito.when(client.getValidatedUser(Mockito.anyMap())).thenReturn(user);
        // perform a login to get valid csrf token
        MvcResult login = this.mockMvc.perform(MockMvcRequestBuilders.get(IdpOpenIdConnectClient.pathSuffixLogin))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection()).andReturn();

        // now perform another get by reusing the session and this time should
        // not go to filter
        MvcResult logout = this.mockMvc
                .perform(MockMvcRequestBuilders.get("/some/random/url/that/does/not/exists")
                        .session((MockHttpSession) login.getRequest().getSession()))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.NOT_FOUND.value())).andReturn();

        System.out.println(("Response Status: " + logout.getResponse().getStatus()));

        // openid client only have been called once
        Mockito.verify(client, Mockito.times(1)).getValidatedUser(Mockito.anyMap());
    }

    @Test
    public void testLogoutHandlerWithValidCsrf() throws Exception {
        // mock openid client to return valid user for the request
        Mockito.when(client.getValidatedUser(Mockito.anyMap())).thenReturn(user);
        // perform a login to get valid csrf token
        MvcResult login = this.mockMvc.perform(MockMvcRequestBuilders.get(IdpOpenIdConnectClient.pathSuffixLogin))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection()).andReturn();
        CsrfToken csrf = (CsrfToken) login.getRequest().getAttribute("_csrf");
        System.out.println("CSRF token: " + csrf.getToken());

        // now perform logout using this valid csrf token
        MvcResult logout = this.mockMvc.perform(
                MockMvcRequestBuilders.post(IdpOpenIdConnectClient.pathSuffixLogout).param("_csrf", csrf.getToken()).session((MockHttpSession) login.getRequest().getSession()))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection()).andReturn();

        System.out.println(("Response Status: " + logout.getResponse().getStatus()));
        System.out.println("Redirected url: " + logout.getResponse().getRedirectedUrl());
    }

}
