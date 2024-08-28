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

package com.ericsson.oss.services.fm.alarmroutingservice.cache;

import java.util.HashMap;
import java.util.Map;

/**
 * AlarmRouteAssociationData holds all the associations data with alarm route poid against association data.
 */
public class AlarmRouteAssociationData {

    private Map<String, Object> associationAttributes = new HashMap<String, Object>();

    public AlarmRouteAssociationData(final Map<String, Object> associationAttributes) {
        setAssociationAttributes(associationAttributes);
    }

    public Map<String, Object> getAssociationAttributes() {
        return associationAttributes;
    }

    private void setAssociationAttributes(final Map<String, Object> associationAttributes) {
        this.associationAttributes = associationAttributes;
    }

    public void updateAssociationAttributes(final Map<String, Object> associationAttributes) {
        this.associationAttributes.putAll(associationAttributes);
    }

    @Override
    public String toString() {
        return "AlarmRouteAssociationData [associationAttributes=" + associationAttributes + "]";
    }

}
