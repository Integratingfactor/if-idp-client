package com.integratingfactor.idp.lib.client.kms;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.integratingfactor.crypto.lib.factory.model.IdpEncrypted;
import com.integratingfactor.crypto.lib.factory.service.IdpCryptoFactory;
import com.integratingfactor.idp.lib.client.config.IdpClientAuthProperties;

@ContextConfiguration(classes = { IdpKmsClientTestConfig.class })
public class IdpKmsClientTest extends AbstractTestNGSpringContextTests {

    @Autowired
    IdpKmsClientImpl kmsClient;

    @Autowired
    IdpClientAuthProperties clientProperties;

    @Test
    public void testBeanInitialization() {
        Assert.assertNotNull(kmsClient);
        Assert.assertFalse(kmsClient.keys.isEmpty());
        Assert.assertEquals(kmsClient.currKeySpec.getVersion(), kmsClient.latestKeyVersion);
    }

    @Test
    public void testEncryption() {
        String data = "some test data";

        IdpEncrypted seData = kmsClient.encrypt(data);

        // encryption should always use latest version key, which is same as PBE
        // using encryption key of the app
        IdpCryptoFactory crypto = IdpCryptoFactory.getInstance();
        crypto.init(kmsClient.currKeySpec, clientProperties.getAppClientEncryptionKey().toCharArray());
        Assert.assertEquals(data, crypto.decrypt(seData).getData().toString());
    }

    @Test
    public void testDecryption() {
        String data = "some test data";
        // encryption should always use latest version key, which is same as PBE
        // using encryption key of the app
        IdpCryptoFactory crypto = IdpCryptoFactory.getInstance();
        crypto.init(kmsClient.currKeySpec, clientProperties.getAppClientEncryptionKey().toCharArray());
        IdpEncrypted seData = crypto.encrypt(data);
        Assert.assertEquals(data, kmsClient.decrypt(seData).getData().toString());
    }
}
