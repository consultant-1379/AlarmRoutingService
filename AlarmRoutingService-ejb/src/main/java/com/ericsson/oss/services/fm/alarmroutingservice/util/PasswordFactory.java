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

import static com.ericsson.oss.services.fm.alarmroutingservice.util.EmailConstants.FM_KEYSTORE_PWD;

import javax.enterprise.context.ApplicationScoped;

/**
 * PasswordFactory reads password from JBoss vault for the @ KeyStore} object to support Digital signing of email message.
 */
@ApplicationScoped
public class PasswordFactory {
    private String password;

    /**
     * Method reads password information from System properties added by the JBoss vault service.
     * @return @ String} .
     */
    public String getPassword() {
        if (password == null) {
            password = System.getProperty(FM_KEYSTORE_PWD);
            return password;
        }
        return password;
    }
}
