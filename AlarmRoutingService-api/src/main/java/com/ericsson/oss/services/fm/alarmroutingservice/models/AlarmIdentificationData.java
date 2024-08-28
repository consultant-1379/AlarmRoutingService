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

package com.ericsson.oss.services.fm.alarmroutingservice.models;

import java.io.Serializable;

/**
 * POJO for sending the response to Alarm Identification Widget.
 */
public class AlarmIdentificationData implements Serializable {
    private static final long serialVersionUID = 113695272190610297L;

    /**
     * a unique ID which serves as an index.
     */
    private int itemId;

    /**
     * contains the attribute value.
     */
    private String value;
    /**
     * holds whether the attribute value is of type user defined (true) or system defined (false).
     */
    private boolean isUserDefined;
    /**
     * Used for differentiating the highlighted word that is queried.
     */
    private String query;

    public String getValue() {
        return value;
    }

    public void setValue(final String value) {
        this.value = value;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(final int itemId) {
        this.itemId = itemId;
    }

    public boolean isUserDefined() {
        return isUserDefined;
    }

    public void setUserDefined(final boolean isUserDefined) {
        this.isUserDefined = isUserDefined;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(final String query) {
        this.query = query;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AlarmIdentificationData alarmIdentificationData = (AlarmIdentificationData) obj;
        if (value == null) {
            if (alarmIdentificationData.value != null) {
                return false;
            }
        } else if (!value.equals(alarmIdentificationData.value)) {
            return false;
        }
        return true;
    }
}
