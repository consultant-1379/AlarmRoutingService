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

package com.ericsson.oss.services.fm.alarmroutingservice.impl;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.services.fm.alarmroutingservice.api.AlarmRouteFileExportHandler;
import com.ericsson.oss.services.fm.alarmroutingservice.models.AlarmRouteFileExportRequest;

/**
 * AlarmRouteFileExportHandlerImpl provides functionality to export route file and saved route file as a zip format file.
 */
@Stateless
public class AlarmRouteFileExportHandlerImpl implements AlarmRouteFileExportHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(AlarmRouteFileExportHandlerImpl.class);

    @Inject
    private AlarmRouteFileExportExecutor alarmRouteFileExportExecutor;

    @Override
    public boolean export(final AlarmRouteFileExportRequest alarmRouteFileExportRequest) {
        boolean requestAccepted = false;
        LOGGER.debug("Compress route file request received for routeName: {}", alarmRouteFileExportRequest.getRouteFileName());
        try {
            alarmRouteFileExportExecutor.createRouteExportRequestFile(alarmRouteFileExportRequest);
            requestAccepted = true;
        } catch (final Exception exception) {
            LOGGER.error("Exception occured in creating routeExportRequestFile : ", exception);
        }
        return requestAccepted;
    }

    @Override
    public boolean isExportInProgress(final AlarmRouteFileExportRequest alarmRouteFileExportRequest) {
        return alarmRouteFileExportExecutor.isExportInProgress(alarmRouteFileExportRequest);
    }

}
