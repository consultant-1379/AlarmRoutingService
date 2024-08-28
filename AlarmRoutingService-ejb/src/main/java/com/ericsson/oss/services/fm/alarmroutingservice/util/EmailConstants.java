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

/**
 * Utility class for constants used for email routing.
 */
public final class EmailConstants {
    // Email Constants
    public static final String ENABLE_OUTBOUND_EMAILS = "ENABLE_OUTBOUND_EMAILS";
    public static final String EMAIL_RETRY_ATTEMPTS = "EMAIL_RETRY_ATTEMPTS";
    public static final String EMAIL_RETRY_EXPONENTIAL_BACK_OFF = "EMAIL_RETRY_EXPONENTIAL_BACK_OFF";
    public static final String EMAIL_RETRY_INTERVAL = "EMAIL_RETRY_INTERVAL";
    public static final String EMAIL_FROM_ADDRESS = "EMAIL_FROM_ADDRESS";

    // SMTP Configurations
    public static final String SMTP_HOST = "mail.smtp.host";
    public static final String RELAY_HOST = "emailrelay";

    // Digital signing constants
    public static final String DIGITAL_SIGNATURE_PATH = "/ericsson/fm/data/certs/FM_EMAIL_DIGITAL_SIGNATURE.jks";
    public static final String STORE_ALIAS = "jbossVault";
    public static final String JKS = "JKS";
    public static final String BC = "BC";
    public static final String DSA = "DSA";
    public static final String SHA1withDSA = "SHA1withDSA";
    public static final String MD5withRSA = "MD5withRSA";
    public static final String FM_KEYSTORE_PWD = "FM_ROUTING_KEYSTORE_PASSWORD_PROPERTY";

    private EmailConstants() {}
}
