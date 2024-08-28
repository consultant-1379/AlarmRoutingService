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

package com.ericsson.oss.services.fm.alarmroutingservice.file;

import java.util.HashMap;
import java.util.Map;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.services.fm.alarmroutingservice.api.RouteFileExport;
import com.ericsson.oss.services.fm.alarmroutingservice.models.AlarmRouteFileExportResponse;

/**
 * Class provides implementation for alarm export operation for alarm monitor application.
 */
@Stateless
public class RouteFileExportImpl implements RouteFileExport {

    private static final Logger LOGGER = LoggerFactory.getLogger(RouteFileExportImpl.class);

    private static final String SUBSCRIPTION_ID = "subscriptionId";
    private static final String ROUTE_FILES_ZIP_NAME = "filePath";
    private static final String STATUS_MESSAGE = "statusMessage";

    @Inject
    private Event<AlarmRouteFileExportResponse> exportResponseEventSender;

    @Override
    public void sendRouteFileExportResponse(final String fileName, final String jobId, final String statusMessage) {
        final AlarmRouteFileExportResponse exportResponse = new AlarmRouteFileExportResponse();
        final Map<String, String> responseMap = new HashMap<String, String>();
        responseMap.put(SUBSCRIPTION_ID, jobId);
        responseMap.put(ROUTE_FILES_ZIP_NAME, fileName);
        responseMap.put(STATUS_MESSAGE, statusMessage);
        exportResponse.setResponseMap(responseMap);
        LOGGER.debug("Sending response for job {}, status {} fileName {}", jobId, statusMessage, fileName);
        exportResponseEventSender.fire(exportResponse);
    }
}
