package com.integratingfactor.idp.lib.client.model;

import org.springframework.util.Base64Utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.integratingfactor.crypto.lib.factory.model.IdpWrappedKeySpec;

@JsonInclude(Include.NON_NULL)
public class IdpWrappedKeySpecJson {

    @JsonProperty("enc_algo")
    String encryptionAlgorithm;

    @JsonProperty("key_algo")
    String keyAlgorithm;

    @JsonProperty("sec_key")
    String key;

    @JsonProperty("key_type")
    Integer keyType;

    @JsonProperty("key_ver")
    Integer version;

    @JsonProperty("sec_iv")
    String iv;

    public String getEncryptionAlgorithm() {
        return encryptionAlgorithm;
    }

    public void setEncryptionAlgorithm(String encryptionAlgorithm) {
        this.encryptionAlgorithm = encryptionAlgorithm;
    }

    public String getKeyAlgorithm() {
        return keyAlgorithm;
    }

    public void setKeyAlgorithm(String keyAlgorithm) {
        this.keyAlgorithm = keyAlgorithm;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Integer getKeyType() {
        return keyType;
    }

    public void setKeyType(Integer keyType) {
        this.keyType = keyType;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getIv() {
        return iv;
    }

    public void setIv(String iv) {
        this.iv = iv;
    }

    public static IdpWrappedKeySpecJson fromIdpWrappedKeySpec(IdpWrappedKeySpec key) {
        IdpWrappedKeySpecJson instance = new IdpWrappedKeySpecJson();
        instance.encryptionAlgorithm = key.getEncryptionAlgorithm();
        instance.keyAlgorithm = key.getKeyAlgorithm();
        instance.version = key.getVersion();
        instance.keyType = key.getKeyType();
        instance.key = Base64Utils.encodeToString(key.getKey());
        instance.iv = Base64Utils.encodeToString(key.getIv());
        return instance;
    }

    public static IdpWrappedKeySpec toIdpWrappedKeySpec(IdpWrappedKeySpecJson wKey) {
        IdpWrappedKeySpec key = new IdpWrappedKeySpec();
        key.setEncryptionAlgorithm(wKey.encryptionAlgorithm);
        key.setKeyAlgorithm(wKey.keyAlgorithm);
        key.setKeyType(wKey.keyType);
        key.setVersion(wKey.version);
        key.setIv(Base64Utils.decodeFromString(wKey.iv));
        key.setKey(Base64Utils.decodeFromString(wKey.key));
        return key;
    }
}
