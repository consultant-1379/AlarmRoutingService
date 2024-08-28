/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2016
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/

package com.ericsson.oss.services.fm.alarmroutingservice.eventhandlers;

import static com.ericsson.oss.services.fm.alarmroutemanagement.models.AlarmRoute.EMAIL;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.PAUSE_EVENT_VALUE;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.itpf.common.Controllable;
import com.ericsson.oss.itpf.common.event.ControlEvent;
import com.ericsson.oss.itpf.common.event.handler.AbstractEventHandler;
import com.ericsson.oss.itpf.common.event.handler.EventInputHandler;
import com.ericsson.oss.services.fm.alarmroutemanagement.models.AlarmRoute;
import com.ericsson.oss.services.fm.alarmroutingservice.configuration.ConfigurationChangeListener;
import com.ericsson.oss.services.fm.alarmroutingservice.route.processors.EmailSender;
import com.ericsson.oss.services.fm.models.processedevent.ProcessedAlarmEvent;
import com.ericsson.oss.services.models.alarm.RouteType;

/**
 * EmailClientHandler is used to send email to the destination addresses along with email route matched alarm as email body.
 */
@Named("emailClientHandler")
public class EmailClientHandler extends AbstractEventHandler implements EventInputHandler, Controllable {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailClientHandler.class);
    private static final boolean DESTROYED = false;
    private boolean paused;

    @Inject
    private ConfigurationChangeListener configurationsChangeListener;

    @Inject
    private EmailSender emailProcesssor;

    @Override
    public void react(final ControlEvent controlEvent) {
        if (controlEvent.getType() == PAUSE_EVENT_VALUE) {
            LOGGER.debug("Asked to pause - will do that");
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
        final List<AlarmRoute> emailRoutes = new ArrayList<AlarmRoute>();
        for (final AlarmRoute alarmRoute : alarmRoutes) {
            if (EMAIL.equalsIgnoreCase(alarmRoute.getOutputType()) || (RouteType.EMAIL.equals(alarmRoute.getRouteType()))) {
                emailRoutes.add(alarmRoute);
            }
        }
        if (!emailRoutes.isEmpty()) {
            if (configurationsChangeListener.isEnableOutBoundEmails()) { // checking, email feature is enabled/disable before
                // send email to external clients.

                LOGGER.debug("ProcessedAlarmEvent: {} and alarmroutes list: {}", processedAlarmEvent, emailRoutes);
                for (final AlarmRoute emailRoute : emailRoutes) {
                    emailProcesssor.sendEmail(emailRoute, processedAlarmEvent);
                }
            } else {
                LOGGER.debug("Email feature is not Enabled,Hence Emails will not be sent.");
            }
        }
    }

    @Override
    protected void doInit() {
    }
}
