/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2017
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/

package com.ericsson.oss.services.fm.alarmroutingservice.file;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.ericsson.oss.itpf.sdk.resources.Resource;

@RunWith(MockitoJUnitRunner.class)
public class FileResourceHelperTest {
    @InjectMocks
    private FileResourceHelper fileResourceHelper;

    @Mock
    private Resource resource;

    @Test
    public void testWriteToFile() {
        when(resource.supportsWriteOperations()).thenReturn(true);
        fileResourceHelper.writeToFile(resource, "testAlarm", "fileNameInRoute", "testResourceUri");
        verify(resource, times(1)).write("testAlarm".getBytes(), true);

    }
}
