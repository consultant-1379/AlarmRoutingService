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

package com.ericsson.oss.services.fm.alarmroutingservice.cache;

import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.ALARM_ROUTE_POLICY;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.EMAIL_DETAILS;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.OSS_FM;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.ejb.Timer;
import javax.ejb.TimerService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.ericsson.oss.itpf.datalayer.dps.DataBucket;
import com.ericsson.oss.itpf.datalayer.dps.DataPersistenceService;
import com.ericsson.oss.itpf.datalayer.dps.object.builder.PersistenceObjectBuilder;
import com.ericsson.oss.itpf.datalayer.dps.persistence.PersistenceObject;
import com.ericsson.oss.itpf.datalayer.dps.query.Query;
import com.ericsson.oss.itpf.datalayer.dps.query.QueryBuilder;
import com.ericsson.oss.itpf.datalayer.dps.query.QueryExecutor;
import com.ericsson.oss.itpf.datalayer.dps.query.TypeRestrictionBuilder;
import com.ericsson.oss.services.fm.alarmroutemanagement.models.AlarmRoute;
import com.ericsson.oss.services.fm.alarmroutemanagement.models.RouteRestrictionBuilder;
import com.ericsson.oss.services.fm.alarmroutingservice.route.associations.handlers.AlarmRouteAssociationsHolder;
import com.ericsson.oss.services.fm.alarmroutingservice.util.DataPersistenceServiceProvider;

@RunWith(MockitoJUnitRunner.class)
public class AlarmRouteCacheInitializerTest {

    @InjectMocks
    private AlarmRouteCacheInitializer alarmRouteCacheInitializer;

    @Mock
    private RouteRestrictionBuilder policyRestrictionBuilder;

    @Mock
    private AlarmRoute alarmRouteData;

    @Mock
    private DataPersistenceServiceProvider dps;

    @Mock
    private DataPersistenceService dataPersistenceService;

    @Mock
    private DataBucket liveBucket;

    @Mock
    private QueryBuilder queryBuilder;

    @Mock
    private Query<TypeRestrictionBuilder> typeQuery;

    @Mock
    private QueryExecutor queryExecutor;

    @Mock
    private TypeRestrictionBuilder restrictionBuilder;

    @Mock
    private Iterator<Object> poListIterator;

    @Mock
    private PersistenceObject poObject;
    @Mock
    private PersistenceObject associationPO;

    @Mock
    private PersistenceObjectBuilder persistenceObjectBuilder;

    @Mock
    private AlarmRoutesHolder alarmRoutesHolder;

    @Mock
    private TimerService timerService;

    @Mock
    private Timer alarmRouteCacheBuildTimer;

    @Mock
    private AlarmRouteAssociationsHolder alarmRouteAssociationsHolder;

    @Mock
    private Collection<PersistenceObject> associations;

    @Before
    public void setUp() {
        when(dps.getDataPersistenceServiceInstance()).thenReturn(dataPersistenceService);
        when(dataPersistenceService.getLiveBucket()).thenReturn(liveBucket);
        when(dps.getDataPersistenceServiceInstance().getQueryBuilder()).thenReturn(queryBuilder);
        when(queryBuilder.createTypeQuery(OSS_FM, ALARM_ROUTE_POLICY)).thenReturn(typeQuery);
        when(typeQuery.getRestrictionBuilder()).thenReturn(restrictionBuilder);
        when(liveBucket.getQueryExecutor()).thenReturn(queryExecutor);
        when(queryExecutor.execute(typeQuery)).thenReturn(poListIterator);

    }

    @Test
    public void test_timeOut() {
        alarmRouteCacheInitializer.startAlarmTimer();
        final HashMap<String, Object> restrictionAttrList = mock(HashMap.class);
        when(policyRestrictionBuilder.getPolicyRestriction()).thenReturn(restrictionAttrList);
        when(restrictionAttrList.size()).thenReturn(8);
        when(poListIterator.hasNext()).thenReturn(true, false);
        when(poListIterator.next()).thenReturn(poObject);
        when(poObject.getAllAttributes()).thenReturn(new HashMap<String, Object>());
        final Collection<PersistenceObject> associationPOs = new ArrayList<PersistenceObject>();
        associationPOs.add(associationPO);
        when(poObject.getAssociations(Matchers.anyString())).thenReturn(associationPOs);
        when(associationPO.getAllAttributes()).thenReturn(new HashMap<String, Object>());
        final List<String> aSideEndPointNames = new ArrayList<String>();
        aSideEndPointNames.add(EMAIL_DETAILS);
        when(alarmRouteAssociationsHolder.getRouteAsideEndPointNames()).thenReturn(aSideEndPointNames);
        alarmRouteCacheInitializer.timeOut();
        verify(alarmRoutesHolder).addAlarmRoute(Matchers.anyLong(), (AlarmRoute) Matchers.anyObject());
        verify(alarmRoutesHolder).addAlarmRouteAssociation(Matchers.anyLong(), (AlarmRouteAssociationData) Matchers.anyObject());
    }

}
