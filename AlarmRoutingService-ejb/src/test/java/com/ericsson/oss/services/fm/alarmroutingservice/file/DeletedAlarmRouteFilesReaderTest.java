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

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import javax.transaction.SystemException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.ericsson.oss.itpf.sdk.resources.Resource;
import com.ericsson.oss.services.fm.alarmroutingservice.configuration.ConfigurationChangeListener;
import com.ericsson.oss.services.fm.alarmroutingservice.dps.DpsHelper;

@RunWith(MockitoJUnitRunner.class)
public class DeletedAlarmRouteFilesReaderTest {

    @InjectMocks
    private DeletableAlarmRouteFilesProvider deletedAlarmRouteFilesReader;

    @Mock
    private JcaDirectoryResource directoryResource;

    @Mock
    private ConfigurationChangeListener configurationChangeListener;

    @Mock
    private DpsHelper dpsHelper;

    @Mock
    private Resource resource;

    @Test
    public void test_getOldFilesList() throws IllegalStateException, SecurityException, SystemException {

        final ArrayList<Resource> resources = new ArrayList<Resource>();
        resources.add(resource);
        when(directoryResource.getFileList()).thenReturn(resources);
        when(resource.getName()).thenReturn("testfile.txt");
        when(directoryResource.getLastModification(resource)).thenReturn(262800000L);
        when(configurationChangeListener.getDeletedAlarmRouteFileRetentionPeriod()).thenReturn(72);
        assertTrue(!deletedAlarmRouteFilesReader.fetchOldFilesToBeDeleted(new ArrayList<Resource>()).isEmpty());
    }

}
