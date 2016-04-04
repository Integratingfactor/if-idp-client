package com.integratingfactor.idp.lib.client.kms;

import java.io.Serializable;

import org.springframework.beans.factory.InitializingBean;

import com.integratingfactor.crypto.lib.factory.model.IdpDecrypted;
import com.integratingfactor.crypto.lib.factory.model.IdpEncrypted;

public interface IdpKmsClient extends InitializingBean {
    /**
     * encrypt using latest version of app's service key
     * 
     * @param data
     *            object to be encrypted
     * @return encrypted cipher text
     */
    <T extends Serializable> IdpEncrypted encrypt(T data);

    /**
     * decrypt using appropriate version of app's service key
     * 
     * @param encrypted
     *            encrypted cipher text
     * @return decrypted object along with service key version used for
     *         decryption
     */
    <T extends Serializable> IdpDecrypted<T> decrypt(IdpEncrypted encrypted);
}
