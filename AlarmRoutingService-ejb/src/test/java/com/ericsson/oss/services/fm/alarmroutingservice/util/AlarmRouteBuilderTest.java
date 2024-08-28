/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2020
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/

package com.ericsson.oss.services.fm.alarmroutingservice.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.ericsson.oss.itpf.datalayer.dps.notification.event.AttributeChangeData;
import com.ericsson.oss.services.fm.alarmroutemanagement.models.AlarmRoute;

@RunWith(MockitoJUnitRunner.class)
public class AlarmRouteBuilderTest {

	@InjectMocks
    private AlarmRouteBuilder alarmRouteBuilder;

    
    @Test
    public void test_getAlarmRouteData() {
    	final AlarmRoute alarmRouteObg = new AlarmRoute();
    	List<String> spList = new ArrayList<>();
    	spList.add("another_sp");
    	alarmRouteObg.setSpecificProblem(spList);
    	List<String> pcList = new ArrayList<>();
    	pcList.add("another_pc");
    	alarmRouteObg.setProbableCause(pcList);
    	List<String> etList = new ArrayList<>();
    	etList.add("another_et");
    	alarmRouteObg.setEventType(etList);
    	Set<AttributeChangeData> changedAttributeData = new HashSet<>();
    	List<String> specificProbs = new ArrayList<>();
    	specificProbs.add("test_sp12");
    	AttributeChangeData attributeChangeDataspList = new AttributeChangeData("specificProblems", null, specificProbs,null,null);
    	List<String> probCauses = new ArrayList<>();
    	probCauses.add("test_pc12");
    	AttributeChangeData attributeChangeDatapcList = new AttributeChangeData("probableCauses", null, probCauses,null,null);
    	List<String> evntCauses = new ArrayList<>();
    	evntCauses.add("test_et12");
    	AttributeChangeData attributeChangeDataetList = new AttributeChangeData("eventTypes", null, evntCauses,null,null);
    	changedAttributeData.add(attributeChangeDataspList);
    	changedAttributeData.add(attributeChangeDatapcList);
    	changedAttributeData.add(attributeChangeDataetList);
        alarmRouteBuilder.buildAlarmRouteFromChangedAttributes(alarmRouteObg, changedAttributeData);
        Assert.assertTrue(alarmRouteObg.getSpecificProblem().contains("test_sp12"));
    }
}
