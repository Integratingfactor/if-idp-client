package com.integratingfactor.idp.lib.client.filter;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;

import com.integratingfactor.idp.lib.client.service.IdpOpenIdConnectClient;

/**
 * Authentication entry point to commence openid connect authentication
 * 
 * @author gnulib
 *
 */
public class IdpOpenIdConnectAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private static Logger LOG = Logger.getLogger(IdpOpenIdConnectAuthenticationEntryPoint.class.getName());

    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Autowired
    IdpOpenIdConnectClient openIdClient;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException {
        LOG.info("attempt to authenticate request: " + request.getRequestURI());
        redirectStrategy.sendRedirect(request, response, openIdClient.getAuthorizationUri());

    }

}
