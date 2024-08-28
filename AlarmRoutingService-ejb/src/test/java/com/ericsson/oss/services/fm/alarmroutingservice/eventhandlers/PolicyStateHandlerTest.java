package com.ericsson.oss.services.fm.alarmroutingservice.eventhandlers;

import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.cache.Cache.Entry;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.ericsson.oss.itpf.common.event.ControlEvent;
import com.ericsson.oss.services.fm.alarmroutemanagement.models.AlarmRoute;
import com.ericsson.oss.services.fm.alarmroutemanagement.models.AlarmRoute.RouteSubordinateObjects;
import com.ericsson.oss.services.fm.alarmroutingservice.cache.AlarmRoutesHolder;
import com.ericsson.oss.services.fm.alarmroutingservice.eventhandlers.PolicyStateHandler;
import com.ericsson.oss.services.fm.models.processedevent.ProcessedAlarmEvent;
import com.ericsson.oss.services.fm.models.processedevent.ProcessedEventSeverity;

@RunWith(MockitoJUnitRunner.class)
public class PolicyStateHandlerTest {

    @Mock
    private AlarmRoutesHolder alarmRoutesHolder;

    @Mock
    private PolicyStateHandler policyStateHandler;

    @Mock
    private Iterator<javax.cache.Cache.Entry<Long, AlarmRoute>> iter;

    private static final String FDN = "MeContext=LTE02ERBS0000A5,ManagedElement=1,ENodeBFunction=1";
    private AlarmRoute alarmRouteData = null;
    private ProcessedAlarmEvent processedAlarmEvent = null;

    @Before
    public void set_Up_Data() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        policyStateHandler = new PolicyStateHandler();
        List<String> spList = new ArrayList<>();
        List<String> pcList = new ArrayList<>();
        List<String> etList = new ArrayList<>();

        final Field mockCache = PolicyStateHandler.class.getDeclaredField("alarmRoutesHolder");
        mockCache.setAccessible(true);
        mockCache.set(policyStateHandler, alarmRoutesHolder);

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
        processedAlarmEvent.setSpecificProblem("RAIN");
        processedAlarmEvent.setFdn(FDN);
        processedAlarmEvent.setPresentSeverity(ProcessedEventSeverity.CRITICAL);
        processedAlarmEvent.setInsertTime(new Date());

    }

    @SuppressWarnings("unchecked")
    @Test
    public void testPolicyStateHandler() throws SecurityException, IllegalAccessException, NoSuchFieldException {
        final Iterator<Entry<Long, AlarmRoute>> itr = Mockito.mock(Iterator.class);
        final Entry<Long, AlarmRoute> entry = Mockito.mock(Entry.class);
        when(entry.getKey()).thenReturn(102L);
        when(entry.getValue()).thenReturn(alarmRouteData);
        when(itr.hasNext()).thenReturn(true).thenReturn(false);
        when(itr.next()).thenReturn(entry);
        policyStateHandler.onEvent(processedAlarmEvent);
    }

    @Test
    public void testReact() {
        final ControlEvent controlEvent = new ControlEvent(42);
        policyStateHandler.react(controlEvent);
        policyStateHandler.onEvent(alarmRouteData);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testPolicyStateHandler1() throws SecurityException, IllegalAccessException, NoSuchFieldException {
        alarmRouteData = new AlarmRoute();
        alarmRouteData.setEnablePolicy(false);
        final Iterator<Entry<Long, AlarmRoute>> itr = Mockito.mock(Iterator.class);
        final Entry<Long, AlarmRoute> entry = Mockito.mock(Entry.class);
        when(entry.getKey()).thenReturn(102L);
        when(entry.getValue()).thenReturn(alarmRouteData);
        when(itr.hasNext()).thenReturn(true).thenReturn(false);
        when(itr.next()).thenReturn(entry);
        policyStateHandler.onEvent(processedAlarmEvent);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testPolicyStateHandlerForAck() throws SecurityException, IllegalAccessException, NoSuchFieldException {
        alarmRouteData = new AlarmRoute();
        alarmRouteData.setEnablePolicy(false);
        final Iterator<Entry<Long, AlarmRoute>> itr = Mockito.mock(Iterator.class);
        final Entry<Long, AlarmRoute> entry = Mockito.mock(Entry.class);
        when(entry.getKey()).thenReturn(102L);
        when(entry.getValue()).thenReturn(alarmRouteData);
        when(itr.hasNext()).thenReturn(true).thenReturn(false);
        when(itr.next()).thenReturn(entry);
        policyStateHandler.onEvent(processedAlarmEvent);

    }

}
