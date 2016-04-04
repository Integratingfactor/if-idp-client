package com.integratingfactor.idp.lib.client.kms;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

@ContextConfiguration(classes = { IdpKmsClientTestConfig.class })
public class IdpKmsClientTest extends AbstractTestNGSpringContextTests {

    @Autowired
    IdpKmsClient kmsClient;

    @Test
    public void testBeanInitialization() {
        Assert.assertNotNull(kmsClient);
    }
}
