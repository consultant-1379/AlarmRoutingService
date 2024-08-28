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

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import com.ericsson.oss.itpf.datalayer.dps.notification.event.DpsObjectCreatedEvent;
import com.ericsson.oss.services.fm.alarmroutingservice.listeners.AlarmRouteChangeEventListener;
import com.ericsson.oss.services.fm.alarmroutingservice.route.processors.AlarmRouteDpsEventProcessor;

@RunWith(MockitoJUnitRunner.class)
public class AlarmRouteChangeEventListenerTest {

    @InjectMocks
    private final AlarmRouteChangeEventListener eventListener = new AlarmRouteChangeEventListener();

    @Mock
    private AlarmRouteDpsEventProcessor alarmRouteEventProcessor;

    @Mock
    private DpsObjectCreatedEvent routeCreatedEvent;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testlistenAlarmRouteEvents() {
        eventListener.listenAlarmRouteEvents(routeCreatedEvent);
        verify(alarmRouteEventProcessor, times(1)).processAlarmRouteDpsEvent(routeCreatedEvent);
    }

}
