/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2017
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/

package com.ericsson.oss.services.fm.alarmroutingservice.file;

import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

/**
 * A FileResourceResource implementation , that writes the alarms to files in CSV format.
 */
@Stateless
@CsvFileResourceQualifier
public class CsvFileResource implements FileResource {

    /**
     * Method that writes the alarms to files in CSV format.
     */

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void delegate(final List<Map<String, Object>> alarms, final List<String> alarmAttributesNames, final String fileNameInRoute) {
        // TODO: This will be implemented in the next sprint
    }
}
