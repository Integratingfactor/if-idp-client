package com.integratingfactor.idp.lib.client.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class IdToken {

    @JsonProperty("iss")
    private String iss;

    @JsonProperty("sub")
    private String sub;

    @JsonProperty("aud")
    private String aud;

    @JsonProperty("exp")
    private long exp;

    @JsonProperty("iat")
    private long iat;

    @JsonProperty("auth_time")
    private long authTime;

    @JsonProperty("nonce")
    private String nonce;

    @JsonProperty("acr")
    private String acr;

    @JsonProperty("amr")
    private String amr;

    @JsonProperty("azp")
    private String azp;

    @JsonProperty("iss")
    public String getIss() {
        return iss;
    }

    @JsonProperty("iss")
    public void setIss(String iss) {
        this.iss = iss;
    }

    @JsonProperty("sub")
    public String getSub() {
        return sub;
    }

    @JsonProperty("sub")
    public void setSub(String sub) {
        this.sub = sub;
    }

    @JsonProperty("aud")
    public String getAud() {
        return aud;
    }

    @JsonProperty("aud")
    public void setAud(String aud) {
        this.aud = aud;
    }

    @JsonProperty("exp")
    public long getExp() {
        return exp;
    }

    @JsonProperty("exp")
    public void setExp(long exp) {
        this.exp = exp;
    }

    @JsonProperty("iat")
    public long getIat() {
        return iat;
    }

    @JsonProperty("iat")
    public void setIat(long iat) {
        this.iat = iat;
    }

    @JsonProperty("auth_time")
    public long getAuthTime() {
        return authTime;
    }

    @JsonProperty("auth_time")
    public void setAuthTime(long authTime) {
        this.authTime = authTime;
    }

    @JsonProperty("nonce")
    public String getNonce() {
        return nonce;
    }

    @JsonProperty("nonce")
    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    @JsonProperty("acr")
    public String getAcr() {
        return acr;
    }

    @JsonProperty("acr")
    public void setAcr(String acr) {
        this.acr = acr;
    }

    @JsonProperty("amr")
    public String getAmr() {
        return amr;
    }

    @JsonProperty("amr")
    public void setAmr(String amr) {
        this.amr = amr;
    }

    @JsonProperty("azp")
    public String getAzp() {
        return azp;
    }

    @JsonProperty("azp")
    public void setAzp(String azp) {
        this.azp = azp;
    }

}
