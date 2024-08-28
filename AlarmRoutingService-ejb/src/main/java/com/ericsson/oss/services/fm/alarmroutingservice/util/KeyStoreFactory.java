/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2016
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/

package com.ericsson.oss.services.fm.alarmroutingservice.util;

import static com.ericsson.oss.services.fm.alarmroutingservice.util.EmailConstants.JKS;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import javax.enterprise.context.ApplicationScoped;

/**
 * KeyStoreFactory provides {@link KeyStore} instance to support Digital signing of email message.
 */
@ApplicationScoped
public class KeyStoreFactory {
    private static KeyStore keystore;

    public KeyStore getKeyStore() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, FileNotFoundException, IOException {
        if (keystore == null) {
            keystore = KeyStore.getInstance(JKS);
            return keystore;
        }
        return keystore;
    }
}
