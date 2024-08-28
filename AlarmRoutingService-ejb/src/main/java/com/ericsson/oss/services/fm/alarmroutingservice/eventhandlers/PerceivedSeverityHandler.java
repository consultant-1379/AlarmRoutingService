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

import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.DELIMITER_COMMA;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.PAUSE_EVENT_VALUE;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.inject.Named;

import com.ericsson.oss.itpf.common.Controllable;
import com.ericsson.oss.itpf.common.event.ControlEvent;
import com.ericsson.oss.itpf.common.event.handler.AbstractEventHandler;
import com.ericsson.oss.itpf.common.event.handler.EventInputHandler;
import com.ericsson.oss.services.fm.alarmroutemanagement.models.AlarmRoute;
import com.ericsson.oss.services.fm.models.processedevent.ProcessedAlarmEvent;

/**
 * This handler receives {@link AlarmRouteHandlerEvent} and processes the event and delegate to next event handler of EPS flow.
 */
@Named("perceivedSeverityHandler")
public class PerceivedSeverityHandler extends AbstractEventHandler implements EventInputHandler, Controllable {

    private static final boolean DESTROYED = false;
    private boolean paused;

    @Override
    public void react(final ControlEvent controlEvent) {
        if (controlEvent.getType() == PAUSE_EVENT_VALUE) {
            log.debug("Asked to pause - will do that");
            paused = true;
        }
    }

    @Override
    public void onEvent(final Object flowInputEvent) {
        if (paused) {
            return;
        }
        if (DESTROYED) {
            throw new IllegalStateException("Component was already destroyed - should not be invoked again. Received event is " + flowInputEvent);
        }
        final AlarmRouteHandlerEvent alarmRouteHandlerEvent = (AlarmRouteHandlerEvent) flowInputEvent;
        final ProcessedAlarmEvent processedAlarmEvent = alarmRouteHandlerEvent.getProcessedAlarmEvent();
        final List<AlarmRoute> alarmRoutes = alarmRouteHandlerEvent.getAlarmRoutes();
        final List<AlarmRoute> perceivedSeverityMatchedAlarmRoutes = new ArrayList<AlarmRoute>();
        try {
            for (final AlarmRoute alarmRoute : alarmRoutes) {
                if (null == alarmRoute.getPerceivedSeverity()) {
                    perceivedSeverityMatchedAlarmRoutes.add(alarmRoute);
                } else {
                    final boolean isValidSeverity = checkPresentSeverity(processedAlarmEvent.getPresentSeverity().toString(),
                            alarmRoute.getPerceivedSeverity());
                    if (isValidSeverity) {
                        perceivedSeverityMatchedAlarmRoutes.add(alarmRoute);
                    }
                }
            }
            if (!perceivedSeverityMatchedAlarmRoutes.isEmpty()) {
                alarmRouteHandlerEvent.setAlarmRoutes(perceivedSeverityMatchedAlarmRoutes);
                log.debug("Route's found for given Alarm  for Percieved Severity  Count {}", perceivedSeverityMatchedAlarmRoutes.size());
                sendToAllSubscribers(alarmRouteHandlerEvent);
            } else {
                log.debug("No Route found for Percieved severity found for given Alarm");
            }
        } catch (final Exception exception) {
            log.error("Exception occurred while processing AlarmRouteHandlerEvent : ", exception);
        }
    }

    private boolean checkPresentSeverity(final String presentSeverity, final String perceivedSeverity) {
        final boolean returnFlag = false;
        final StringTokenizer severityTokenizer = new StringTokenizer(perceivedSeverity, DELIMITER_COMMA);
        while (severityTokenizer.hasMoreElements()) {
            if (presentSeverity.equalsIgnoreCase(severityTokenizer.nextElement().toString())) {
                return true;
            }
        }
        return returnFlag;
    }

    @Override
    protected void doInit() {
    }
}
