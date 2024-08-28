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

import static com.ericsson.oss.services.fm.alarmroutemanagement.models.AlarmRoute.NE_FDNS;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.ALARM_ROUTE_POLICY;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.ericsson.oss.itpf.datalayer.dps.DataBucket;
import com.ericsson.oss.itpf.datalayer.dps.DataPersistenceService;
import com.ericsson.oss.itpf.datalayer.dps.notification.event.AssociatedSideData;
import com.ericsson.oss.itpf.datalayer.dps.notification.event.DpsAssociationCreatedEvent;
import com.ericsson.oss.itpf.datalayer.dps.notification.event.DpsAssociationRemovedEvent;
import com.ericsson.oss.itpf.datalayer.dps.notification.event.DpsAttributeChangedEvent;
import com.ericsson.oss.itpf.datalayer.dps.persistence.PersistenceObject;
import com.ericsson.oss.services.fm.alarmroutemanagement.models.AlarmRoute;
import com.ericsson.oss.services.fm.alarmroutingservice.cache.AlarmRouteAssociationData;
import com.ericsson.oss.services.fm.alarmroutingservice.cache.AlarmRoutesHolder;
import com.ericsson.oss.services.fm.alarmroutingservice.route.associations.handlers.AlarmRouteAssociationsHolder;
import com.ericsson.oss.services.fm.alarmroutingservice.util.AlarmRouteBuilder;
import com.ericsson.oss.services.fm.alarmroutingservice.util.DataPersistenceServiceProvider;

@RunWith(MockitoJUnitRunner.class)
public class AlarmRouteAssociationProcessorTest {

    @InjectMocks
    private AlarmRouteAssociationProcessor alarmRouteAssociationProcessor;

    @Mock
    private AlarmRoutesHolder alarmRoutesHolder;

    @Mock
    private DataPersistenceServiceProvider dps;

    @Mock
    private DataPersistenceService dataPersistenceService;

    @Mock
    private DataBucket dataBucket;

    @Mock
    private AlarmRouteBuilder alarmRouteBuilder;

    @Mock
    private AlarmRouteAssociationsHolder alarmRouteAssociationsHolder;

    @Mock
    private DpsAssociationCreatedEvent dpsAssociationCreatedEvent;

    @Mock
    private DpsAssociationRemovedEvent dpsAssociationRemovedEvent;

    @Mock
    private DpsAttributeChangedEvent dpsAttributeChangedEvent;

    @Mock
    private PersistenceObject associationPersistenceObject;

    @Mock
    private PersistenceObject alarmRoutePersistenceObject;

    @Mock
    private AlarmRoute alarmRoute;

    @Test
    public void test_ProcessAssociationEvents_RemoveAssociation() {
        dpsAssociationRemovedEvent.setPoId(1234L);
        alarmRouteAssociationProcessor.processAssociationEvents(dpsAssociationRemovedEvent);
        verify(alarmRoutesHolder).removeAlarmRouteAssociation(1234L);

    }

    @Test
    public void test_ProcessAssociationEvents_CreateAssociation_AlarmRoute_Avaliable_In_Cache() {
        createOrDeleteAssociationDataSetUp();
        when(alarmRoutesHolder.getAlarmRoute(1234L)).thenReturn(alarmRoute);
        alarmRouteAssociationProcessor.processAssociationEvents(dpsAssociationCreatedEvent);
        verify(alarmRoutesHolder).addAlarmRouteAssociation(Matchers.anyLong(), (AlarmRouteAssociationData) Matchers.anyObject());

    }

    @Test
    public void test_ProcessAssociationEvents_CreateAssociation_AlarmRoute_Not_Avaliable_In_Cache() {
        createOrDeleteAssociationDataSetUp();
        when(dps.getDataPersistenceServiceInstance().getLiveBucket().findPoById(1234L)).thenReturn(alarmRoutePersistenceObject);
        when(alarmRoutePersistenceObject.getAllAttributes()).thenReturn(new HashMap<String, Object>());
        when(alarmRoutePersistenceObject.getAttribute(NE_FDNS)).thenReturn(new ArrayList<String>());
        alarmRouteAssociationProcessor.processAssociationEvents(dpsAssociationCreatedEvent);
        verify(alarmRoutesHolder).addAlarmRoute(Matchers.anyLong(), (AlarmRoute) Matchers.anyObject());
        verify(alarmRoutesHolder).addAlarmRouteAssociation(Matchers.anyLong(), (AlarmRouteAssociationData) Matchers.anyObject());

    }

    @Test
    public void test_ProcessUpdatedEvent_AlarmRoute_Not_Available_In_Cache() {
        processUpdateEventDataSetup();
        alarmRouteAssociationProcessor.processUpdatedEvent(dpsAttributeChangedEvent);
        Assert.assertFalse(alarmRoutesHolder.updateAlarmRouteAssociation(Matchers.anyLong(), (Map<String, Object>) Matchers.anyObject()));
    }

    private void processUpdateEventDataSetup() {
        dpsAttributeChangedEvent.setPoId(12345L);
        when(dps.getDataPersistenceServiceInstance()).thenReturn(dataPersistenceService);
        when(dps.getDataPersistenceServiceInstance().getLiveBucket()).thenReturn(dataBucket);
        when(dps.getDataPersistenceServiceInstance().getLiveBucket().findPoById(12345L)).thenReturn(associationPersistenceObject);
        final Set<String> bsideEndpointNames = new HashSet<String>();
        bsideEndpointNames.add(ALARM_ROUTE_POLICY);
        when(alarmRouteAssociationsHolder.getRouteBsideEndPointNames()).thenReturn(bsideEndpointNames);
        final Collection<PersistenceObject> alarmRoutePOs = new ArrayList<PersistenceObject>();
        alarmRoutePOs.add(alarmRoutePersistenceObject);
        when(associationPersistenceObject.getAssociations(Matchers.anyString())).thenReturn(alarmRoutePOs);
    }

    @Test
    public void test_ProcessUpdatedEvent_AlarmRoute_Available_In_Cache() {
        processUpdateEventDataSetup();
        when(alarmRoutePersistenceObject.getPoId()).thenReturn(1234L);
        when(alarmRoutesHolder.getAlarmRoute(alarmRoutePersistenceObject.getPoId())).thenReturn(alarmRoute);
        alarmRouteAssociationProcessor.processUpdatedEvent(dpsAttributeChangedEvent);
        verify(alarmRoutesHolder).updateAlarmRouteAssociation(Matchers.anyLong(), (Map<String, Object>) Matchers.anyObject());
    }

    private void createOrDeleteAssociationDataSetUp() {
        dpsAssociationCreatedEvent.setPoId(1234L);
        final AssociatedSideData associatedSideData = new AssociatedSideData(12345L, null);
        final Collection<AssociatedSideData> associations = new ArrayList<AssociatedSideData>();
        associations.add(associatedSideData);
        when(dpsAssociationCreatedEvent.getNewAssociations()).thenReturn(associations);
        when(dps.getDataPersistenceServiceInstance()).thenReturn(dataPersistenceService);
        when(dps.getDataPersistenceServiceInstance().getLiveBucket()).thenReturn(dataBucket);
        when(dps.getDataPersistenceServiceInstance().getLiveBucket().findPoById(12345L)).thenReturn(associationPersistenceObject);

        when(associationPersistenceObject.getAllAttributes()).thenReturn(new HashMap<String, Object>());
    }

}
