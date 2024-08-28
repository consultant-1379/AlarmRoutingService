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

import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.ALARMROUTINGSERVICE;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.PAUSE_EVENT_VALUE;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;

import com.ericsson.oss.itpf.common.Controllable;
import com.ericsson.oss.itpf.common.event.ControlEvent;
import com.ericsson.oss.itpf.common.event.handler.AbstractEventHandler;
import com.ericsson.oss.itpf.common.event.handler.EventInputHandler;
import com.ericsson.oss.itpf.sdk.core.annotation.EServiceRef;
import com.ericsson.oss.services.alarm.action.service.api.AlarmActionService;
import com.ericsson.oss.services.alarm.action.service.model.AlarmAction;
import com.ericsson.oss.services.alarm.action.service.model.AlarmActionData;
import com.ericsson.oss.services.fm.models.processedevent.ProcessedAlarmEvent;

/**
 * This handler receives the list processed alarm events to be considered for auto acknowledgement from EPS batch component.
 */
@Named("autoAckHandler")
public class AutoAckHandler extends AbstractEventHandler implements EventInputHandler, Controllable {

    private static final boolean DESTROYED = false;
    private boolean paused;

    @EServiceRef
    private AlarmActionService alarmActionService;

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

        final List<ProcessedAlarmEvent> processedAlarmEvents = (List<ProcessedAlarmEvent>) flowInputEvent;
        processAutoAck(processedAlarmEvents);
    }

    @Override
    protected void doInit() {
    }

    /**
     * @param processedAlarmEvents
     *            The list of processed alarm events received for auto acknowledgement. All the list of events will be sent to alarm action service in
     *            alarm action data.
     */
    private void processAutoAck(final List<ProcessedAlarmEvent> processedAlarmEvents) {
        final List<Long> poIds = new ArrayList<Long>(processedAlarmEvents.size());
        for (final ProcessedAlarmEvent event : processedAlarmEvents) {
            poIds.add(event.getEventPOId());
        }
        final AlarmActionData alarmActionData = new AlarmActionData();
        alarmActionData.setOperatorName(ALARMROUTINGSERVICE);
        alarmActionData.setAlarmIds(poIds);
        alarmActionData.setAction(AlarmAction.ACK);

        log.debug("Request for AutoAck is ready with : {}", alarmActionData);
        alarmActionService.performAutoAck(alarmActionData);
    }
}
