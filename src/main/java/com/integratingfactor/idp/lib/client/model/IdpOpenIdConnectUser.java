package com.integratingfactor.idp.lib.client.model;

import java.io.Serializable;
import java.util.Collection;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

public class IdpOpenIdConnectUser implements Serializable, Authentication {

    /**
     * 
     */
    private static final long serialVersionUID = -2847764599170939894L;

    private String firstName;

    private String lastName;

    private String subject;

    private String userId;

    private boolean authenticated;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return userId;
    }

    @Override
    public String getName() {
        return getFirstName();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Object getCredentials() {
        // credentials are not applicable to OpenID Connect authentication
        return null;
    }

    @Override
    public Object getDetails() {
        return getSubject();
    }

    @Override
    public Object getPrincipal() {
        return getUserId();
    }

    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        this.authenticated = isAuthenticated;

    }

}
