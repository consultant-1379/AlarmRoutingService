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

package com.ericsson.oss.services.fm.alarmroutingservice.webpush;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.services.fm.alarmroutingservice.models.AlarmRouteFileExportResponse;

/**
 * Listens for export event {@link AlarmRouteFileExportResponse} and broadcasts event to webpush.
 */
@ApplicationScoped
public class AlarmRouteFileExportResponseListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(AlarmRouteFileExportResponseListener.class);

    @Inject
    private WebPushResponseSender webPushResponseSender;

    public void listenExportResponseEvent(@Observes final AlarmRouteFileExportResponse routeFileExportResponse) {
        LOGGER.debug("Received AlarmRouteFileExportResponse Webpush Data CDI Event: {}", routeFileExportResponse);
        webPushResponseSender.broadcastMessage(routeFileExportResponse);
    }
}
