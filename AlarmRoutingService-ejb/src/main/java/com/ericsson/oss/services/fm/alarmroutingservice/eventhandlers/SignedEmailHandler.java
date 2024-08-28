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

package com.ericsson.oss.services.fm.alarmroutingservice.eventhandlers;

import static com.ericsson.oss.services.fm.alarmroutingservice.util.EmailConstants.DIGITAL_SIGNATURE_PATH;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.EmailConstants.STORE_ALIAS;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.activation.CommandMap;
import javax.activation.MailcapCommandMap;
import javax.inject.Inject;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.bouncycastle.cert.jcajce.JcaCertStore;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.mail.smime.SMIMESignedGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.services.fm.alarmroutingservice.util.KeyStoreFactory;
import com.ericsson.oss.services.fm.alarmroutingservice.util.PasswordFactory;
import com.ericsson.oss.services.fm.alarmroutingservice.util.SignerFactory;

/**
 * SignedEmailHandler provides functionality to apply digital signature to the email message and providing security with the help of
 * {@link SMIMESignedGenerator }.
 */
public class SignedEmailHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(SignedEmailHandler.class);

    @Inject
    private KeyStoreFactory keyStoreFactory;

    @Inject
    private PasswordFactory passwordFactory;

    /**
     * method takes session and mime message and signs the message.
     * @param session
     *            it represents SMTP Session.
     * @param mimeMessage
     *            it represents the MimeMessage.
     * @return it returns the signed MimeMessage
     * @throws Exception
     *             Exception
     */
    public MimeMessage getSignedMessage(final MimeMessage mimeMessage) throws Exception {
        final SignerFactory signerFactory = new SignerFactory();
        getMailCap();

        Security.addProvider(new BouncyCastleProvider());

        final KeyStore keyStore = keyStoreFactory.getKeyStore();
        final String password = passwordFactory.getPassword();

        keyStore.load(new FileInputStream(DIGITAL_SIGNATURE_PATH), password.toCharArray());

        final Certificate[] chain = keyStore.getCertificateChain(STORE_ALIAS);

        // Get the private key to sign the message.
        final PrivateKey privateKey = (PrivateKey) keyStore.getKey(STORE_ALIAS, password.toCharArray());

        if (privateKey == null) {
            throw new Exception("cannot find private key for alias: " + STORE_ALIAS);
        }

        // Create the SMIMESignedGenerator
        final SMIMESignedGenerator signer = signerFactory.getSigner(chain, privateKey);

        // Add the list of certs to the generator
        final List<Certificate> certificates = new ArrayList<Certificate>();
        certificates.add(chain[0]);

        signer.addCertificates(new JcaCertStore(certificates));

        // Sign the message
        final MimeMultipart signedMultipart = signer.generate(mimeMessage);

        // Set the content of the signed message
        mimeMessage.setContent(signedMultipart);
        mimeMessage.saveChanges();

        LOGGER.debug("The signed message is {}", mimeMessage);

        return mimeMessage;
    }

    /**
     * This is used to provide runtime environment for security for sending each email.
     */
    private void getMailCap() {
        final MailcapCommandMap mailcap = (MailcapCommandMap) CommandMap.getDefaultCommandMap();

        mailcap.addMailcap("application/pkcs7-signature;; x-java-content-handler=org.bouncycastle.mail.smime.handlers.pkcs7_signature");
        mailcap.addMailcap("application/pkcs7-mime;; x-java-content-handler=org.bouncycastle.mail.smime.handlers.pkcs7_mime");
        mailcap.addMailcap("application/x-pkcs7-signature;; x-java-content-handler=org.bouncycastle.mail.smime.handlers.x_pkcs7_signature");
        mailcap.addMailcap("application/x-pkcs7-mime;; x-java-content-handler=org.bouncycastle.mail.smime.handlers.x_pkcs7_mime");
        mailcap.addMailcap("multipart/signed;; x-java-content-handler=org.bouncycastle.mail.smime.handlers.multipart_signed");

        CommandMap.setDefaultCommandMap(mailcap);
    }

}
