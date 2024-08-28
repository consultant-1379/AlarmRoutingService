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

package com.ericsson.oss.services.fm.alarmroutingservice.eventhandlers;

import java.util.Collections;
import java.util.List;

import com.ericsson.oss.services.fm.alarmroutemanagement.models.AlarmRoute;
import com.ericsson.oss.services.fm.models.processedevent.ProcessedAlarmEvent;

/**
 * The event used for passing the alarm route list and processed alarm event received for auto acknowledgement among the different handlers.
 */
public class AlarmRouteHandlerEvent {
    private List<AlarmRoute> alarmRoutes = Collections.emptyList();
    private ProcessedAlarmEvent processedAlarmEvent;

    public List<AlarmRoute> getAlarmRoutes() {
        return alarmRoutes;
    }

    public void setAlarmRoutes(final List<AlarmRoute> routes) {
        this.alarmRoutes = routes;
    }

    public ProcessedAlarmEvent getProcessedAlarmEvent() {
        return processedAlarmEvent;
    }

    public void setProcessedAlarmEvent(final ProcessedAlarmEvent processedAlarmEvent) {
        this.processedAlarmEvent = processedAlarmEvent;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("AlarmRouteHandlerEvent [alarmRoutes=").append(alarmRoutes).append(", processedAlarmEvent=").append(processedAlarmEvent)
                .append("]");
        return builder.toString();
    }

}
