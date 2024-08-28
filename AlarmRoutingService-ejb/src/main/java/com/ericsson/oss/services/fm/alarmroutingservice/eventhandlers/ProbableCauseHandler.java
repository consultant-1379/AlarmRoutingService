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
import java.util.List;

import javax.inject.Named;

import com.ericsson.oss.itpf.common.Controllable;
import com.ericsson.oss.itpf.common.event.ControlEvent;
import com.ericsson.oss.itpf.common.event.handler.AbstractEventHandler;
import com.ericsson.oss.itpf.common.event.handler.EventInputHandler;
import com.ericsson.oss.services.fm.alarmroutemanagement.models.AlarmRoute;
import com.ericsson.oss.services.fm.alarmroutingservice.util.MetaDataProcessor;
import com.ericsson.oss.services.fm.models.processedevent.ProcessedAlarmEvent;

/**
 * This handler receives {@link AlarmRouteHandlerEvent} and processes the event and delegate to next event handler of EPS flow.
 */
@Named("probableCauseHandler")
public class ProbableCauseHandler extends AbstractEventHandler implements EventInputHandler, Controllable {

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

        final List<AlarmRoute> probableCauseMatchedAlarmRoutes = new ArrayList<AlarmRoute>();
        try {
            for (final AlarmRoute alarmRoute : alarmRoutes) {
                if (null == processedAlarmEvent.getProbableCause() || null == alarmRoute.getProbableCause()
                        || alarmRoute.getProbableCause().isEmpty()
                        || processedAlarmEvent.getProbableCause().isEmpty()) {
                    probableCauseMatchedAlarmRoutes.add(alarmRoute);
                } else {
                    if (MetaDataProcessor.isAttributePresent(processedAlarmEvent.getProbableCause(), alarmRoute.getProbableCause())) {
                        probableCauseMatchedAlarmRoutes.add(alarmRoute);
                    }
                }
            }
            if (!probableCauseMatchedAlarmRoutes.isEmpty()) {
                alarmRouteHandlerEvent.setAlarmRoutes(probableCauseMatchedAlarmRoutes);
                log.debug("The incoming alarm matched with the Probable cause filtered alarmRoutes count :{} ",
                        probableCauseMatchedAlarmRoutes.size());
                sendToAllSubscribers(alarmRouteHandlerEvent);
            } else {
                log.debug("The incoming alarm not matched with any Probable Cause filtered alarmRoute.");
            }
        } catch (final Exception exception) {
            log.error("Exception occurred while processing AlarmRouteHandlerEvent : ", exception);
        }
    }

    @Override
    protected void doInit() {
    }
}
