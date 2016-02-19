package com.integratingfactor.idp.lib.client.filter;

import java.io.IOException;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.StringUtils;

import com.integratingfactor.idp.lib.client.config.IdpClientAuthProperties;
import com.integratingfactor.idp.lib.client.model.IdpTokenValidation;
import com.integratingfactor.idp.lib.client.rbac.IdpApiRbacDetails;
import com.integratingfactor.idp.lib.client.util.IdpOauthClient;

/**
 * below implementation does not works as expected, after successful
 * authentication spring security will redirect, even if we redirect to original
 * target url. This does not work well with REST API, cannot send a 302 on a
 * POST request!!!
 * 
 * so will have to use regular filter, not spring security, for API RBAC
 * 
 * @author gnulib
 *
 */
public class IdpApiRbacFilter extends AbstractAuthenticationProcessingFilter {
    private static Logger LOG = Logger.getLogger(IdpApiRbacFilter.class.getName());

    @Autowired
    IdpClientAuthProperties clientProperties;

    @Autowired
    AuthenticationManager authManager;

    public static final String IdpTokenRbacDetails = "IDP_TOKEN_RBAC_DETAILS";

    public static final String AuthTokenType = OAuth2AccessToken.BEARER_TYPE;

    public static final int StartIndex = AuthTokenType.length() + 1;

    DefaultRedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Autowired
    private IdpOauthClient oauthClient;

    @PostConstruct
    public void initialize() {
    }

    public IdpApiRbacFilter() {
        super("/");
    }

    @Override
    public void afterPropertiesSet() {
        super.setAuthenticationManager(authManager);
        if (clientProperties.getMiscProp("idp.client.api.path") != null) {
            LOG.info("Adding API path to filter: " + clientProperties.getMiscProp("idp.client.api.path"));
            super.setFilterProcessesUrl(clientProperties.getMiscProp("idp.client.api.path"));
            redirectStrategy.setContextRelative(false);
        } else {
            LOG.info("No API path configured");
        }
    }

    protected IdpApiRbacFilter(RequestMatcher requiresAuthenticationRequestMatcher) {
        super(requiresAuthenticationRequestMatcher);
        // TODO Auto-generated constructor stub
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException, IOException, ServletException {
        LOG.fine("RBAC on request: " + request.getRequestURI());
        // first get the authorization header
        String authorization = request.getHeader("Authorization");
        if (StringUtils.isEmpty(authorization) || !authorization.startsWith(AuthTokenType)) {
            // not authorized
            throw new AuthenticationCredentialsNotFoundException("access token required");
        }
        IdpTokenValidation userAuth = oauthClient.validateToken(authorization.substring(StartIndex));
        if (userAuth == null) {
            // not authorized
            throw new BadCredentialsException("access token not valid");
        }
        IdpApiRbacDetails rbacDetails = new IdpApiRbacDetails();
        rbacDetails.setToken(authorization.substring(StartIndex));
        rbacDetails.setAccountId(userAuth.getUserId());
        rbacDetails.setClientId(userAuth.getClientId());
        rbacDetails.setTenantId(userAuth.getOrg());
        rbacDetails.setRoles(userAuth.getRoles());
        rbacDetails.setScopes(userAuth.getScopes());
        LOG.info("Adding RBAC Details " + rbacDetails);
        request.setAttribute(IdpTokenRbacDetails, rbacDetails);
        redirectStrategy.sendRedirect(request, response, request.getRequestURI());
        return null;
    }
}
