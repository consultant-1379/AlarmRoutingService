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
package com.ericsson.oss.services.fm.alarmroutingservice.route.processors;

import static com.ericsson.oss.services.fm.alarmroutemanagement.models.AlarmRoute.SUBJECT;
import static com.ericsson.oss.services.fm.alarmroutemanagement.models.AlarmRoute.TO_ADDRESS;
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
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.ericsson.oss.itpf.sdk.core.retry.RetryManager;
import com.ericsson.oss.itpf.sdk.recording.SystemRecorder;
import com.ericsson.oss.services.fm.alarmroutemanagement.models.AlarmRoute;
import com.ericsson.oss.services.fm.alarmroutemanagement.models.AlarmRoute.RouteSubordinateObjects;
import com.ericsson.oss.services.fm.alarmroutingservice.cache.AlarmRouteAssociationData;
import com.ericsson.oss.services.fm.alarmroutingservice.cache.AlarmRoutesHolder;
import com.ericsson.oss.services.fm.alarmroutingservice.configuration.ConfigurationChangeListener;
import com.ericsson.oss.services.fm.alarmroutingservice.eventhandlers.AlarmRouteHandlerEvent;
import com.ericsson.oss.services.fm.alarmroutingservice.eventhandlers.SignedEmailHandler;
import com.ericsson.oss.services.fm.models.processedevent.ProcessedAlarmEvent;
import com.ericsson.oss.services.fm.models.processedevent.ProcessedEventSeverity;
import com.ericsson.oss.services.models.alarm.RouteType;

@RunWith(MockitoJUnitRunner.class)
public class EmailSenderTest {

    @InjectMocks
    private final EmailSender emailSender = new EmailSender();

    @Mock
    private ConfigurationChangeListener configurationsChangeListener;

    @Mock
    private SystemRecorder systemRecorder;

    @Mock
    private RetryManager retryManager;

    @Mock
    private AlarmRoutesHolder alarmRouteHolder;

    @Mock
    private SignedEmailHandler signedEmailHandler;

    @Mock
    private AlarmRouteHandlerEvent alarmRouteHandlerEvent;

    @Mock
    private MimeMessage mimeMessage;

    public static final String FDN = "MeContext=LTE02ERBS0000A5,ManagedElement=1,ENodeBFunction=1";
    @Mock
    private AlarmRoute emailRoute;
    @Mock
    private ProcessedAlarmEvent processedAlarmEvent;
    @Mock
    private AlarmRouteAssociationData alarmRouteAssociationData;

    private Map<String, Object> associationData;

    @Before
    public void set_Up_Data() {

        alarmRouteHandlerEvent = new AlarmRouteHandlerEvent();
        List<String> spList = new ArrayList<>();
        List<String> pcList = new ArrayList<>();
        List<String> etList = new ArrayList<>();

        emailRoute = new AlarmRoute();
        emailRoute.setName("TestCreate");
        emailRoute.setObjectOfReference(FDN);
        emailRoute.setDescription("testdescription");
        emailRoute.setPerceivedSeverity("CRITICAL");
        emailRoute.setBeginTime("00:00:00");
        emailRoute.setEnablePolicy(true);
        emailRoute.setEndTime("23:59:59");
        emailRoute.setOutputType("email");
        emailRoute.setDays("MONDAY");
        emailRoute.setSubordinateType(RouteSubordinateObjects.All_SUBORDINATES);
        spList.add("RAIN");
        pcList.add("test_probablecause");
        etList.add("test_eventtype");
        emailRoute.setSpecificProblem(spList);
        emailRoute.setEventType(etList);
        emailRoute.setProbableCause(pcList);
        emailRoute.setRouteId(1234L);
        emailRoute.setRouteType(RouteType.EMAIL);

        associationData = new HashMap<String, Object>();
        final List<String> toAddresses = new ArrayList<String>();
        toAddresses.add("test@ericsson.com");
        associationData.put(TO_ADDRESS, toAddresses);
        associationData.put(SUBJECT, "test mail");
        alarmRouteAssociationData.updateAssociationAttributes(associationData);
        alarmRouteHolder.addAlarmRouteAssociation(1234L, alarmRouteAssociationData);

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
        alarmRoutes.add(emailRoute);
        alarmRouteHandlerEvent.setAlarmRoutes(alarmRoutes);
        alarmRouteHandlerEvent.setProcessedAlarmEvent(processedAlarmEvent);

    }

    @Test
    public void test_SendEmail() throws Exception {
        when(alarmRouteHolder.getAlarmRouteAssociation(1234L)).thenReturn(alarmRouteAssociationData);
        final List<String> toAddresses = new ArrayList<String>();
        toAddresses.add("test@ericsson.com");
        associationData.put(TO_ADDRESS, toAddresses);
        associationData.put(SUBJECT, "test mail");
        when(alarmRouteAssociationData.getAssociationAttributes()).thenReturn(associationData);
        when(configurationsChangeListener.getEmailRetryAttempts()).thenReturn(3);
        when(configurationsChangeListener.getEmailRetryInterval()).thenReturn(500);
        when(configurationsChangeListener.getEmailRetryExponentialBackOff()).thenReturn(1.0);
        when(configurationsChangeListener.getEmailFromAddress()).thenReturn("enmfaultmanagement@ericsson.com");
        when(signedEmailHandler.getSignedMessage(mimeMessage)).thenReturn(mimeMessage);
        emailSender.sendEmail(emailRoute, processedAlarmEvent);
    }

}
