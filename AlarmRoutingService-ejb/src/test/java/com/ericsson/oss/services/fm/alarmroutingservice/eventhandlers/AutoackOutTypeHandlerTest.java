/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2012
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/

package com.ericsson.oss.services.fm.alarmroutingservice.eventhandlers;

import static com.ericsson.oss.services.fm.alarmroutemanagement.models.AlarmRoute.AUTO_ACK;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.ericsson.oss.itpf.common.event.ControlEvent;
import com.ericsson.oss.services.fm.alarmroutemanagement.models.AlarmRoute;
import com.ericsson.oss.services.fm.alarmroutemanagement.models.AlarmRoute.RouteSubordinateObjects;
import com.ericsson.oss.services.fm.models.processedevent.ProcessedAlarmEvent;
import com.ericsson.oss.services.fm.models.processedevent.ProcessedEventSeverity;
import com.ericsson.oss.services.models.alarm.RouteType;

@RunWith(MockitoJUnitRunner.class)
public class AutoackOutTypeHandlerTest {

    @Mock
    private AutoAckOutTypeHandler autoAckOutTypeHandler;

    @Mock
    private Iterator<javax.cache.Cache.Entry<Long, AlarmRoute>> iter;

    private static final String FDN = "MeContext=LTE02ERBS0000A5,ManagedElement=1,ENodeBFunction=1";

    AlarmRoute alarmRouteData = null;
    ProcessedAlarmEvent processedAlarmEvent = null;

    @Before
    public void set_Up_Data() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {

        autoAckOutTypeHandler = new AutoAckOutTypeHandler();
        List<String> spList = new ArrayList<>();
        List<String> pcList = new ArrayList<>();
        List<String> etList = new ArrayList<>();

        alarmRouteData = new AlarmRoute();
        alarmRouteData.setName("TestCreate");
        alarmRouteData.setObjectOfReference(FDN);
        alarmRouteData.setDescription("testdescription");
        alarmRouteData.setPerceivedSeverity("CRITICAL");
        alarmRouteData.setBeginTime("00:00:00");
        alarmRouteData.setEnablePolicy(true);
        alarmRouteData.setEndTime("23:59:59");
        alarmRouteData.setOutputType("Auto_Ack");
        alarmRouteData.setDays("Monday");
        alarmRouteData.setSubordinateType(RouteSubordinateObjects.All_SUBORDINATES);
        spList.add("RAIN");
        pcList.add("test_probablecause");
        etList.add("test_eventtype");
        alarmRouteData.setSpecificProblem(spList);
        alarmRouteData.setEventType(etList);
        alarmRouteData.setProbableCause(pcList);
        alarmRouteData.setRouteId(102L);

        processedAlarmEvent = new ProcessedAlarmEvent();
        processedAlarmEvent.setAckOperator("Ericcsson");
        processedAlarmEvent.setEventPOId((long) 2126549842);
        processedAlarmEvent.setObjectOfReference(FDN);
        processedAlarmEvent.setProbableCause("test_probablecause");
        processedAlarmEvent.setEventType("test_eventtype");
        // processedAlarmEvent.setProblemText("AtrTestP");
        processedAlarmEvent.setSpecificProblem("RAIN");
        processedAlarmEvent.setFdn(FDN);
        processedAlarmEvent.setPresentSeverity(ProcessedEventSeverity.CRITICAL);
        processedAlarmEvent.setInsertTime(new Date());

    }

    @Test
    public void testAutoAckHandler() throws SecurityException, IllegalAccessException, NoSuchFieldException {
        final AlarmRouteHandlerEvent exchangealarmPolicyHelper = new AlarmRouteHandlerEvent();
        exchangealarmPolicyHelper.setProcessedAlarmEvent(processedAlarmEvent);
        final List<AlarmRoute> list = new ArrayList<AlarmRoute>();
        list.add(alarmRouteData);
        exchangealarmPolicyHelper.setAlarmRoutes(list);
        autoAckOutTypeHandler.onEvent(exchangealarmPolicyHelper);
    }

    @Test
    public void testAutoAckHandler1() throws SecurityException, IllegalAccessException, NoSuchFieldException {
        alarmRouteData.setEnablePolicy(false);
        final AlarmRouteHandlerEvent exchangealarmPolicyHelper = new AlarmRouteHandlerEvent();
        exchangealarmPolicyHelper.setProcessedAlarmEvent(processedAlarmEvent);
        final List<AlarmRoute> list = new ArrayList<AlarmRoute>();
        list.add(alarmRouteData);
        exchangealarmPolicyHelper.setAlarmRoutes(list);
        autoAckOutTypeHandler.onEvent(exchangealarmPolicyHelper);
    }

    @Test
    public void testAutoAckHandler2() throws SecurityException, IllegalAccessException, NoSuchFieldException {
        alarmRouteData = new AlarmRoute();
        alarmRouteData.setEnablePolicy(false);
        alarmRouteData.setOutputType(AUTO_ACK);
        alarmRouteData.setRouteId(102L);

        final AlarmRouteHandlerEvent exchangealarmPolicyHelper = new AlarmRouteHandlerEvent();
        exchangealarmPolicyHelper.setProcessedAlarmEvent(processedAlarmEvent);
        final List<AlarmRoute> list = new ArrayList<AlarmRoute>();
        list.add(alarmRouteData);
        exchangealarmPolicyHelper.setAlarmRoutes(list);
        autoAckOutTypeHandler.onEvent(exchangealarmPolicyHelper);
    }

    @Test
    public void testAutoAckHandler3() throws SecurityException, IllegalAccessException, NoSuchFieldException {
        alarmRouteData = new AlarmRoute();
        alarmRouteData.setEnablePolicy(false);
        alarmRouteData.setOutputType(AUTO_ACK);
        alarmRouteData.setRouteType(RouteType.AUTO_ACK);
        alarmRouteData.setRouteId(102L);

        final AlarmRouteHandlerEvent exchangealarmPolicyHelper = new AlarmRouteHandlerEvent();
        exchangealarmPolicyHelper.setProcessedAlarmEvent(processedAlarmEvent);
        final List<AlarmRoute> list = new ArrayList<AlarmRoute>();
        list.add(alarmRouteData);
        exchangealarmPolicyHelper.setAlarmRoutes(list);
        autoAckOutTypeHandler.onEvent(exchangealarmPolicyHelper);
    }

    @Test
    public void testReact() {
        final ControlEvent controlEvent = new ControlEvent(42);
        autoAckOutTypeHandler.react(controlEvent);
        autoAckOutTypeHandler.onEvent(alarmRouteData);
    }

}
