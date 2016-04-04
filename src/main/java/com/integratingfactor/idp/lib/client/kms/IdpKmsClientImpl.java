package com.integratingfactor.idp.lib.client.kms;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import com.integratingfactor.crypto.lib.factory.exceptions.IdpDecryptionException;
import com.integratingfactor.crypto.lib.factory.model.IdpDecrypted;
import com.integratingfactor.crypto.lib.factory.model.IdpEncrypted;
import com.integratingfactor.crypto.lib.factory.service.IdpCryptoFactory;
import com.integratingfactor.crypto.lib.factory.specs.IdpPbeKeySpec;
import com.integratingfactor.crypto.lib.factory.specs.IdpSecretKeySpec;
import com.integratingfactor.idp.lib.client.config.IdpClientAuthProperties;
import com.integratingfactor.idp.lib.client.model.IdpWrappedKeySpecJson;

public class IdpKmsClientImpl implements IdpKmsClient {
    private static Logger LOG = Logger.getLogger(IdpKmsClientImpl.class.getName());

    private RestTemplate restTemplate;

    private String kmsHost;

    private HttpHeaders headers;

    private static final String ApiPrefix = "/api/v1/keys";

    static final Integer KmsKeyDerivativeCount = 65521;

    static final Integer KmsKeyLength = 128;

    IdpPbeKeySpec currKeySpec = null;

    Map<Integer, IdpSecretKeySpec> keys = new ConcurrentHashMap<Integer, IdpSecretKeySpec>();

    Integer latestKeyVersion = 0;

    private ThreadLocal<IdpCryptoFactory> crypto = new ThreadLocal<IdpCryptoFactory>();

    @Autowired
    IdpClientAuthProperties clientProperties;

    @Override
    public void afterPropertiesSet() throws Exception {
        initialize(clientProperties.getAppClientId(), clientProperties.getAppClientSecret(),
                clientProperties.getKmsHostUrl(), clientProperties.getAppClientEncryptionKey());
    }

    public void initialize(String clientId, String clientSecret, String kmsHost, String encryptionKey) {
        if (StringUtils.isEmpty(clientId)) {
            LOG.warning("Client ID cannot be empty");
            throw new RuntimeException("Client ID cannot be empty");
        }
        if (StringUtils.isEmpty(clientSecret)) {
            LOG.warning("Client secret cannot be empty");
            throw new RuntimeException("Client secret cannot be empty");
        }
        if (StringUtils.isEmpty(kmsHost)) {
            LOG.warning("KMS host cannot be empty");
            throw new RuntimeException("KMS host cannot be empty");
        }
        this.kmsHost = kmsHost;
        if (StringUtils.isEmpty(encryptionKey)) {
            LOG.warning("Encryption key cannot be empty");
            throw new RuntimeException("Encryption key cannot be empty");
        }
        this.headers = new HttpHeaders();
        this.headers.set("Authorization", "Basic " + new String(
                Base64.encodeBase64(new String(clientId + ":" + clientSecret).getBytes(Charset.forName("US-ASCII")))));
        headers.setContentType(MediaType.APPLICATION_JSON);
        restTemplate = new RestTemplate();

        // fetch service keys from KMS
        List<IdpWrappedKeySpecJson> serviceKeys = fetchServiceKeys();

        // initialize crypto with encryption key (version will match latest
        // service key version)
        this.currKeySpec = getKeySpec(encryptionKey, serviceKeys);
        IdpCryptoFactory crypto = IdpCryptoFactory.getInstance();
        crypto.init(this.currKeySpec, encryptionKey.toCharArray());

        // unwrap and store versioned service keys in memory
        for (IdpWrappedKeySpecJson key : serviceKeys) {
            this.keys.put(key.getVersion(), crypto.unwrap(IdpWrappedKeySpecJson.toIdpWrappedKeySpec(key)));
            latestKeyVersion = key.getVersion() > latestKeyVersion ? key.getVersion() : latestKeyVersion;
        }

    }

    private IdpPbeKeySpec getKeySpec(String encryptionKey, List<IdpWrappedKeySpecJson> serviceKeys) {
        IdpPbeKeySpec keySpec = new IdpPbeKeySpec();
        // walk through service keys list to find latest versioned key
        IdpWrappedKeySpecJson currKey = serviceKeys.get(0);
        for (IdpWrappedKeySpecJson key : serviceKeys) {
            currKey = key.getVersion() > currKey.getVersion() ? key : currKey;
        }
        keySpec.setEncryptionAlgorithm(currKey.getEncryptionAlgorithm());
        keySpec.setKeyAlgorithm(currKey.getKeyAlgorithm());
        keySpec.setKeySize(KmsKeyLength);
        keySpec.setDerivationCount(KmsKeyDerivativeCount);
        keySpec.setSalt(encryptionKey.getBytes());
        keySpec.setVersion(currKey.getVersion());
        return keySpec;
    }

    IdpCryptoFactory myCrypto(Integer ver) {
        IdpCryptoFactory myCrypto = crypto.get();
        if (myCrypto == null) {
            myCrypto = IdpCryptoFactory.getInstance();
            crypto.set(myCrypto);
        }
        myCrypto.init(keys.get(ver));
        return myCrypto;
    }

    private List<IdpWrappedKeySpecJson> fetchServiceKeys() {
        try {
            return restTemplate.exchange(kmsHost + ApiPrefix, HttpMethod.GET, new HttpEntity<>(headers),
                    new ParameterizedTypeReference<List<IdpWrappedKeySpecJson>>() {
                    })
                    .getBody();

        } catch (Exception e) {
            LOG.warning("Failed to fetch service keys: " + e.getMessage());
            throw new RuntimeException("failed to fetch service keys for app");
        }
    }

    @Override
    public <T extends Serializable> IdpEncrypted encrypt(T data) {
        // encryption will always use latest version of service key
        return myCrypto(latestKeyVersion).encrypt(data);
    }

    @Override
    public <T extends Serializable> IdpDecrypted<T> decrypt(IdpEncrypted encrypted) {
        if (!keys.containsKey(encrypted.getKeyVersion())) {
            LOG.warning("Encryption key version " + encrypted.getKeyVersion() + " does not exists");
            throw new IdpDecryptionException("encryption key version does not exists");
        }
        return myCrypto(encrypted.getKeyVersion()).decrypt(encrypted);
    }

}
