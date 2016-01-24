package com.integratingfactor.idp.lib.client.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(Include.NON_NULL)
public class IdpTokenValidation  implements Serializable, Authentication {

    /**
     * 
     */
    private static final long serialVersionUID = -5078798702231259432L;

    @JsonProperty("exp")
    private String exp;

    @JsonProperty("user_name")
    private String userId;

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    // this is the scope of the app
    @JsonProperty("scope")
    private Set<String> scopes;

    // for now will stick to single tenant per app
    @JsonProperty("authorities")
    private Set<GrantedAuthority> authorities;

    @JsonProperty("client_id")
    private String clientId;

    // this is the openid connect ID token
    @JsonProperty("id_token")
    private String idToken;

    @JsonProperty("exp")
    public String getExp() {
        return exp;
    }

    @JsonProperty("exp")
    public void setExp(String exp) {
        this.exp = exp;
    }

    @JsonProperty("user_name")
    public String getUserId() {
        return userId;
    }

    @JsonProperty("user_name")
    public void setUserId(String userId) {
        this.userId = userId;
    }

    @JsonProperty("scope")
    public Set<String> getScopes() {
        return scopes;
    }

    @JsonProperty("scope")
    public void setScopes(Set<String> scopes) {
        this.scopes = scopes;
    }

    @JsonProperty("authorities")
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @JsonProperty("authorities")
    public void setAuthorities(Set<String> authorities) {
        this.authorities = new HashSet<GrantedAuthority>();
        for (String authority : authorities) {
            this.authorities.add(new SimpleGrantedAuthority(authority));
        }
    }

    @JsonProperty("client_id")
    public String getClientId() {
        return clientId;
    }

    @JsonProperty("client_id")
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    @JsonProperty("id_token")
    public String getIdToken() {
        return idToken;
    }

    @JsonProperty("id_token")
    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    @JsonProperty("first_name")
    public String getFirstName() {
        return firstName;
    }

    @JsonProperty("first_name")
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @JsonProperty("last_name")
    public String getLastName() {
        return lastName;
    }

    @JsonProperty("last_name")
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public String getName() {
        return getFirstName();
    }

    @Override
    public Object getCredentials() {
        // credentials are not applicable to OpenID Connect authentication
        return null;
    }

    @Override
    public Object getDetails() {
        return getFirstName() + " " + getLastName();
    }

    @Override
    public Object getPrincipal() {
        return getUserId();
    }

    @Override
    public boolean isAuthenticated() {
        return true;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        // NOOP
    }

    @Override
    public String toString() {
        return getUserId();
    }
}
