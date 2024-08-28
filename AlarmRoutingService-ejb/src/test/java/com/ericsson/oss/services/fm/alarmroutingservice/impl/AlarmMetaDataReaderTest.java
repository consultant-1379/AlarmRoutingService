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
package com.ericsson.oss.services.fm.alarmroutingservice.impl;

import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.ET_ALARM_INFORMATION;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.NE_TYPE;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.OSS_FM;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.PC_ALARM_INFORMATION;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.SPECIFIC_PROBLEM;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.SP_ALARM_INFORMATION;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.ericsson.oss.itpf.datalayer.dps.DataBucket;
import com.ericsson.oss.itpf.datalayer.dps.DataPersistenceService;
import com.ericsson.oss.itpf.datalayer.dps.persistence.PersistenceObject;
import com.ericsson.oss.itpf.datalayer.dps.query.Query;
import com.ericsson.oss.itpf.datalayer.dps.query.QueryBuilder;
import com.ericsson.oss.itpf.datalayer.dps.query.QueryExecutor;
import com.ericsson.oss.itpf.datalayer.dps.query.Restriction;
import com.ericsson.oss.itpf.datalayer.dps.query.TypeRestrictionBuilder;
import com.ericsson.oss.itpf.datalayer.dps.query.projection.Projection;
import com.ericsson.oss.services.fm.alarmroutemanagement.models.AlarmRoute;
import com.ericsson.oss.services.fm.alarmroutingservice.cache.AlarmRoutesHolder;
import com.ericsson.oss.services.fm.alarmroutingservice.impl.AlarmMetaDataReader;
import com.ericsson.oss.services.fm.alarmroutingservice.models.AlarmIdentificationData;
import com.ericsson.oss.services.fm.alarmroutingservice.util.DataPersistenceServiceProvider;
import com.ericsson.oss.services.fm.models.processedevent.AlarmMetadataInformation;

@RunWith(MockitoJUnitRunner.class)
public class AlarmMetaDataReaderTest {

    @InjectMocks
    private AlarmMetaDataReader alarmMetaDataReader;

    @Mock
    private AlarmRoutesHolder alarmRouteHolder;

    @Mock
    private DataPersistenceServiceProvider dataPersistenceServiceProvider;

    @Mock
    private DataPersistenceService dataPersistenceService;

    @Mock
    private DataBucket liveBucket;

    @Mock
    private QueryBuilder queryBuilder;

    @Mock
    private QueryExecutor queryExecutor;

    @Mock
    private Query<TypeRestrictionBuilder> typeQuery;

    @Mock
    private TypeRestrictionBuilder restrictionBuilder;

    @Mock
    private Restriction restriction;

    @Mock
    private Iterator<Object> poListIterator;

    @Mock
    private PersistenceObject persistenceObject;

    @Mock
    private Projection projection;

    @Before
    public void setUp() {
        when(dataPersistenceServiceProvider.getDataPersistenceServiceInstance()).thenReturn(dataPersistenceService);
        when(dataPersistenceService.getLiveBucket()).thenReturn(liveBucket);
        when(dataPersistenceServiceProvider.getDataPersistenceServiceInstance().getQueryBuilder()).thenReturn(queryBuilder);
        when(liveBucket.getQueryExecutor()).thenReturn(queryExecutor);
        when(queryBuilder.createTypeQuery(OSS_FM, SP_ALARM_INFORMATION)).thenReturn(typeQuery);
        when(queryBuilder.createTypeQuery(OSS_FM, PC_ALARM_INFORMATION)).thenReturn(typeQuery);
        when(queryBuilder.createTypeQuery(OSS_FM, ET_ALARM_INFORMATION)).thenReturn(typeQuery);
        when(typeQuery.getRestrictionBuilder()).thenReturn(restrictionBuilder);
    }

    @Test
    public void testGetData_SystemDefinedNeType() {
        final String neType = "ERBS";
        final Long routeId = 120L;
        final Object[] attributes = { "ERBS", "testSpecificProblem" };
        final List<Object[]> attributeList = new ArrayList<Object[]>();
        attributeList.add(attributes);
        when(restrictionBuilder.equalTo(NE_TYPE, neType)).thenReturn(restriction);
        typeQuery.setRestriction(restriction);
        when(
                queryExecutor.executeProjection((Query<TypeRestrictionBuilder>) Matchers.any(), (Projection) Matchers.any(),
                        (Projection) Matchers.any())).thenReturn(attributeList);
        AlarmRoute alarmRoute = new AlarmRoute();
        List<String> spList= new ArrayList<>();
        spList.add("test_sp");
        List<String> pcList= new ArrayList<>();
        pcList.add("test_pc");
        List<String> etList= new ArrayList<>();
        etList.add("test_et");
        alarmRoute.setSpecificProblem(spList);
        alarmRoute.setProbableCause(pcList);
        alarmRoute.setEventType(etList);
        final AlarmMetadataInformation alarmMetadataInformation = new AlarmMetadataInformation();
        when(alarmRouteHolder.getAlarmRoute(routeId)).thenReturn(alarmRoute);
        final Map<String, List<AlarmIdentificationData>> responseMap = alarmMetaDataReader.getAlarmIdentificationData(routeId, alarmMetadataInformation);
        final List<AlarmIdentificationData> alarmIdentificationDataList = responseMap.get(SPECIFIC_PROBLEM);
        final boolean result = alarmIdentificationDataList.get(0).isUserDefined();
        assertFalse(result);

    }

    @Test
    public void testGetData_UserDefined() {
        final String neType = "USERDEFINED";
        final Long routeId = 0L;
        final Object[] attributes = { "USERDEFINED", "testSpecificProblem" };
        final List<Object[]> attributeList = new ArrayList<Object[]>();
        attributeList.add(attributes);

        when(restrictionBuilder.equalTo(NE_TYPE, neType)).thenReturn(restriction);
        typeQuery.setRestriction(restriction);
        when(
                queryExecutor.executeProjection((Query<TypeRestrictionBuilder>) Matchers.any(), (Projection) Matchers.any(),
                        (Projection) Matchers.any())).thenReturn(attributeList);

        final AlarmMetadataInformation alarmMetadataInformation = new AlarmMetadataInformation();
        final Map<String, List<AlarmIdentificationData>> responseMap = alarmMetaDataReader.getAlarmIdentificationData(routeId, alarmMetadataInformation);
        final List<AlarmIdentificationData> alarmIdentificationDataList = responseMap.get(SPECIFIC_PROBLEM);
        final boolean result = alarmIdentificationDataList.get(0).isUserDefined();
        assertTrue(result);

    }
    
    
}