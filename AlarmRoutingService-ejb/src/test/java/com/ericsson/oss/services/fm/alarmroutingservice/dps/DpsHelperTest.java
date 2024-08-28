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

package com.ericsson.oss.services.fm.alarmroutingservice.dps;

import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.ALARM_ROUTE_POLICY;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.FILE_DETAILS;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.FILE_NAME;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.OSS_FM;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.ericsson.oss.itpf.datalayer.dps.DataBucket;
import com.ericsson.oss.itpf.datalayer.dps.DataPersistenceService;
import com.ericsson.oss.itpf.datalayer.dps.object.builder.PersistenceObjectBuilder;
import com.ericsson.oss.itpf.datalayer.dps.persistence.PersistenceObject;
import com.ericsson.oss.itpf.datalayer.dps.query.Query;
import com.ericsson.oss.itpf.datalayer.dps.query.QueryBuilder;
import com.ericsson.oss.itpf.datalayer.dps.query.QueryExecutor;
import com.ericsson.oss.itpf.datalayer.dps.query.Restriction;
import com.ericsson.oss.itpf.datalayer.dps.query.TypeRestrictionBuilder;
import com.ericsson.oss.services.fm.alarmroutingservice.util.DataPersistenceServiceProvider;

@RunWith(MockitoJUnitRunner.class)
public class DpsHelperTest {

    @InjectMocks
    private DpsHelper dpsHelper;

    @Mock
    private DataPersistenceService dataPersistenceService;

    @Mock
    private DataBucket liveBucket;

    @Mock
    private PersistenceObject persistenceObject;

    @Mock
    private PersistenceObjectBuilder persistenceObjectBuilder;

    @Mock
    private QueryBuilder queryBuilder;

    @Mock
    private Query<TypeRestrictionBuilder> typeQuery;

    @Mock
    private TypeRestrictionBuilder restrictionBuilder;

    @Mock
    private Restriction restriction;

    @Mock
    private QueryExecutor queryExecutor;

    @Mock
    private Iterator<Object> iterator;

    @Mock
    private DataPersistenceServiceProvider dpsProxy;

    @Mock
    Iterator<Object> alarmRoutePos;

    private void setUpForDatabase() {
        when(dpsProxy.getDataPersistenceServiceInstance()).thenReturn(dataPersistenceService);
        when(dpsProxy.getLiveBucket()).thenReturn(liveBucket);
        when(dataPersistenceService.getQueryBuilder()).thenReturn(queryBuilder);
        when(queryBuilder.createTypeQuery(OSS_FM, ALARM_ROUTE_POLICY)).thenReturn(typeQuery);
        when(typeQuery.getRestrictionBuilder()).thenReturn(restrictionBuilder);
        when(restrictionBuilder.equalTo(FILE_NAME, "testfile")).thenReturn(restriction);
        when(liveBucket.getQueryExecutor()).thenReturn(queryExecutor);
        when(queryExecutor.execute(typeQuery)).thenReturn(alarmRoutePos);
        when(alarmRoutePos.hasNext()).thenReturn(true).thenReturn(false);
        when(alarmRoutePos.next()).thenReturn(persistenceObject);
        final Collection<PersistenceObject> fileRoutePOs = new ArrayList<PersistenceObject>();
        fileRoutePOs.add(persistenceObject);
        when(persistenceObject.getAssociations(FILE_DETAILS)).thenReturn(fileRoutePOs);
    }

    @Test
    public void test_checkIfFileExistsInRoute() {
        setUpForDatabase();
        dpsHelper.fetchFileNamesFromDatabase();
        verify(persistenceObject, times(1)).getAttribute("fileName");
    }
}
