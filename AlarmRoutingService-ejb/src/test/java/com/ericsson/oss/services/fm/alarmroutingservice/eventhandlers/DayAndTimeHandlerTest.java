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

import static org.junit.Assert.assertEquals;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
import com.ericsson.oss.services.fm.alarmroutingservice.cache.AlarmRouteCacheInitializer;
import com.ericsson.oss.services.fm.alarmroutingservice.eventhandlers.AlarmRouteHandlerEvent;
import com.ericsson.oss.services.fm.alarmroutingservice.eventhandlers.DayAndTimeHandler;
import com.ericsson.oss.services.fm.models.processedevent.ProcessedAlarmEvent;
import com.ericsson.oss.services.fm.models.processedevent.ProcessedEventSeverity;

@RunWith(MockitoJUnitRunner.class)
public class DayAndTimeHandlerTest {

    @Mock
    private DayAndTimeHandler dayAndTimeHandler;

    @Mock
    private AlarmRouteCacheInitializer alarmPolicyBuilder;

    @Mock
    private Iterator<javax.cache.Cache.Entry<Long, AlarmRoute>> iter;

    @Mock
    private Calendar cal;

    public static final String FDN = "MeContext=LTE02ERBS0000A5,ManagedElement=1,ENodeBFunction=1";

    private AlarmRoute alarmRouteData = null;
    private ProcessedAlarmEvent processedAlarmEvent = null;

    @Before
    public void set_Up_Data() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {

        dayAndTimeHandler = new DayAndTimeHandler();
        cal = Calendar.getInstance();
        List<String> spList = new ArrayList<>();
        List<String> pcList = new ArrayList<>();
        List<String> etList = new ArrayList<>();

        cal.setTime(new Date());
        final SimpleDateFormat dateFormat1 = new SimpleDateFormat("EEEEE");
        final String dayOfWeek = dateFormat1.format(cal.getTime());
        alarmRouteData = new AlarmRoute();
        alarmRouteData.setName("TestCreate");
        alarmRouteData.setObjectOfReference(FDN);
        alarmRouteData.setDescription("testdescription");
        alarmRouteData.setPerceivedSeverity("CRITICAL");
        alarmRouteData.setBeginTime("00:00:00");
        alarmRouteData.setEnablePolicy(true);
        alarmRouteData.setEndTime("23:59:59");
        alarmRouteData.setOutputType("Auto_Ack");
        alarmRouteData.setDays(dayOfWeek);
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
        processedAlarmEvent.setEventTime(new Date());

    }

    @Test
    public void testEventTypeHandler() throws SecurityException, IllegalAccessException, NoSuchFieldException {
        final AlarmRouteHandlerEvent exchangealarmPolicyHelper = new AlarmRouteHandlerEvent();
        exchangealarmPolicyHelper.setProcessedAlarmEvent(processedAlarmEvent);
        final List<AlarmRoute> list = new ArrayList<AlarmRoute>();
        list.add(alarmRouteData);
        exchangealarmPolicyHelper.setAlarmRoutes(list);
        dayAndTimeHandler.onEvent(exchangealarmPolicyHelper);
        assertEquals(FDN, processedAlarmEvent.getFdn());
    }

    @Test
    public void testEventTypeHandler1() throws SecurityException, IllegalAccessException, NoSuchFieldException {
        alarmRouteData.setEndTime("55:55:55");
        alarmRouteData.setDays("SUNDAY");
        final AlarmRouteHandlerEvent exchangealarmPolicyHelper = new AlarmRouteHandlerEvent();
        exchangealarmPolicyHelper.setProcessedAlarmEvent(processedAlarmEvent);
        final List<AlarmRoute> list = new ArrayList<AlarmRoute>();
        list.add(alarmRouteData);
        exchangealarmPolicyHelper.setAlarmRoutes(list);
        dayAndTimeHandler.onEvent(exchangealarmPolicyHelper);
        assertEquals(FDN, processedAlarmEvent.getFdn());
    }

    @Test
    public void testReact() {
        final ControlEvent controlEvent = new ControlEvent(42);
        dayAndTimeHandler.react(controlEvent);
        dayAndTimeHandler.onEvent(alarmRouteData);
    }
}
