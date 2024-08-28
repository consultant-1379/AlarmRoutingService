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

import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.ALARM_END;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.CLEARED_ALARM_START;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.DATE_FORMAT;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.MULTIPLE_HIPHEN;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.NORMAL_ALARM_START;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.STAGING_FILE_LOCATION;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.TEXT_FILE_EXTENSION;
import static com.ericsson.oss.services.fm.common.constants.AlarmAttrConstants.ALARM_NUMBER;
import static com.ericsson.oss.services.fm.common.constants.AlarmAttrConstants.OBJECT_OF_REFERENCE;
import static com.ericsson.oss.services.fm.common.constants.AlarmAttrConstants.PRESENT_SEVERITY;
import static com.ericsson.oss.services.fm.common.constants.GeneralConstants.COLON_DELIMITER;
import static com.ericsson.oss.services.fm.common.constants.GeneralConstants.NEW_LINE;
import static com.ericsson.oss.services.fm.common.constants.GeneralConstants.SLASH_DELIMITER;
import static com.ericsson.oss.services.fm.common.constants.GeneralConstants.TAB_SPACE;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.ericsson.oss.itpf.sdk.core.util.ServiceIdentity;
import com.ericsson.oss.itpf.sdk.resources.Resource;
import com.ericsson.oss.services.fm.alarmroutingservice.configuration.ConfigurationChangeListener;
import com.ericsson.oss.services.fm.common.util.EventSeverity;

@RunWith(MockitoJUnitRunner.class)
public class TextFileResourceTest {

    @InjectMocks
    private TextFileResource textFileResource;

    @Mock
    private Resource resource;

    @Mock
    private ResourceRetryManager resourceRetryManager;

    @Mock
    private ServiceIdentity serviceIdentity;

    @Mock
    private FileResourceHelper fileHelper;

    @Mock
    private ConfigurationChangeListener configurationChangeListener;

    final List<Map<String, Object>> alarms = new ArrayList<Map<String, Object>>();
    final List<String> alarmAttributesNames = new ArrayList<String>();

    public void setup() {
        final Map<String, Object> alarm = new HashMap<String, Object>();
        alarm.put(OBJECT_OF_REFERENCE, OBJECT_OF_REFERENCE);
        alarm.put(ALARM_NUMBER, ALARM_NUMBER);
        alarm.put(PRESENT_SEVERITY, PRESENT_SEVERITY);
        alarmAttributesNames.add(OBJECT_OF_REFERENCE);
        alarmAttributesNames.add(ALARM_NUMBER);
        alarmAttributesNames.add(PRESENT_SEVERITY);
        alarms.add(alarm);
        when(resourceRetryManager.tryToGetResource(configurationChangeListener.getAlarmRouteFileLocation() + STAGING_FILE_LOCATION))
                .thenReturn(resource);
        when(resource.supportsWriteOperations()).thenReturn(true);
        when(serviceIdentity.getNodeId()).thenReturn("svc-1-fmhistory");
    }

    @Test
    public void testDelegate_NormalAlarm() throws Exception {
        setup();
        final String testAlarm = NORMAL_ALARM_START + OBJECT_OF_REFERENCE + MULTIPLE_HIPHEN + NEW_LINE + ALARM_NUMBER + COLON_DELIMITER
                + TAB_SPACE + ALARM_NUMBER + NEW_LINE + PRESENT_SEVERITY + COLON_DELIMITER + TAB_SPACE + PRESENT_SEVERITY + NEW_LINE + ALARM_END
                + NEW_LINE;
        final String formattedDate = new SimpleDateFormat(DATE_FORMAT).format(new Date());
        final String fileName = "fileName$!&svc-1-fmhistory$!&" + formattedDate + TEXT_FILE_EXTENSION;
        final String resourceUri = configurationChangeListener.getAlarmRouteFileLocation() + STAGING_FILE_LOCATION + SLASH_DELIMITER + fileName;
        textFileResource.delegate(alarms, alarmAttributesNames, "fileName");
        verify(fileHelper, times(1)).writeToFile(resource, testAlarm, fileName, resourceUri);
    }

    @Test
    public void testDelegate_CeaseAlarm() throws Exception {
        setup();
        final Map<String, Object> alarm1 = new HashMap<String, Object>();
        alarm1.put(OBJECT_OF_REFERENCE, OBJECT_OF_REFERENCE);
        alarm1.put(ALARM_NUMBER, ALARM_NUMBER);
        alarm1.put(PRESENT_SEVERITY, EventSeverity.CLEARED.toString());

        final List<Map<String, Object>> alarms = new ArrayList<Map<String, Object>>();
        alarms.add(alarm1);

        final String testAlarm = CLEARED_ALARM_START + OBJECT_OF_REFERENCE + MULTIPLE_HIPHEN + NEW_LINE + ALARM_NUMBER + COLON_DELIMITER
                + TAB_SPACE + ALARM_NUMBER + NEW_LINE + PRESENT_SEVERITY + COLON_DELIMITER + TAB_SPACE + EventSeverity.CLEARED.toString() + NEW_LINE
                + ALARM_END + NEW_LINE;
        final String formattedDate = new SimpleDateFormat(DATE_FORMAT).format(new Date());
        final String fileName = "fileName$!&svc-1-fmhistory$!&" + formattedDate + TEXT_FILE_EXTENSION;
        final String resourceUri = configurationChangeListener.getAlarmRouteFileLocation() + STAGING_FILE_LOCATION + SLASH_DELIMITER + fileName;
        textFileResource.delegate(alarms, alarmAttributesNames, "fileName");
        verify(fileHelper, times(1)).writeToFile(resource, testAlarm, fileName, resourceUri);
    }

}
