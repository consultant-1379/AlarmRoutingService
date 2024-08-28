/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2018
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/

package com.ericsson.oss.services.fm.alarmroutingservice.eventhandlers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import com.ericsson.oss.itpf.common.event.ControlEvent;
import com.ericsson.oss.services.fm.alarmroutemanagement.models.AlarmRoute;
import com.ericsson.oss.services.fm.alarmroutemanagement.models.AlarmRoute.RouteSubordinateObjects;
import com.ericsson.oss.services.fm.models.processedevent.ProcessedAlarmEvent;
import com.ericsson.oss.services.fm.models.processedevent.ProcessedEventSeverity;
import com.ericsson.oss.services.models.alarm.RouteType;

@RunWith(MockitoJUnitRunner.class)
public class FileRouteHandlerTest {

    @InjectMocks
    private FileRouteHandler fileRouteHandler;

    private static final String FDN = "MeContext=LTE02ERBS0000A5,ManagedElement=1,ENodeBFunction=1";

    private AlarmRoute alarmRouteData = null;
    private AlarmRoute alarmRouteData1 = null;
    private ProcessedAlarmEvent processedAlarmEvent = null;

    @Before
    public void set_Up_Data() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        new FileRouteHandlerTestTable();
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
        processedAlarmEvent.setFdn(FDN);
        processedAlarmEvent.setPresentSeverity(ProcessedEventSeverity.CRITICAL);
        processedAlarmEvent.setInsertTime(new Date());
    }

    /**
     * Tests send To file route functionality when route is not available in the list.
     */
    @Test
    public void testSendToFileRouteDoesNotExists() {
        final AlarmRouteHandlerEvent exchangealarmPolicyHelper = new AlarmRouteHandlerEvent();
        final FileRouteHandler fileRouteHandler = new FileRouteHandlerTestTable();
        exchangealarmPolicyHelper.setProcessedAlarmEvent(processedAlarmEvent);
        final List<AlarmRoute> list = new ArrayList<AlarmRoute>();
        alarmRouteData1 = new AlarmRoute();
        list.add(alarmRouteData1);
        exchangealarmPolicyHelper.setAlarmRoutes(list);
        fileRouteHandler.onEvent(exchangealarmPolicyHelper);
    }

    /**
     * Tests send To file route functionality when route is available in the list with different routeType.
     */
    @Test
    public void testSendToFileRoute() {
        final AlarmRouteHandlerEvent exchangealarmPolicyHelper = new AlarmRouteHandlerEvent();
        final FileRouteHandler fileRouteHandler = new FileRouteHandlerTestTable();
        exchangealarmPolicyHelper.setProcessedAlarmEvent(processedAlarmEvent);
        final List<AlarmRoute> list = new ArrayList<AlarmRoute>();
        alarmRouteData.setRouteType(RouteType.AUTO_ACK);
        list.add(alarmRouteData);
        exchangealarmPolicyHelper.setAlarmRoutes(list);
        fileRouteHandler.onEvent(exchangealarmPolicyHelper);
    }

    @Test
    public void testReact() {
        final ControlEvent controlEvent = new ControlEvent(42);
        final FileRouteHandler fileRouteHandler = new FileRouteHandlerTestTable();
        fileRouteHandler.react(controlEvent);
        fileRouteHandler.onEvent(alarmRouteData);
    }

    class FileRouteHandlerTestTable extends FileRouteHandler {
        Object input;

        public Object getInput() {
            return input;
        }

        public void setInput(final Object input) {
            this.input = input;
        }
    }

}
