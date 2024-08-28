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

import static com.ericsson.oss.services.fm.alarmroutemanagement.models.AlarmRoute.SUBJECT;
import static com.ericsson.oss.services.fm.alarmroutemanagement.models.AlarmRoute.TO_ADDRESS;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.internet.MimeMessage;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.ericsson.oss.itpf.common.event.ControlEvent;
import com.ericsson.oss.services.fm.alarmroutemanagement.models.AlarmRoute;
import com.ericsson.oss.services.fm.alarmroutemanagement.models.AlarmRoute.RouteSubordinateObjects;
import com.ericsson.oss.services.fm.alarmroutingservice.configuration.ConfigurationChangeListener;
import com.ericsson.oss.services.fm.alarmroutingservice.route.processors.EmailSender;
import com.ericsson.oss.services.fm.models.processedevent.ProcessedAlarmEvent;
import com.ericsson.oss.services.fm.models.processedevent.ProcessedEventSeverity;
import com.ericsson.oss.services.models.alarm.RouteType;

@RunWith(MockitoJUnitRunner.class)
public class EmailClientHandlerTest {

    @InjectMocks
    private EmailClientHandler emailClientHandler;

    @Mock
    private ConfigurationChangeListener configurationsChangeListener;

    @Mock
    private EmailSender emailSender;

    @Mock
    private MimeMessage mimeMessage;
    @Mock
    private AlarmRouteHandlerEvent alarmRouteHandlerEvent;

    public static final String FDN = "MeContext=LTE02ERBS0000A5,ManagedElement=1,ENodeBFunction=1";
    private AlarmRoute alarmRoute;
    private ProcessedAlarmEvent processedAlarmEvent;

    @Before
    public void set_Up_Data() {

        alarmRouteHandlerEvent = new AlarmRouteHandlerEvent();
        List<String> spList = new ArrayList<>();
        List<String> pcList = new ArrayList<>();
        List<String> etList = new ArrayList<>();

        alarmRoute = new AlarmRoute();
        alarmRoute.setName("TestCreate");
        alarmRoute.setObjectOfReference(FDN);
        alarmRoute.setDescription("testdescription");
        alarmRoute.setPerceivedSeverity("CRITICAL");
        alarmRoute.setBeginTime("00:00:00");
        alarmRoute.setEnablePolicy(true);
        alarmRoute.setEndTime("23:59:59");
        alarmRoute.setOutputType("email");
        alarmRoute.setDays("MONDAY");
        alarmRoute.setSubordinateType(RouteSubordinateObjects.All_SUBORDINATES);
        spList.add("RAIN");
        pcList.add("test_probablecause");
        etList.add("test_eventtype");
        alarmRoute.setSpecificProblem(spList);
        alarmRoute.setEventType(etList);
        alarmRoute.setProbableCause(pcList);
        alarmRoute.setRouteId(102L);
        alarmRoute.setRouteType(RouteType.EMAIL);

        final Map<String, Object> associationData = new HashMap<String, Object>();
        final List<String> toAddresses = new ArrayList<String>();
        toAddresses.add("test@ericsson.com");
        associationData.put(TO_ADDRESS, toAddresses);
        associationData.put(SUBJECT, "test mail");

        processedAlarmEvent = new ProcessedAlarmEvent();
        processedAlarmEvent.setAckOperator("Ericcsson");
        processedAlarmEvent.setEventPOId((long) 2126549842);
        processedAlarmEvent.setObjectOfReference(FDN);
        processedAlarmEvent.setProbableCause("test_probablecause");
        processedAlarmEvent.setEventType("test_eventtype");
        processedAlarmEvent.setSpecificProblem("RAIN");
        processedAlarmEvent.setFdn(FDN);
        processedAlarmEvent.setPresentSeverity(ProcessedEventSeverity.CRITICAL);
        processedAlarmEvent.setInsertTime(new Date());
        processedAlarmEvent.setEventTime(new Date());

        final List<AlarmRoute> alarmRoutes = new ArrayList<AlarmRoute>();
        alarmRoutes.add(alarmRoute);
        alarmRouteHandlerEvent.setAlarmRoutes(alarmRoutes);
        alarmRouteHandlerEvent.setProcessedAlarmEvent(processedAlarmEvent);

    }

    @Test
    public void testReact() {
        final ControlEvent controlEvent = new ControlEvent(42);
        emailClientHandler.react(controlEvent);
        emailClientHandler.onEvent(alarmRouteHandlerEvent);
    }

    @Test
    public void testOnEvent() {
        final ControlEvent controlEvent = new ControlEvent(12);
        emailClientHandler.react(controlEvent);
        when(configurationsChangeListener.isEnableOutBoundEmails()).thenReturn(true);
        emailClientHandler.onEvent(alarmRouteHandlerEvent);
        verify(emailSender).sendEmail((AlarmRoute) Matchers.anyObject(), (ProcessedAlarmEvent) Matchers.anyObject());
    }
}
