package com.integratingfactor.idp.lib.client.rbac;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

public class IdpApiRbacDetails {
    private String token;

    private String accountId;

    private String clientId;

    private Set<String> roles;

    private String tenantId;

    private Set<String> scopes;

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public Set<String> getRoles() {
        return roles;
    }

    private Set<String> toSet(Object o) {
        if (!(o instanceof Collection<?>))
            return null;

        Set<String> set = new LinkedHashSet<String>();
        for (Object item : (Collection<?>) o) {
            set.add(item.toString());
        }
        return set;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public Set<String> getScopes() {
        return scopes;
    }

    public void setScopes(Object scopes) {
        this.scopes = toSet(scopes);
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "IdpApiRbacDetails [token=" + token + ", accountId=" + accountId + ", clientId=" + clientId + ", roles="
                + roles + ", tenantId=" + tenantId + ", scopes=" + scopes + "]";
    }

}
