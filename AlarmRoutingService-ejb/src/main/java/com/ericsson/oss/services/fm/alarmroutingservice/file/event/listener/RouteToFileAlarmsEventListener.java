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

package com.ericsson.oss.services.fm.alarmroutingservice.file.event.listener;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.itpf.sdk.eventbus.model.annotation.Modeled;
import com.ericsson.oss.services.fm.alarmroutingservice.file.event.processor.RouteToFileAlarmsEventProcessor;
import com.ericsson.oss.services.fm.models.RouteToFileAlarmsEvent;

/**
 * Class responsible for listening the {@link RouteToFileAlarmsEvent}.
 */
@ApplicationScoped
public class RouteToFileAlarmsEventListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(RouteToFileAlarmsEventListener.class);

    @Inject
    private RouteToFileAlarmsEventProcessor routeToFileAlarmsEventProcessor;

    /**
     * Listens for RouteToFileAlarmsEvent.
     * @param routeToFileAlarmsEvent
     *            event containing alarms to be written to file.
     */
    public void listenAlarmRouteEvents(@Observes @Modeled final RouteToFileAlarmsEvent routeToFileAlarmsEvent) {
        LOGGER.debug("Received {}", routeToFileAlarmsEvent);
        routeToFileAlarmsEventProcessor.processRouteToFileAlarmsEvent(routeToFileAlarmsEvent);
    }

}
