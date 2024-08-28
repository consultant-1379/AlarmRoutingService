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
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import com.ericsson.oss.services.alarm.action.service.api.AlarmActionService;
import com.ericsson.oss.services.alarm.action.service.model.AlarmActionData;
import com.ericsson.oss.services.fm.alarmroutingservice.eventhandlers.AutoAckHandler;
import com.ericsson.oss.services.fm.models.processedevent.ProcessedAlarmEvent;

@RunWith(MockitoJUnitRunner.class)
public class AutoAckHandlerTest {

    @InjectMocks
    private final AutoAckHandler autoAckHandler = new AutoAckHandler();

    @Mock
    private AlarmActionService alarmActionService;

    @Mock
    private ProcessedAlarmEvent processedAlarmEvent;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testOnEvent() {
        final List<ProcessedAlarmEvent> alarms = new ArrayList<ProcessedAlarmEvent>(1);
        alarms.add(processedAlarmEvent);
        autoAckHandler.onEvent(alarms);
        final String attributeValue = "aaa";
        final String[] metaDataAttributes = attributeValue.split(DELIMITER_COMMA);

        
        verify(alarmActionService, times(1)).performAutoAck((AlarmActionData) anyObject());
    }
}
