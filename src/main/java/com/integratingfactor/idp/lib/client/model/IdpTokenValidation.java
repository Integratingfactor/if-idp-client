package com.integratingfactor.idp.lib.client.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.fasterxml.jackson.annotation.JsonAnySetter;
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

    @JsonProperty("given_name")
    private String firstName;

    @JsonProperty("family_name")
    private String lastName;

    // this is the scope of the app
    @JsonProperty("scope")
    private Set<String> scopes;

    @JsonProperty("org_roles")
    private Set<String> roles;

    // for now will stick to single tenant per app
    @JsonProperty("authorities")
    private Set<GrantedAuthority> authorities;

    @JsonProperty("client_id")
    private String clientId;

    @JsonProperty("org_id")
    private String org;

    // this is the openid connect ID token
    @JsonProperty("id_token")
    private String idToken;

    Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public String getExp() {
        return exp;
    }

    public void setExp(String exp) {
        this.exp = exp;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Set<String> getScopes() {
        return scopes;
    }

    public void setScopes(Set<String> scopes) {
        this.scopes = scopes;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Set<String> authorities) {
        this.authorities = new HashSet<GrantedAuthority>();
        for (String authority : authorities) {
            this.authorities.add(new SimpleGrantedAuthority(authority));
        }
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getOrg() {
        return org;
    }

    public void setOrg(String org) {
        this.org = org;
    }

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

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

    public Map<String, Object> getAdditionalProperties() {
        return additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperties(String key, Object value) {
        additionalProperties.put(key, value);
    }
}
