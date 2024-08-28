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

package com.ericsson.oss.services.fm.alarmroutingservice.file;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import javax.transaction.UserTransaction;

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
import com.ericsson.oss.itpf.datalayer.dps.query.Restriction;
import com.ericsson.oss.itpf.datalayer.dps.query.TypeRestrictionBuilder;
import com.ericsson.oss.itpf.sdk.resources.Resource;
import com.ericsson.oss.services.fm.alarmroutingservice.configuration.ConfigurationChangeListener;
import com.ericsson.oss.services.fm.alarmroutingservice.dps.DpsHelper;
import com.ericsson.oss.services.fm.alarmroutingservice.util.DataPersistenceServiceProvider;

@RunWith(MockitoJUnitRunner.class)
public class JcaDirectoryResourceTest {

    @InjectMocks
    private JcaDirectoryResource directoryResource;

    @Mock
    private Resource resource;

    @Mock
    private UserTransaction userTransaction;

    @Mock
    private ConfigurationChangeListener configurationChangeListener;

    @Mock
    private ResourceRetryManager resourceRetryManager;

    @Mock
    private DpsHelper dpsHelper;

    @Mock
    private DataPersistenceServiceProvider dpsProxy;

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

    private final String alarmRouteFileLocation = "/ericsson/netlog/fm/alarmroute";

    @Test
    public void test_getFilesList() {
        final ArrayList<Resource> resources = new ArrayList<Resource>();
        resources.add(resource);
        when(configurationChangeListener.getAlarmRouteFileLocation()).thenReturn(alarmRouteFileLocation);
        when(resourceRetryManager.tryToGetResource((String) Matchers.anyObject())).thenReturn(resource);
        when(resource.listFiles()).thenReturn(resources);
        assertTrue(!directoryResource.getFileList().isEmpty());

    }

    @Test
    public void test_deleteFile() {
        when(resourceRetryManager.tryToGetResource((String) Matchers.anyObject())).thenReturn(resource);
        when(resource.exists()).thenReturn(true);
        when(resource.delete()).thenReturn(true);
        directoryResource.deleteFile(alarmRouteFileLocation + "/data/test.txt");
    }

    @Test
    public void test_getLastModification() {
        when(configurationChangeListener.getAlarmRouteFileLocation()).thenReturn(alarmRouteFileLocation);
        when(resourceRetryManager.tryToGetResource((String) Matchers.anyObject())).thenReturn(resource);
        when(resource.getName()).thenReturn("test.txt");
        when(resource.exists()).thenReturn(true);
        when(resource.getLastModificationTimestamp()).thenReturn(262800000L);
        assertEquals((Long) 262800000L, directoryResource.getLastModification(resource));
    }

}
