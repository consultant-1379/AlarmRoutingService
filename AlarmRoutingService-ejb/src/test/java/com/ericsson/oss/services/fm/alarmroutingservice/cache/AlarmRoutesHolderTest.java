/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2015
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.oss.services.fm.alarmroutingservice.cache;

import static com.ericsson.oss.services.fm.alarmroutemanagement.models.AlarmRoute.SUBJECT;
import static com.ericsson.oss.services.fm.alarmroutemanagement.models.AlarmRoute.TO_ADDRESS;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import com.ericsson.oss.services.fm.alarmroutemanagement.models.AlarmRoute;

@RunWith(MockitoJUnitRunner.class)
public class AlarmRoutesHolderTest {

    @InjectMocks
    private final AlarmRoutesHolder alarmRoutesHolder = new AlarmRoutesHolder();

    @Mock
    private AlarmRoute routeData;

    @Mock
    private AlarmRouteAssociationData alarmRouteAssociationData;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testAddEntrytoAlarmRouteMap() {
        alarmRoutesHolder.addAlarmRoute(1234L, routeData);
        Assert.assertNotNull(alarmRoutesHolder.getAlarmRoute(1234L));
    }

    @Test
    public void testAddEntrytoAlarmRouteAssociationMap() {
        final Map<String, Object> associationAttributes = new HashMap<String, Object>();
        associationAttributes.put(TO_ADDRESS, "testemail@ericsston.com");
        associationAttributes.put(SUBJECT, "test email");
        final AlarmRouteAssociationData AlarmRouteAssociationData = new AlarmRouteAssociationData(associationAttributes);
        alarmRoutesHolder.addAlarmRouteAssociation(1234L, AlarmRouteAssociationData);
        Assert.assertNotNull(alarmRoutesHolder.getAlarmRouteAssociation(1234L));
    }

    @Test
    public void testUpdateEntrytoAlarmRouteMap() {
        alarmRoutesHolder.addAlarmRoute(12345L, routeData);
        Assert.assertTrue(alarmRoutesHolder.updateAlarmRoute(12345L, routeData));
        alarmRoutesHolder.addAlarmRoute(12345L, routeData);
        Assert.assertFalse(alarmRoutesHolder.updateAlarmRoute(1234L, routeData));
    }

    @Test
    public void testUpdateEntrytoAlarmRouteAssociationMap() {
        final Map<String, Object> associationAttributes = new HashMap<String, Object>();
        associationAttributes.put(TO_ADDRESS, "testemail@ericsston.com");
        associationAttributes.put(SUBJECT, "test email");
        final AlarmRouteAssociationData AlarmRouteAssociationData = new AlarmRouteAssociationData(associationAttributes);
        alarmRoutesHolder.addAlarmRouteAssociation(12345L, AlarmRouteAssociationData);
        final Map<String, Object> associationAttributesUpdate = new HashMap<String, Object>();
        associationAttributesUpdate.put(SUBJECT, "test email");
        Assert.assertTrue(alarmRoutesHolder.updateAlarmRouteAssociation(12345L, associationAttributesUpdate));
    }

    @Test
    public void testUpdateEntrytoAlarmRouteMap_WithNoPreviousEntry() {
        alarmRoutesHolder.addAlarmRoute(12345L, routeData);
        Assert.assertTrue(alarmRoutesHolder.updateAlarmRoute(12345L, routeData));
    }

    @Test
    public void testUpdateEntrytoAlarmRouteAssociationMap_WithNoPreviousEntry() {
        final Map<String, Object> associationAttributes = new HashMap<String, Object>();
        associationAttributes.put(TO_ADDRESS, "testemail@ericsston.com");
        associationAttributes.put(SUBJECT, "test email");
        alarmRoutesHolder.updateAlarmRouteAssociation(12345L, associationAttributes);
        Assert.assertNull(alarmRoutesHolder.getAlarmRouteAssociation(12345L));
    }

    @Test
    public void testRemoveAlarmRouteEntryAndVerifyAssociationRemoved() {
        alarmRoutesHolder.addAlarmRoute(123456L, routeData);
        alarmRoutesHolder.addAlarmRouteAssociation(123456L, alarmRouteAssociationData);
        Assert.assertNotNull(alarmRoutesHolder.getAlarmRoute(123456L));
        Assert.assertNotNull(alarmRoutesHolder.getAlarmRoutes());
        Assert.assertTrue(alarmRoutesHolder.removeAlarmRoute(123456L));
        Assert.assertNull(alarmRoutesHolder.getAlarmRouteAssociation(12345L));
        Assert.assertFalse(alarmRoutesHolder.removeAlarmRoute(123455L));

    }

    @Test
    public void testRemoveAlarmROuteAssociationEntryAndVerifyAlarmRouRemoved() {
        alarmRoutesHolder.addAlarmRoute(123456L, routeData);
        alarmRoutesHolder.addAlarmRouteAssociation(123456L, alarmRouteAssociationData);
        Assert.assertNotNull(alarmRoutesHolder.getAlarmRoute(123456L));
        Assert.assertNotNull(alarmRoutesHolder.getAlarmRoutes());
        Assert.assertTrue(alarmRoutesHolder.removeAlarmRouteAssociation(123456L));
        Assert.assertNotNull(alarmRoutesHolder.getAlarmRoute(123456L));
    }

}
