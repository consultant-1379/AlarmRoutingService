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
import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.EVENT_TYPE;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.NE_TYPE;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.OSS_FM;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.PC_ALARM_INFORMATION;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.PROBABLE_CAUSE;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.SPECIFIC_PROBLEM;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.SP_ALARM_INFORMATION;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.USERDEFINED;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
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
import com.ericsson.oss.services.fm.alarmroutingservice.impl.AlarmMetaDataRemover;
import com.ericsson.oss.services.fm.alarmroutingservice.util.DataPersistenceServiceProvider;
import com.ericsson.oss.services.fm.models.processedevent.AlarmMetadataInformation;

@RunWith(MockitoJUnitRunner.class)
public class AlarmMetaDataRemoverTest {

    @InjectMocks
    private AlarmMetaDataRemover alarmMetaDataRemover;

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

    private AlarmMetadataInformation setUp_AlarmMetaDataInformation() {
        final AlarmMetadataInformation alarmMetadataInformation = new AlarmMetadataInformation();
        alarmMetadataInformation.setEventType("testEventType");
        alarmMetadataInformation.setNeType(USERDEFINED);
        alarmMetadataInformation.setProbableCause("testProbableCause");
        alarmMetadataInformation.setSpecificProblem("testSpecificProblem");
        return alarmMetadataInformation;
    }

    @Test
    public void testDeleteSpPcEtInfo() {

        final AlarmMetadataInformation alarmMetadataInformation = setUp_AlarmMetaDataInformation();
        when(restrictionBuilder.equalTo(NE_TYPE, alarmMetadataInformation.getNeType())).thenReturn(restriction);
        when(restrictionBuilder.equalTo(SPECIFIC_PROBLEM, "testSpecificProblem")).thenReturn(restriction);
        when(restrictionBuilder.equalTo(PROBABLE_CAUSE, "testProbableCause")).thenReturn(restriction);
        when(restrictionBuilder.equalTo(EVENT_TYPE, "testEventType")).thenReturn(restriction);
        when(restrictionBuilder.allOf(restriction, restriction)).thenReturn(restriction);
        typeQuery.setRestriction(restriction);
        when(queryExecutor.execute(typeQuery)).thenReturn(poListIterator);
        when(poListIterator.hasNext()).thenReturn(true, false);
        when(poListIterator.next()).thenReturn(persistenceObject);

        alarmMetaDataRemover.deleteMetaData(alarmMetadataInformation);
        verify(liveBucket, times(1)).deletePo(persistenceObject);
    }

}
