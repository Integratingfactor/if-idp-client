package com.integratingfactor.idp.lib.client.filter;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import com.integratingfactor.idp.lib.client.model.IdpTokenValidation;
import com.integratingfactor.idp.lib.client.service.IdpOpenIdConnectClient;

public class IdpOpenIdConnectAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
    private static Logger LOG = Logger.getLogger(IdpOpenIdConnectAuthenticationFilter.class.getName());

    public IdpOpenIdConnectAuthenticationFilter() {
        super(new AntPathRequestMatcher(IdpOpenIdConnectClient.pathSuffixLogin, "GET"));
        LOG.info("Filter has been created and in place for openid connect authentication....");
    }

    protected IdpOpenIdConnectAuthenticationFilter(RequestMatcher requiresAuthenticationRequestMatcher) {
        super(requiresAuthenticationRequestMatcher);
        // TODO Auto-generated constructor stub
        LOG.warning("Not sure why this constructor was called ????");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException, IOException, ServletException {
        LOG.info("attempt to authenticate request: " + request.getRequestURI());
        // get the user profile from session context
        IdpTokenValidation user = (IdpTokenValidation) request.getSession(true)
                .getAttribute(IdpOpenIdConnectClient.IdpUserProfileKey);

        if (user == null) {
            LOG.info("Openid connect user information does not exists, user is not yet authenticated");
            // openid connect authentication is not done yet
            throw new AuthenticationCredentialsNotFoundException("OpenID Connect user not found");
        }

        LOG.info("User has been authenticated already...");
        return user;
    }
}
