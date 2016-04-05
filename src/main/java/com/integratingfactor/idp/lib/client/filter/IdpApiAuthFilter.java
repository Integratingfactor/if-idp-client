package com.integratingfactor.idp.lib.client.filter;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.integratingfactor.idp.lib.client.config.IdpClientAuthProperties;
import com.integratingfactor.idp.lib.client.model.IdpTokenValidation;
import com.integratingfactor.idp.lib.client.rbac.IdpApiRbacDetails;
import com.integratingfactor.idp.lib.client.rbac.IdpRbacAccessDeniedException;
import com.integratingfactor.idp.lib.client.rbac.IdpRbacAuthenticationException;
import com.integratingfactor.idp.lib.client.util.IdpOauthClient;

public class IdpApiAuthFilter extends OncePerRequestFilter {
    private static Logger LOG = Logger.getLogger(IdpApiAuthFilter.class.getName());

    // public static final String IdpTokenRbacDetails =
    // "IDP_TOKEN_RBAC_DETAILS";

    public static final String AuthTokenType = OAuth2AccessToken.BEARER_TYPE;

    public static final int StartIndex = AuthTokenType.length() + 1;

    private static ThreadLocal<IdpApiRbacDetails> rbacDetails = new ThreadLocal<IdpApiRbacDetails>();

    @Autowired
    private IdpOauthClient oauthClient;

    @Autowired
    IdpClientAuthProperties clientProperties;

    private Set<AntPathRequestMatcher> publicUrls = null;

    synchronized boolean isPublic(HttpServletRequest request) {
        if (publicUrls == null) {
            publicUrls = new HashSet<AntPathRequestMatcher>();
            for (String url : clientProperties.getAppClientPublicUrls().split(",")) {
                publicUrls.add(new AntPathRequestMatcher(url));
            }
        }
        for (AntPathRequestMatcher matcher : publicUrls) {
            if (matcher.matches(request))
                return true;
        }
        return false;
    }

    public static IdpApiRbacDetails getRbacDetails() {
        return rbacDetails.get();
    }

    private static void setRbacDetails(IdpApiRbacDetails details) {
        rbacDetails.set(details);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        LOG.info("RBAC on request: " + request.getRequestURI());
        try {
            // first get the authorization header
            String authorization = request.getHeader("Authorization");
            if (!isPublic(request)) {
                if (StringUtils.isEmpty(authorization) || !authorization.startsWith(AuthTokenType)) {
                    // not authorized
                    sendError(response, HttpStatus.UNAUTHORIZED, "Missing or incorrect authorization header");
                    return;
                }
                // request.setAttribute(IdpTokenRbacDetails,
                // getRbacDetails(authorization.substring(StartIndex)));
                setRbacDetails(getRbacDetails(authorization.substring(StartIndex)));
            }
            filterChain.doFilter(request, response);
            setRbacDetails(null);
        } catch (IdpRbacAuthenticationException e) {
            sendError(response, HttpStatus.UNAUTHORIZED, e.getMessage());
            return;
        } catch (IdpRbacAccessDeniedException e) {
            sendError(response, HttpStatus.FORBIDDEN, e.getMessage());
            return;
        } catch (Exception e) {
            if (e.getCause() instanceof IdpRbacAccessDeniedException) {
                sendError(response, HttpStatus.FORBIDDEN, ((IdpRbacAccessDeniedException) e.getCause()).getMessage());
            }
            return;
        }
    }

    private IdpApiRbacDetails getRbacDetails(String token) {
        IdpApiRbacDetails rbacDetails = new IdpApiRbacDetails();
        IdpTokenValidation validation = oauthClient.validateToken(token);
        if (validation == null) {
            throw new IdpRbacAuthenticationException("user not authenticated");
        }
        rbacDetails.setToken(token);
        rbacDetails.setAccountId(validation.getUserId());
        rbacDetails.setClientId(validation.getClientId());
        rbacDetails.setTenantId(validation.getOrg());
        LOG.info("Got roles: " + validation.getRoles());
        rbacDetails.setRoles(validation.getRoles());
        rbacDetails.setScopes(validation.getScopes());
        LOG.info("Adding RBAC Details " + rbacDetails);
        return rbacDetails;
    }

    private void sendError(HttpServletResponse response, HttpStatus status, String error) {
        LOG.info("Error: " + error);
        response.setContentType("application/json");
        response.setStatus(status.value());
        StringBuffer sb = new StringBuffer();
        sb.append("{\n \"error\" : \"" + error + "\"\n}");
        response.setContentLength(sb.length());
        try {
            response.getOutputStream().write(sb.toString().getBytes());
            response.getOutputStream().close();
        } catch (IOException e) {
            LOG.warning("Failed to write error response: " + e.getMessage());
        }
    }
}
