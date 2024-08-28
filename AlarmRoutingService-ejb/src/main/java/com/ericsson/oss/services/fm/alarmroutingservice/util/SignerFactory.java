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

import static com.ericsson.oss.services.fm.alarmroutingservice.util.EmailConstants.BC;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.EmailConstants.DSA;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.EmailConstants.MD5withRSA;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.EmailConstants.SHA1withDSA;

import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;

import javax.enterprise.context.ApplicationScoped;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.asn1.cms.IssuerAndSerialNumber;
import org.bouncycastle.asn1.smime.SMIMECapabilitiesAttribute;
import org.bouncycastle.asn1.smime.SMIMECapability;
import org.bouncycastle.asn1.smime.SMIMECapabilityVector;
import org.bouncycastle.asn1.smime.SMIMEEncryptionKeyPreferenceAttribute;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cms.jcajce.JcaSimpleSignerInfoGeneratorBuilder;
import org.bouncycastle.mail.smime.SMIMESignedGenerator;
import org.bouncycastle.operator.OperatorCreationException;

/**
 * SignerFactory provides {@link SMIMESignedGenerator} instance to apply digital signature to the MimeMessage message.
 */
@ApplicationScoped
public class SignerFactory {

    private SMIMESignedGenerator smimeSignedGenerator;

    private ASN1EncodableVector asn1EncodableVector;

    /**
     * Method takes Certificates array {@link Certificate} and privateKey {@link PrivateKey}.And based on the private key's algorithm
     * SMIMESignedGenerator {@link SMIMESignedGenerator} instance will be created to sign the  MimeMessage message.
     * @param chain
     *            it represents the certificate chain
     * @param privateKey
     *            it represents the private key which is used to sign the message
     * @return SMIMESignedGenerator
     * @throws OperatorCreationException
     * @throws CertificateEncodingException
     */
    public SMIMESignedGenerator getSigner(final Certificate[] chain, final PrivateKey privateKey) throws OperatorCreationException,
            CertificateEncodingException {
        if (smimeSignedGenerator == null) {
            asn1EncodableVector = new ASN1EncodableVector();
            asn1EncodableVector.add(new SMIMEEncryptionKeyPreferenceAttribute(new IssuerAndSerialNumber(new X500Name(((X509Certificate) chain[0])
                    .getIssuerDN().getName()), ((X509Certificate) chain[0]).getSerialNumber())));
            addSMIMECapabilities(asn1EncodableVector);

            smimeSignedGenerator = new SMIMESignedGenerator();
            smimeSignedGenerator.addSignerInfoGenerator(new JcaSimpleSignerInfoGeneratorBuilder().setProvider(BC)
                    .setSignedAttributeGenerator(new AttributeTable(asn1EncodableVector))
                    .build(DSA.equals(privateKey.getAlgorithm()) ? SHA1withDSA : MD5withRSA, privateKey, (X509Certificate) chain[0]));
            return smimeSignedGenerator;
        }
        return smimeSignedGenerator;
    }

    /**
     * Method takes {@link ASN1EncodableVector} and add the {@link SMIMECapabilityVector} capabilities.
     */
    private void addSMIMECapabilities(final ASN1EncodableVector encodableVector) {
        final SMIMECapabilityVector capabilities = new SMIMECapabilityVector();

        capabilities.addCapability(SMIMECapability.dES_EDE3_CBC);
        capabilities.addCapability(SMIMECapability.rC2_CBC, 128);
        capabilities.addCapability(SMIMECapability.dES_CBC);
        capabilities.addCapability(SMIMECapability.aES256_CBC);
        encodableVector.add(new SMIMECapabilitiesAttribute(capabilities));
    }
}
