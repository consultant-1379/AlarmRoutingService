/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2018
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
import java.util.Map;

/**
 * A response class that encapsulates the response containing job id and status of alarm export operation for an alarm export request.<br>
 */
public class AlarmRouteFileExportResponse implements Serializable {

    private static final long serialVersionUID = 9086481497242904711L;
    private Map<String, String> responseMap;

    public Map<String, String> getResponseMap() {
        return responseMap;
    }

    public void setResponseMap(final Map<String, String> responseMap) {
        this.responseMap = responseMap;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("RouteFileExportResponse [responseMap=").append(responseMap).append("]");
        return builder.toString();
    }
}
