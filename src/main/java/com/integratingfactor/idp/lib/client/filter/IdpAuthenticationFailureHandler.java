package com.integratingfactor.idp.lib.client.filter;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import com.integratingfactor.idp.lib.client.service.IdpOpenIdConnectClient;

public class IdpAuthenticationFailureHandler implements AuthenticationFailureHandler {
    private static Logger LOG = Logger.getLogger(IdpAuthenticationFailureHandler.class.getName());

    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Autowired
    IdpOpenIdConnectClient openIdClient;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) throws IOException, ServletException {

        LOG.info("Redirecting to IDP service to authenticate request: " + request.getRequestURI());
        redirectStrategy.sendRedirect(request, response, openIdClient.getAuthorizationUri());

    }

}
