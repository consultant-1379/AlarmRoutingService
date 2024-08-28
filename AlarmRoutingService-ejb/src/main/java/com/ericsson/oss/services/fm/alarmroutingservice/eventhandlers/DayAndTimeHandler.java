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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
@Named("dayAndTimeHandler")
public class DayAndTimeHandler extends AbstractEventHandler implements EventInputHandler, Controllable {

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
        final List<AlarmRoute> dayTimeMatchedAlarmRoutes = new ArrayList<AlarmRoute>();
        try {
            for (final AlarmRoute alarmRoute : alarmRoutes) {
                if (compareDayAndTime(processedAlarmEvent, alarmRoute)) {
                    dayTimeMatchedAlarmRoutes.add(alarmRoute);
                }
            }
            if (!dayTimeMatchedAlarmRoutes.isEmpty()) {
                alarmRouteHandlerEvent.setAlarmRoutes(dayTimeMatchedAlarmRoutes);
                log.debug("The incoming alarm matched with the Day and Time filtered alarmRoutes count :{} ", dayTimeMatchedAlarmRoutes.size());
                sendToAllSubscribers(alarmRouteHandlerEvent);
            } else {
                log.debug("The incoming alarm not matched with any Day and Time filter alarmRoute");
            }
        } catch (final Exception exception) {
            log.error("Exception occurred while processing AlarmRouteHandlerEvent : ", exception);
        }
    }

    @Override
    protected void doInit() {
    }

    private Boolean compareDayAndTime(final ProcessedAlarmEvent processedAlarmEvent, final AlarmRoute alarmRoute) {
        boolean isWithin = false;
        Date beginTime = null;
        Date endTime = null;
        Date eventTime = null;
        Boolean dayCheck = false;
        try {
            final DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
            final Calendar cal = Calendar.getInstance();
            cal.setTime(processedAlarmEvent.getEventTime());

            beginTime = dateFormat.parse(alarmRoute.getBeginTime());
            endTime = dateFormat.parse(alarmRoute.getEndTime());
            eventTime = dateFormat.parse(dateFormat.format(cal.getTime()));
            isWithin = eventTime.after(beginTime) && eventTime.before(endTime);
            if (eventTime.equals(beginTime) || eventTime.equals(endTime)) {
                isWithin = true;
            }

            cal.setTime(processedAlarmEvent.getEventTime());
            final SimpleDateFormat dateFormat1 = new SimpleDateFormat("EEEEE");
            final String dayOfWeek = dateFormat1.format(cal.getTime());

            final StringTokenizer st = new StringTokenizer(alarmRoute.getDays(), ",");

            if (st.countTokens() > 1) {
                while (st.hasMoreElements()) {
                    if (dayOfWeek.equalsIgnoreCase(st.nextElement().toString())) {
                        dayCheck = true;
                        break;
                    }
                }
            } else if (alarmRoute.getDays().equalsIgnoreCase(dayOfWeek)) {
                dayCheck = true;
            }

            log.debug("return isWithin value {}", isWithin);
        } catch (final Exception exception) {
            log.error("Exception in compareDayAndTime : ", exception);
        }

        return dayCheck && isWithin;
    }
}
