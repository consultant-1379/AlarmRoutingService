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
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.ericsson.oss.itpf.common.event.ControlEvent;
import com.ericsson.oss.services.fm.alarmroutemanagement.models.AlarmRoute;
import com.ericsson.oss.services.fm.alarmroutemanagement.models.AlarmRoute.RouteSubordinateObjects;
import com.ericsson.oss.services.fm.alarmroutingservice.cache.AlarmRouteCacheInitializer;
import com.ericsson.oss.services.fm.models.processedevent.ProcessedAlarmEvent;
import com.ericsson.oss.services.fm.models.processedevent.ProcessedEventSeverity;

@RunWith(MockitoJUnitRunner.class)
public class SourceObjectHandlerTest {

    @Mock
    private AlarmRouteCacheInitializer alarmPolicyBuilder;

    @Mock
    private Iterator<javax.cache.Cache.Entry<Long, AlarmRoute>> iter;

    private static final String FDN = "MeContext=LTE02ERBS0000A5";

    private AlarmRoute alarmRouteData = null;
    private ProcessedAlarmEvent processedAlarmEvent = null;

    @Before
    public void set_Up_Data() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {

        new SourceObjectHandlerTestable();
        List<String> spList = new ArrayList<>();
        List<String> pcList = new ArrayList<>();
        List<String> etList = new ArrayList<>();
        final Map<String, String> additionalInformation = new HashMap<String, String>();
        additionalInformation.put("fdn", "Networkelement=LTE02ERBS0000A5");

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
        processedAlarmEvent.setAdditionalInformation(additionalInformation);
        processedAlarmEvent.setFdn(FDN);
        processedAlarmEvent.setPresentSeverity(ProcessedEventSeverity.CRITICAL);
        processedAlarmEvent.setInsertTime(new Date());

    }

    @Test
    public void testSourceObjectHandler_All_SUBORDINATES() throws SecurityException, IllegalAccessException, NoSuchFieldException {
        alarmRouteData.setObjectOfReferences(Arrays.asList((FDN).split(",")));
        alarmRouteData.setSubordinateType(RouteSubordinateObjects.All_SUBORDINATES);
        final AlarmRouteHandlerEvent exchangealarmPolicyHelper = new AlarmRouteHandlerEvent();
        final SourceObjectHandler sourceObjectHandler = new SourceObjectHandlerTestable();
        exchangealarmPolicyHelper.setProcessedAlarmEvent(processedAlarmEvent);
        final List<AlarmRoute> list = new ArrayList<AlarmRoute>();
        list.add(alarmRouteData);
        exchangealarmPolicyHelper.setAlarmRoutes(list);
        sourceObjectHandler.onEvent(exchangealarmPolicyHelper);

        assertEquals("MeContext=LTE02ERBS0000A5", processedAlarmEvent.getFdn());
    }

    @Test
    public void testSourceObjectHandler_NO_SUBORDINATES() throws SecurityException, IllegalAccessException, NoSuchFieldException {
        alarmRouteData.setObjectOfReferences(Arrays.asList((FDN).split(",")));
        alarmRouteData.setSubordinateType(RouteSubordinateObjects.NO_SUBORDINATES);
        final AlarmRouteHandlerEvent exchangealarmPolicyHelper = new AlarmRouteHandlerEvent();
        final SourceObjectHandler sourceObjectHandler = new SourceObjectHandlerTestable();
        exchangealarmPolicyHelper.setProcessedAlarmEvent(processedAlarmEvent);
        final List<AlarmRoute> list = new ArrayList<AlarmRoute>();
        list.add(alarmRouteData);
        exchangealarmPolicyHelper.setAlarmRoutes(list);
        sourceObjectHandler.onEvent(exchangealarmPolicyHelper);

        assertEquals("MeContext=LTE02ERBS0000A5", processedAlarmEvent.getFdn());
    }

    @Test
    public void testForOORwithNoSubordinatesInPolicy() throws SecurityException, IllegalAccessException, NoSuchFieldException {
        final AlarmRouteHandlerEvent exchangealarmPolicyHelper = new AlarmRouteHandlerEvent();
        final SourceObjectHandler sourceObjectHandler = new SourceObjectHandlerTestable();
        processedAlarmEvent.setObjectOfReference("MeContext=LTE02ERBS0000A5");
        alarmRouteData.setSubordinateType(RouteSubordinateObjects.NO_SUBORDINATES);
        processedAlarmEvent.setFdn("MeContext=LTE02ERBS0000A5");
        exchangealarmPolicyHelper.setProcessedAlarmEvent(processedAlarmEvent);
        final List<AlarmRoute> list = new ArrayList<AlarmRoute>();
        alarmRouteData.setObjectOfReference("MeContext=LTE02ERBS0000A5");
        list.add(alarmRouteData);
        exchangealarmPolicyHelper.setAlarmRoutes(list);
        sourceObjectHandler.onEvent(exchangealarmPolicyHelper);
        // assertEquals(exchangealarmPolicyHelper.toString(), ((SourceObjectHandlerTestable) sourceObjectHandler).getInput().toString());
    }

    @Test
    public void testForEmptyOORInPolicy() throws SecurityException, IllegalAccessException, NoSuchFieldException {
        final AlarmRouteHandlerEvent exchangealarmPolicyHelper = new AlarmRouteHandlerEvent();
        final SourceObjectHandler sourceObjectHandler = new SourceObjectHandlerTestable();
        processedAlarmEvent.setObjectOfReference("MeContext=LTE02ERBS0000A5,ManagedElement=1,ENodeBFunction=1");
        processedAlarmEvent.setFdn("");
        exchangealarmPolicyHelper.setProcessedAlarmEvent(processedAlarmEvent);
        final List<AlarmRoute> list = new ArrayList<AlarmRoute>();
        alarmRouteData.setObjectOfReference("");
        list.add(alarmRouteData);
        exchangealarmPolicyHelper.setAlarmRoutes(list);
        sourceObjectHandler.onEvent(exchangealarmPolicyHelper);
        // assertEquals(exchangealarmPolicyHelper.toString(), ((SourceObjectHandlerTestable) sourceObjectHandler).getInput().toString());
    }

    @Test
    public void testReact() {
        final ControlEvent controlEvent = new ControlEvent(42);
        final SourceObjectHandler sourceObjectHandler = new SourceObjectHandlerTestable();
        sourceObjectHandler.react(controlEvent);
        sourceObjectHandler.onEvent(alarmRouteData);
    }

    class SourceObjectHandlerTestable extends SourceObjectHandler {
        Object input;

        public Object getInput() {
            return input;
        }

        public void setInput(final Object input) {
            this.input = input;
        }

    }
}
