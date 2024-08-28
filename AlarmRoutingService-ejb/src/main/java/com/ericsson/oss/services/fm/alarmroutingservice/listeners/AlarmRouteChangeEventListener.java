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

package com.ericsson.oss.services.fm.alarmroutingservice.listeners;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.itpf.datalayer.dps.notification.DpsNotificationConfiguration;
import com.ericsson.oss.itpf.datalayer.dps.notification.event.DpsDataChangedEvent;
import com.ericsson.oss.itpf.sdk.eventbus.annotation.Consumes;
import com.ericsson.oss.services.fm.alarmroutingservice.route.processors.AlarmRouteDpsEventProcessor;

/**
 * Bean responsible for listening the AlarmRoutePolicy created or updated or deleted events from dps notification topic.
 */
@ApplicationScoped
public class AlarmRouteChangeEventListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(AlarmRouteChangeEventListener.class);
    // AlarmRoutePolicy is the model name and its main PO for all the alarm routes.
    private static final String ALARM_ROUTE_POLICY_FILTER = "type='AlarmRoutePolicy'";
    // EmailDetails model is an association PO for AlarmRoutePolicy PO.
    private static final String EMAIL_DETAILS_FILTER = "type='EmailDetails'";
    // FileDetails model is an association PO for AlarmRoutePolicy PO.
    private static final String FILE_DETAILS_FILTER = "type='FileDetails'";
    // prepared for listening multiple types of alarm route associations.
    private static final String FILTER_STRING = ALARM_ROUTE_POLICY_FILTER + " OR " + EMAIL_DETAILS_FILTER + " OR " + FILE_DETAILS_FILTER;

    @Inject
    private AlarmRouteDpsEventProcessor alarmRouteEventProcessor;

    /**
     * Listens for DpsDataChangedEvent and updates the local cache.
     * @param dpsAttributeNotificationEvent
     *            dpsAttributeNotificationEvent received from DPS topic
     */
    public void listenAlarmRouteEvents(@Observes @Consumes(endpoint = DpsNotificationConfiguration.DPS_EVENT_NOTIFICATION_CHANNEL_URI,
            filter = FILTER_STRING) final DpsDataChangedEvent dpsAttributeNotificationEvent) {
        LOGGER.debug("ModeledDpsAttributeNotificationEvent received {}", dpsAttributeNotificationEvent);
        alarmRouteEventProcessor.processAlarmRouteDpsEvent(dpsAttributeNotificationEvent);
    }
}
