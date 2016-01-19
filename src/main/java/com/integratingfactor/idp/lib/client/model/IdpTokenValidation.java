package com.integratingfactor.idp.lib.client.model;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class IdpTokenValidation {

    @JsonProperty("exp")
    private String exp;

    @JsonProperty("user_name")
    private String userId;

    // this is the scope of the app
    @JsonProperty("scope")
    private Set<String> scopes;

    // for now will stick to single tenant per app
    @JsonProperty("authorities")
    private Set<String> authorities;

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
    public Set<String> getAuthorities() {
        return authorities;
    }

    @JsonProperty("authorities")
    public void setAuthorities(Set<String> authorities) {
        this.authorities = authorities;
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
}
