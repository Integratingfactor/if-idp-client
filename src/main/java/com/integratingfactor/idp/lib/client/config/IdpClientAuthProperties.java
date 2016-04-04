package com.integratingfactor.idp.lib.client.config;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

/**
 * global app properties provider bean
 * 
 * @author gnulib
 *
 */
public class IdpClientAuthProperties {
    
    static final String EnvClientIdKey = "idp.client.id";
    String appClientId = "test.backend.client";
    static final String EnvClientSecretKey = "idp.client.secret";
    String appClientSecret = "secret";
    static final String EnvClientServiceAccountKey = "idp.client.service.account";
    String appClientServiceAccount = null;
    static final String EnvClientServicePasswordKey = "idp.client.service.password";
    String appClientServicePassword = null;
    static final String EnvClientEncryptionKey = "idp.client.encryption.key";
    String appClientEncryptionKey = null;
    static final String EnvIdpHostUrlKey = "idp.client.idp.host";
    String idpHostUrl = "https://if-idp.appspot.com";
    static final String EnvClientRedirectUrlKey = "idp.client.redirect.url";
    String appRedirectUrl = "http://localhost:8080";
    static final String EnvClientPublicUrlsKey = "idp.client.public.urls";
    String appClientPublicUrls = "/,/about/**,/resources/**";

    @Autowired
    private Environment env;

    private String getNotNull(String envKey, String old) {
        String prop = env.getProperty(envKey);
        return prop == null ? old : prop;
    }

    @PostConstruct
    public void setup() {
        appClientId = getNotNull(EnvClientIdKey, appClientId);
        assert (appClientId != null);
        appClientSecret = getNotNull(EnvClientSecretKey, appClientSecret);
        assert (appClientSecret != null);
        appClientServiceAccount = getNotNull(EnvClientServiceAccountKey, appClientServiceAccount);
        appClientServicePassword = getNotNull(EnvClientServicePasswordKey, appClientServicePassword);
        appClientEncryptionKey = getNotNull(EnvClientEncryptionKey, appClientEncryptionKey);
        idpHostUrl = getNotNull(EnvIdpHostUrlKey, idpHostUrl);
        assert (idpHostUrl != null);
        appRedirectUrl = getNotNull(EnvClientRedirectUrlKey, appRedirectUrl);
        assert (appRedirectUrl != null);
        appClientPublicUrls = getNotNull(EnvClientPublicUrlsKey, appClientPublicUrls);
    }

    public String getMiscProp(String envKey) {
        return getNotNull(envKey, null);
    }

    public String getAppClientId() {
        return appClientId;
    }

    public String getAppClientSecret() {
        return appClientSecret;
    }

    public String getAppClientEncryptionKey() {
        return appClientEncryptionKey;
    }

    public String getIdpHostUrl() {
        return idpHostUrl;
    }

    public String getKmsHostUrl() {
        return idpHostUrl;
    }

    public String getAppRedirectUrl() {
        return appRedirectUrl;
    }

    public String getAppClientPublicUrls() {
        return appClientPublicUrls;
    }

    public String getAppServiceAccount() {
        return appClientServiceAccount;
    }

    public String getAppServicePassword() {
        return appClientServicePassword;
    }

}
