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

import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.FDN;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.NE_TYPE;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import com.ericsson.oss.services.fm.alarmroutingservice.impl.AlarmMetaDataInfoImpl;
import com.ericsson.oss.services.fm.alarmroutingservice.impl.AlarmMetaDataReader;
import com.ericsson.oss.services.fm.alarmroutingservice.impl.AlarmMetaDataRemover;
import com.ericsson.oss.services.fm.alarmroutingservice.impl.AlarmMetaDataWriter;
import com.ericsson.oss.services.fm.models.processedevent.AlarmMetadataInformation;

@RunWith(MockitoJUnitRunner.class)
public class AlarmMetaDataInfoImplTest {

    @InjectMocks
    private final AlarmMetaDataInfoImpl alarmMetaDataInfoImpl = new AlarmMetaDataInfoImpl();

    @Mock
    private AlarmMetadataInformation alarmMetadataInformation;

    @Mock
    private AlarmMetaDataReader alarmMetaDataReader;

    @Mock
    private AlarmMetaDataWriter alarmMetaDataWriter;

    @Mock
    private AlarmMetaDataRemover alarmMetaDataRemover;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetAlarmMetaDataInfo() {
        alarmMetaDataInfoImpl.get(NE_TYPE);
        verify(alarmMetaDataReader, times(1)).getMetaDataInformation(NE_TYPE);
    }

    @Test
    public void testUpdateAlarmMetaDataInfo() {
        alarmMetaDataInfoImpl.update(alarmMetadataInformation);
        verify(alarmMetaDataWriter, times(1)).updateMetaDataInformation(alarmMetadataInformation);
    }

    @Test
    public void testUpdateAlarmMetaDataInfoForFdn() {
        final Map<String, List<String>> neTypes = new HashMap<String, List<String>>(1);
        neTypes.put(NE_TYPE, new ArrayList<String>());
        when(alarmMetaDataWriter.constructNeType(FDN)).thenReturn(neTypes);
        alarmMetaDataInfoImpl.update(alarmMetadataInformation, FDN);
        verify(alarmMetaDataWriter, times(1)).updateMetaDataInformation(alarmMetadataInformation);
    }

    @Test
    public void testUpdateAlarmMetaDataInfoForFdnNull() {
        alarmMetaDataInfoImpl.update(alarmMetadataInformation, null);
        verify(alarmMetaDataWriter, times(1)).updateMetaDataInformation(alarmMetadataInformation);
    }

    @Test
    public void testDeleteAlarmMetaDataInformation() {
        alarmMetaDataInfoImpl.delete(alarmMetadataInformation);
        verify(alarmMetaDataRemover, times(1)).deleteMetaData(alarmMetadataInformation);
    }

}
