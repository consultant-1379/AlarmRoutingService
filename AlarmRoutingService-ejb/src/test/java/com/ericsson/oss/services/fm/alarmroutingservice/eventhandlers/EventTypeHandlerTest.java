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
import com.ericsson.oss.services.fm.alarmroutingservice.cache.AlarmRouteCacheInitializer;
import com.ericsson.oss.services.fm.alarmroutingservice.eventhandlers.AlarmRouteHandlerEvent;
import com.ericsson.oss.services.fm.alarmroutingservice.eventhandlers.EventTypeHandler;
import com.ericsson.oss.services.fm.models.processedevent.ProcessedAlarmEvent;
import com.ericsson.oss.services.fm.models.processedevent.ProcessedEventSeverity;

@RunWith(MockitoJUnitRunner.class)
public class EventTypeHandlerTest {

    @Mock
    private AlarmRouteCacheInitializer alarmPolicyBuilder;

    @Mock
    private Iterator<javax.cache.Cache.Entry<Long, AlarmRoute>> iter;

    private static final String FDN = "MeContext=LTE02ERBS0000A5,ManagedElement=1,ENodeBFunction=1";

    private AlarmRoute alarmRouteData = null;
    private ProcessedAlarmEvent processedAlarmEvent = null;

    @Before
    public void set_Up_Data() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        alarmRouteData = new AlarmRoute();
        List<String> spList = new ArrayList<>();
        List<String> pcList = new ArrayList<>();
        List<String> etList = new ArrayList<>();
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
        processedAlarmEvent.setSpecificProblem("RAIN");
        processedAlarmEvent.setFdn(FDN);
        processedAlarmEvent.setPresentSeverity(ProcessedEventSeverity.CRITICAL);
        processedAlarmEvent.setInsertTime(new Date());

    }

    @Test
    public void testETHandler() throws SecurityException, IllegalAccessException, NoSuchFieldException {
        final AlarmRouteHandlerEvent exchangealarmPolicyHelper = new AlarmRouteHandlerEvent();
        final EventTypeHandler eTHandler = new EventTypeHandlerTestable();
        exchangealarmPolicyHelper.setProcessedAlarmEvent(processedAlarmEvent);
        final List<AlarmRoute> list = new ArrayList<AlarmRoute>();
        list.add(alarmRouteData);
        exchangealarmPolicyHelper.setAlarmRoutes(list);
        eTHandler.onEvent(exchangealarmPolicyHelper);
        assertEquals(FDN, processedAlarmEvent.getFdn());
    }

    @Test
    public void testETHandler1() throws SecurityException, IllegalAccessException, NoSuchFieldException {
        final AlarmRouteHandlerEvent exchangealarmPolicyHelper = new AlarmRouteHandlerEvent();
        final EventTypeHandler eTHandler = new EventTypeHandlerTestable();
        exchangealarmPolicyHelper.setProcessedAlarmEvent(processedAlarmEvent);
        final List<AlarmRoute> list = new ArrayList<AlarmRoute>();
        List<String> etList = new ArrayList<>();
        etList.add("Sample22");
        alarmRouteData.setEventType(etList);
        list.add(alarmRouteData);
        exchangealarmPolicyHelper.setAlarmRoutes(list);
        eTHandler.onEvent(exchangealarmPolicyHelper);
        assertEquals(FDN, processedAlarmEvent.getFdn());
    }

    @Test
    public void testETHandler2() throws SecurityException, IllegalAccessException, NoSuchFieldException {
        final AlarmRouteHandlerEvent exchangealarmPolicyHelper = new AlarmRouteHandlerEvent();
        final EventTypeHandler eTHandler = new EventTypeHandlerTestable();
        exchangealarmPolicyHelper.setProcessedAlarmEvent(processedAlarmEvent);
        final List<AlarmRoute> list = new ArrayList<AlarmRoute>();
        List<String> etList = new ArrayList<>();
        alarmRouteData.setEventType(etList);
        list.add(alarmRouteData);
        exchangealarmPolicyHelper.setAlarmRoutes(list);
        eTHandler.onEvent(exchangealarmPolicyHelper);
        assertEquals(FDN, processedAlarmEvent.getFdn());
    }

    @Test
    public void testToValidateMultipleETValueInPolicy() throws SecurityException, IllegalAccessException, NoSuchFieldException {
        final AlarmRouteHandlerEvent exchangealarmPolicyHelper = new AlarmRouteHandlerEvent();
        final EventTypeHandler eTHandler = new EventTypeHandlerTestable();
        List<String> etList = new ArrayList<>();
        etList.add("ET1");
        etList.add("ET2");
        alarmRouteData.setEventType(etList);
        processedAlarmEvent.setEventType("ET1");
        processedAlarmEvent.setFdn(FDN);
        exchangealarmPolicyHelper.setProcessedAlarmEvent(processedAlarmEvent);
        final List<AlarmRoute> list = new ArrayList<AlarmRoute>();
        list.add(alarmRouteData);
        exchangealarmPolicyHelper.setAlarmRoutes(list);
        eTHandler.onEvent(exchangealarmPolicyHelper);
        //assertEquals(exchangealarmPolicyHelper.toString(), ((EventTypeHandlerTestable) eTHandler).getInput().toString());
    }

    @Test
    public void testToValidateSingleETValueInPolicy() throws SecurityException, IllegalAccessException, NoSuchFieldException {
        final AlarmRouteHandlerEvent exchangealarmPolicyHelper = new AlarmRouteHandlerEvent();
        final EventTypeHandler eTHandler = new EventTypeHandlerTestable();
        processedAlarmEvent.setEventType("ET1");
        exchangealarmPolicyHelper.setProcessedAlarmEvent(processedAlarmEvent);
        final List<AlarmRoute> list = new ArrayList<AlarmRoute>();
        List<String> etList = new ArrayList<>();
        etList.add("ET1");
        alarmRouteData.setEventType(etList);
        processedAlarmEvent.setFdn(FDN);
        list.add(alarmRouteData);
        exchangealarmPolicyHelper.setAlarmRoutes(list);
        eTHandler.onEvent(exchangealarmPolicyHelper);
        //assertEquals(exchangealarmPolicyHelper.toString(), ((EventTypeHandlerTestable) eTHandler).getInput().toString());
    }

    @Test
    public void testReact() {
        final ControlEvent controlEvent = new ControlEvent(42);
        final EventTypeHandler eTHandler = new EventTypeHandlerTestable();
        eTHandler.react(controlEvent);
        eTHandler.onEvent(alarmRouteData);
    }

    class EventTypeHandlerTestable extends EventTypeHandler {
        Object input;

        public Object getInput() {
            return input;
        }

        public void setInput(final Object input) {
            this.input = input;
        }

    }

}
