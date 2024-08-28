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
import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.MANAGEMENT_SYSTEM;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.PAUSE_EVENT_VALUE;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;

import com.ericsson.oss.itpf.common.Controllable;
import com.ericsson.oss.itpf.common.event.ControlEvent;
import com.ericsson.oss.itpf.common.event.handler.AbstractEventHandler;
import com.ericsson.oss.itpf.common.event.handler.EventInputHandler;
import com.ericsson.oss.services.fm.alarmroutemanagement.models.AlarmRoute;
import com.ericsson.oss.services.fm.alarmroutemanagement.models.AlarmRoute.RouteSubordinateObjects;
import com.ericsson.oss.services.fm.models.processedevent.ProcessedAlarmEvent;

/**
 * This handler receives {@link AlarmRouteHandlerEvent} and processes the event and delegate to next event handler of EPS flow.
 */
@Named("sourceObjectHandler")
public class SourceObjectHandler extends AbstractEventHandler implements EventInputHandler, Controllable {

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
        final List<AlarmRoute> sourceObjectMatchedAlarmRoutes = new ArrayList<AlarmRoute>(alarmRoutes.size());
        try {
            for (final AlarmRoute alarmRouteData : alarmRoutes) {
                if (alarmRouteData.getFdns() != null && !alarmRouteData.getFdns().isEmpty()) {
                    if (RouteSubordinateObjects.All_SUBORDINATES == alarmRouteData.getSubordinateType()) {
                        if (alarmRouteData.getFdns().contains(processedAlarmEvent.getFdn())) {
                            sourceObjectMatchedAlarmRoutes.add(alarmRouteData);
                        }
                    } else if (RouteSubordinateObjects.NO_SUBORDINATES == alarmRouteData.getSubordinateType()) {
                        // NetworkElemnt=LTE01ERBS00001,NEtworkElement=LTE01ERBS00001
                        final List<String> fdns = alarmRouteData.getFdns();
                        final String[] oorSplit = processedAlarmEvent.getObjectOfReference().split(DELIMITER_COMMA);
                        // In case of noSub ordinates the fdn will be the OOR.
                        if (fdns.contains(processedAlarmEvent.getFdn()) && (oorSplit.length == 1)) {
                            sourceObjectMatchedAlarmRoutes.add(alarmRouteData);
                        }
                        if (processedAlarmEvent.getFdn().contains(MANAGEMENT_SYSTEM)) { // This part handles internal alarm as applicable.
                            if (alarmRouteData.getFdns().contains(processedAlarmEvent.getFdn())) {
                                sourceObjectMatchedAlarmRoutes.add(alarmRouteData);
                            }
                        }
                    }
                } else if (null == alarmRouteData.getFdns() || alarmRouteData.getFdns().isEmpty()
                        || processedAlarmEvent.getObjectOfReference().isEmpty()
                        || null == processedAlarmEvent.getObjectOfReference()) {
                    sourceObjectMatchedAlarmRoutes.add(alarmRouteData);
                }
            }
            alarmRouteHandlerEvent.setAlarmRoutes(sourceObjectMatchedAlarmRoutes);
            if (!sourceObjectMatchedAlarmRoutes.isEmpty()) {
                log.debug("The incoming alarm matched with the SourceObject filtered alarmRoutes count :{}", sourceObjectMatchedAlarmRoutes.size());
                sendToAllSubscribers(alarmRouteHandlerEvent);
            } else {
                log.debug("The incoming alarm not matched with any SourceObject filtered alarmRoute.");
            }
        } catch (final Exception exception) {
            log.error("Exception occurred while processing AlarmRouteHandlerEvent : ", exception);
        }
    }

    @Override
    protected void doInit() {
    }
}
