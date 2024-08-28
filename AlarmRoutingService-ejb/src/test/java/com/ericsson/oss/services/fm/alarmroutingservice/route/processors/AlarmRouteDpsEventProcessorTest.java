package com.ericsson.oss.services.fm.alarmroutingservice.route.processors;

import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.ericsson.oss.itpf.datalayer.dps.notification.event.DpsObjectCreatedEvent;
import com.ericsson.oss.services.fm.alarmroutingservice.cache.AlarmRoutesHolder;

@RunWith(MockitoJUnitRunner.class)
public class AlarmRouteDpsEventProcessorTest {

    @InjectMocks
    private AlarmRouteDpsEventProcessor AlarmRouteDpsEventProcessor;

    @Mock
    private DpsObjectCreatedEvent dpsAttributeEvent;
    
    @Mock
    private AlarmRoutesHolder alarmRoutesHolder;
    

    @Test
    public void test_processAlarmRouteCreateEvent() {
    	final Map<String, Object> alarmRouteAttributes = new HashMap<>();
    	alarmRouteAttributes.put("specificProblem", "specificProblem_test");
    	alarmRouteAttributes.put("probableCause", "probableCause_test");
    	alarmRouteAttributes.put("eventType", "eventType_test");
    	final List<String> specificProbs = new ArrayList<>();
        specificProbs.add("specificProblems_test_1");
    	alarmRouteAttributes.put("specificProblems", specificProbs);
    	final List<String> probCauses = new ArrayList<>();
    	probCauses.add("probableCauses_test_1");
    	alarmRouteAttributes.put("probableCauses", probCauses);
    	alarmRouteAttributes.put("enablePolicy", false);
    	final List<String> eventTypes = new ArrayList<>();
    	eventTypes.add("eventTypes_test_1");
    	alarmRouteAttributes.put("eventTypes", eventTypes);
    	dpsAttributeEvent.setPoId(20L);
		when(alarmRoutesHolder.getAlarmRoute(dpsAttributeEvent.getPoId())).thenReturn(null);
    	when(dpsAttributeEvent.getAttributeValues()).thenReturn(alarmRouteAttributes);
    	AlarmRouteDpsEventProcessor.processAlarmRouteDpsEvent(dpsAttributeEvent);
    }
       
    }

