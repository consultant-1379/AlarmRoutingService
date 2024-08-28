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

package com.ericsson.oss.services.fm.alarmroutingservice.startup;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import javax.transaction.SystemException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.ericsson.oss.itpf.sdk.resources.Resource;
import com.ericsson.oss.services.fm.alarmroutingservice.cluster.AlarmRoutingServiceClusterListener;
import com.ericsson.oss.services.fm.alarmroutingservice.configuration.ConfigurationChangeListener;
import com.ericsson.oss.services.fm.alarmroutingservice.file.DeletableAlarmRouteFilesProvider;
import com.ericsson.oss.services.fm.alarmroutingservice.file.JcaDirectoryResource;

@RunWith(MockitoJUnitRunner.class)
public class DeletedAlarmRouteFilePurgeTimerTest {

    @InjectMocks
    private DeletedAlarmRouteFilePurgeTimer deletedAlarmRouteFilePurgeTimer;

    @Mock
    private JcaDirectoryResource directoryResource;

    @Mock
    private ConfigurationChangeListener configurationChangeListener;

    @Mock
    private Resource resource;

    @Mock
    private DeletableAlarmRouteFilesProvider deletedAlarmRouteFilesReader;

    @Mock
    private AlarmRoutingServiceClusterListener alarmRoutingServiceClusterListener;

    private final String alarmRouteFileLocation = "/ericsson/netlog/fm/alarmroute";

    @Test
    public void testTimeout() throws IllegalStateException, SecurityException, SystemException {
        final ArrayList<Resource> resources = new ArrayList<Resource>();
        resources.add(resource);
        when(configurationChangeListener.getAlarmRouteFileLocation()).thenReturn(alarmRouteFileLocation);
        when(deletedAlarmRouteFilesReader.fetchOldFilesToBeDeleted(new ArrayList<Resource>())).thenReturn(resources);
        when(alarmRoutingServiceClusterListener.getMasterState()).thenReturn(true);
        deletedAlarmRouteFilePurgeTimer.timeout();
        verify(deletedAlarmRouteFilesReader, times(1)).fetchOldFilesToBeDeleted(new ArrayList<Resource>());
        verify(directoryResource, times(1)).deleteFile((String) Matchers.anyObject());
    }
}
