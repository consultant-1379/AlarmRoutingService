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

package com.ericsson.oss.services.fm.alarmroutingservice.eventhandlers;

import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.PAUSE_EVENT_VALUE;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;

import com.ericsson.oss.itpf.common.Controllable;
import com.ericsson.oss.itpf.common.event.ControlEvent;
import com.ericsson.oss.itpf.common.event.handler.AbstractEventHandler;
import com.ericsson.oss.itpf.common.event.handler.EventInputHandler;
import com.ericsson.oss.services.fm.alarmroutemanagement.models.AlarmRoute;
import com.ericsson.oss.services.fm.alarmroutingservice.file.cache.manager.AlarmFileRouteCacheManager;
import com.ericsson.oss.services.fm.alarmroutingservice.instrumentation.AlarmRouteCounters;
import com.ericsson.oss.services.fm.models.processedevent.ProcessedAlarmEvent;
import com.ericsson.oss.services.models.alarm.RouteType;

/**
 * Responsible for delegating alarms to get written to a file. Stores received alarm matching against a route with type as "file" in a clustered
 * cache.
 */
@Named("fileRouteHandler")
public class FileRouteHandler extends AbstractEventHandler implements EventInputHandler, Controllable {

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
        final List<AlarmRoute> sendToFileRoutes = new ArrayList<AlarmRoute>();
        for (final AlarmRoute alarmRoute : alarmRoutes) {
            if (RouteType.FILE.equals(alarmRoute.getRouteType())) {
                sendToFileRoutes.add(alarmRoute);
            }
        }
        if (!sendToFileRoutes.isEmpty()) {
            // TODO : Introduce instrumentation here to record number of alarms received to file route.
            log.debug("Number of Alarm Routes matched for alarm {} are {}", processedAlarmEvent, sendToFileRoutes.size());
            // Add alarms to Cache
            AlarmFileRouteCacheManager.getInstance().addToCache(processedAlarmEvent, sendToFileRoutes);
            AlarmRouteCounters.increasedAlarmCount(sendToFileRoutes.size());
        }
    }

    @Override
    protected void doInit() {
    }

}
