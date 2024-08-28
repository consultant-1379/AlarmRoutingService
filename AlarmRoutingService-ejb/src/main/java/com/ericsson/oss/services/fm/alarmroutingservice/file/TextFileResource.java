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

import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.ALARM_END;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.CLEARED_ALARM_START;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.DATE_FORMAT;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.FILE_NAME_DELIMITER;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.MULTIPLE_HIPHEN;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.NORMAL_ALARM_START;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.STAGING_FILE_LOCATION;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.TEXT_FILE_EXTENSION;
import static com.ericsson.oss.services.fm.common.constants.AlarmAttrConstants.OBJECT_OF_REFERENCE;
import static com.ericsson.oss.services.fm.common.constants.AlarmAttrConstants.PRESENT_SEVERITY;
import static com.ericsson.oss.services.fm.common.constants.GeneralConstants.COLON_DELIMITER;
import static com.ericsson.oss.services.fm.common.constants.GeneralConstants.NEW_LINE;
import static com.ericsson.oss.services.fm.common.constants.GeneralConstants.SLASH_DELIMITER;
import static com.ericsson.oss.services.fm.common.constants.GeneralConstants.TAB_SPACE;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.itpf.sdk.core.util.ServiceIdentity;
import com.ericsson.oss.itpf.sdk.resources.Resource;
import com.ericsson.oss.services.fm.alarmroutingservice.configuration.ConfigurationChangeListener;
import com.ericsson.oss.services.fm.alarmroutingservice.instrumentation.AlarmRouteCounters;
import com.ericsson.oss.services.fm.common.util.EventSeverity;

/**
 * A FileResourceResource implementation , that writes the alarms to files in Text format.
 */
@Stateless
@TextFileResourceQualifier
public class TextFileResource implements FileResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(TextFileResource.class);

    @Inject
    private ResourceRetryManager resourceRetryManager;

    @Inject
    private ServiceIdentity serviceIdentity;

    @Inject
    private FileResourceHelper fileResourceHelper;

    @Inject
    private ConfigurationChangeListener configurationChangeListener;

    @Override
    public void delegate(final List<Map<String, Object>> alarms, final List<String> alarmAttributesNames, final String fileNameInRoute) {
        try {
            final Resource resource = resourceRetryManager.tryToGetResource(configurationChangeListener.getAlarmRouteFileLocation()
                    + STAGING_FILE_LOCATION);
            if (resource != null) {
                final StringBuilder alarmsBuilder = new StringBuilder();
                final String fileName = getFileName(fileNameInRoute, TEXT_FILE_EXTENSION);
                final String resourceUri = configurationChangeListener.getAlarmRouteFileLocation() + STAGING_FILE_LOCATION + SLASH_DELIMITER
                        + fileName;

                for (final Map<String, Object> alarm : alarms) {
                    final String severity = alarm.get(PRESENT_SEVERITY).toString();
                    final String objectOfReference = alarm.get(OBJECT_OF_REFERENCE).toString();
                    alarmAttributesNames.remove(OBJECT_OF_REFERENCE);
                    if (!EventSeverity.CLEARED.toString().equalsIgnoreCase(severity)) {
                        alarmsBuilder.append(buildAlarm(alarmAttributesNames, alarm, objectOfReference, NORMAL_ALARM_START));
                    } else {
                        alarmsBuilder.append(buildAlarm(alarmAttributesNames, alarm, objectOfReference, CLEARED_ALARM_START));
                    }
                }
                LOGGER.trace("Alarm to be written to SFS is {} ", alarmsBuilder.toString());
                fileResourceHelper.writeToFile(resource, alarmsBuilder.toString(), fileName, resourceUri);
            } else {
                LOGGER.warn("SFS location {} is not present ", configurationChangeListener.getAlarmRouteFileLocation() + STAGING_FILE_LOCATION);
            }
        } catch (final Exception exception) {
            AlarmRouteCounters.increasedFailedAlarmCount(alarms.size());
            LOGGER.error("Exception caught while writing content to files : {} ", exception.getMessage());
        }
    }

    private StringBuilder buildAlarm(final List<String> alarmAttributesNames, final Map<String, Object> alarm, final String objectOfReference,
            final String alarmType) {
        final StringBuilder alarmBuilder = new StringBuilder();
        alarmBuilder.append(alarmType);
        alarmBuilder.append(objectOfReference);
        alarmBuilder.append(MULTIPLE_HIPHEN);
        alarmBuilder.append(NEW_LINE);
        for (final String alarmAttributeName : alarmAttributesNames) {
            alarmBuilder.append(alarmAttributeName);
            alarmBuilder.append(COLON_DELIMITER);
            alarmBuilder.append(TAB_SPACE);
            alarmBuilder.append(alarm.get(alarmAttributeName));
            alarmBuilder.append(NEW_LINE);
        }
        alarmBuilder.append(ALARM_END);
        alarmBuilder.append(NEW_LINE);
        return alarmBuilder;
    }

    private String getFileName(final String fileNameInRoute, final String fileExtension) {
        final String nodeId = serviceIdentity.getNodeId();
        final String formattedDate = new SimpleDateFormat(DATE_FORMAT).format(new Date());
        final StringBuilder fileNameBuilder = new StringBuilder();
        fileNameBuilder.append(fileNameInRoute);
        fileNameBuilder.append(FILE_NAME_DELIMITER);
        fileNameBuilder.append(nodeId);
        fileNameBuilder.append(FILE_NAME_DELIMITER);
        fileNameBuilder.append(formattedDate);
        fileNameBuilder.append(fileExtension);
        return fileNameBuilder.toString();
    }

}
