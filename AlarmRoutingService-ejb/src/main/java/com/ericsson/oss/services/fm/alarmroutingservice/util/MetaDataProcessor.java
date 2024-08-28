/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2015
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/

package com.ericsson.oss.services.fm.alarmroutingservice.util;

import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.DELIMITER_COMMA;

import java.util.List;

/**
 * MetaDataProcessor verifies whether attributeValue available in the attributes list and also verifies objectOfReferrence in list of
 * objectOfReferrences list.
 */
public final class MetaDataProcessor {

    private MetaDataProcessor() {
    }

    /**
     * Validates if the attribute present in the give attributes in string format.
     * @param attributeValue
     *            The specific problem or probable cause or event type received in processed alarm event.
     * @param attributes
     *            The objectOfReference or specific problem or probable cause or event type attribute value present in alarm route with "," as
     *            delimiter.
     * @return true if the attributeValue exists in attributes.
     */
    public static boolean isAttributePresent(final String attributeValue, final String attributes) {
        boolean attributePresent = false;
        final String[] alarmAttributes = attributes.split(DELIMITER_COMMA);
        for (final String attribute : alarmAttributes) {
            if (attributeValue.equals(attribute)) {
                attributePresent = true;
            }
        }
        return attributePresent;
    }

    /**
     * Checks if the object of reference from alarm present in alarm route.
     * @param objectOfReference
     *            The objectOfReference received in processed alarm event.
     * @param objectOfReferences
     *            The list objectOfReferences present in alarm route.
     * @return boolean
     */
    public static boolean isAttributePresent(final String objectOfReference, final List<String> objectOfReferences) {
        return objectOfReferences.contains(objectOfReference);
    }

}
