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

import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.PAUSE_EVENT_VALUE;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Inject;
import javax.inject.Named;

import com.ericsson.oss.itpf.common.Controllable;
import com.ericsson.oss.itpf.common.event.ControlEvent;
import com.ericsson.oss.itpf.common.event.handler.AbstractEventHandler;
import com.ericsson.oss.itpf.common.event.handler.EventInputHandler;
import com.ericsson.oss.services.fm.alarmroutemanagement.models.AlarmRoute;
import com.ericsson.oss.services.fm.alarmroutingservice.cache.AlarmRoutesHolder;
import com.ericsson.oss.services.fm.models.processedevent.ProcessedAlarmEvent;

/**
 * Class used for handling PolicyState of AlarmRoute.
 */
@Named("policyStateHandler")
public class PolicyStateHandler extends AbstractEventHandler implements EventInputHandler, Controllable {

    private static final boolean DESTROYED = false;
    private boolean paused;

    @Inject
    private AlarmRoutesHolder alarmRoutesHolder;

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

        final ProcessedAlarmEvent processedAlarmEvent = (ProcessedAlarmEvent) flowInputEvent;
        log.debug("Alarm received to PolicyStateHandler {}", processedAlarmEvent);
        final AlarmRouteHandlerEvent alarmRouteHandlerEvent = new AlarmRouteHandlerEvent();
        final List<AlarmRoute> alarmRoutes = new ArrayList<AlarmRoute>(100);
        final Map<Long, AlarmRoute> alrmrMap = alarmRoutesHolder.getAlarmRoutes();
        final Iterator<Entry<Long, AlarmRoute>> routeIterator = alrmrMap.entrySet().iterator();
        while (routeIterator.hasNext()) {
            final Entry<Long, AlarmRoute> alarmRouteEntry = routeIterator.next();
            final AlarmRoute alarmRoute = alarmRouteEntry.getValue();
            if (alarmRoute.getEnablePolicy()) {
                alarmRoutes.add(alarmRoute);
            }
        }

        if (!alarmRoutes.isEmpty()) {
            alarmRouteHandlerEvent.setAlarmRoutes(alarmRoutes);
            alarmRouteHandlerEvent.setProcessedAlarmEvent(processedAlarmEvent);
            log.debug("The alarmRouteHandlerEvent created is: {}", alarmRouteHandlerEvent);
            try {
                sendToAllSubscribers(alarmRouteHandlerEvent);
            } catch (final Exception exception) {
                log.error("Exception occurred while sending AlarmRouteHandlerEvent : ", exception);
            }
        } else {
            log.debug("There are no enabled routes,the alarm not processed");
        }
    }

    @Override
    protected void doInit() {
    }

}
