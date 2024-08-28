package com.ericsson.oss.services.fm.alarmroutingservice.impl;

import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.ET_ALARM_INFORMATION;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.OSS_FM;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.PC_ALARM_INFORMATION;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.SP_ALARM_INFORMATION;
import static org.mockito.Mockito.when;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.ericsson.oss.itpf.datalayer.dps.DataBucket;
import com.ericsson.oss.itpf.datalayer.dps.DataPersistenceService;
import com.ericsson.oss.itpf.datalayer.dps.object.builder.PersistenceObjectBuilder;
import com.ericsson.oss.itpf.datalayer.dps.query.Query;
import com.ericsson.oss.itpf.datalayer.dps.query.QueryBuilder;
import com.ericsson.oss.itpf.datalayer.dps.query.QueryExecutor;
import com.ericsson.oss.itpf.datalayer.dps.query.TypeRestrictionBuilder;
import com.ericsson.oss.services.fm.alarmroutingservice.util.DataPersistenceServiceProvider;
import com.ericsson.oss.services.fm.models.processedevent.AlarmMetadataInformation;

@RunWith(MockitoJUnitRunner.class)
public class AlarmMetaDataWriterTest {
	
	@InjectMocks
    private AlarmMetaDataWriter alarmMetaDataWriter;
	
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
    private PersistenceObjectBuilder persistenceObjectBuilder;

    @Mock
    private Query<TypeRestrictionBuilder> typeQuery;

    @Mock
    private TypeRestrictionBuilder restrictionBuilder;
    
    @Mock
    private AlarmMetaDataRemover alarmMetaDataRemover;
	
	 @Before
	    public void setUp() {
	        when(dataPersistenceServiceProvider.getDataPersistenceServiceInstance()).thenReturn(dataPersistenceService);
	        when(dataPersistenceService.getLiveBucket()).thenReturn(liveBucket);
	        when(dataPersistenceServiceProvider.getDataPersistenceServiceInstance().getQueryBuilder()).thenReturn(queryBuilder);
	        when(liveBucket.getQueryExecutor()).thenReturn(queryExecutor);
	        when(liveBucket.getPersistenceObjectBuilder()).thenReturn(persistenceObjectBuilder);
	        when(queryExecutor.executeCount(typeQuery)).thenReturn((long) 5);
	        when(queryBuilder.createTypeQuery(OSS_FM, SP_ALARM_INFORMATION)).thenReturn(typeQuery);
	        when(queryBuilder.createTypeQuery(OSS_FM, PC_ALARM_INFORMATION)).thenReturn(typeQuery);
	        when(queryBuilder.createTypeQuery(OSS_FM, ET_ALARM_INFORMATION)).thenReturn(typeQuery);
	        when(typeQuery.getRestrictionBuilder()).thenReturn(restrictionBuilder);
	    }
	
	 @Test
	 public void test_updateMetaDataInformation() {
		 final AlarmMetadataInformation alarmMetadataInformation = new AlarmMetadataInformation();
		 alarmMetadataInformation.setSpecificProblem("test_sp");
		 alarmMetadataInformation.setProbableCause("test_pc");
		 alarmMetadataInformation.setEventType("test_et");
		 alarmMetaDataWriter.updateMetaDataInformation(alarmMetadataInformation);
		 
	 }

}
