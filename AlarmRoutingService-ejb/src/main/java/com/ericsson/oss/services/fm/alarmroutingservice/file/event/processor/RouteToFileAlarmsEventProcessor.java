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

package com.ericsson.oss.services.fm.alarmroutingservice.file.event.processor;

import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.FILE_HEADERS;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.FILE_NAME;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.FILE_TYPE;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.services.fm.alarmroutingservice.cache.AlarmRouteAssociationData;
import com.ericsson.oss.services.fm.alarmroutingservice.cache.AlarmRoutesHolder;
import com.ericsson.oss.services.fm.alarmroutingservice.dps.DpsHelper;
import com.ericsson.oss.services.fm.alarmroutingservice.file.FileResource;
import com.ericsson.oss.services.fm.alarmroutingservice.file.FileResourceBeanProvider;
import com.ericsson.oss.services.fm.alarmroutingservice.util.ProcessedAlarmToMapConverter;
import com.ericsson.oss.services.fm.models.RouteToFileAlarmsEvent;
import com.ericsson.oss.services.fm.models.processedevent.ProcessedAlarmEvent;

/**
 * Class responsible for processing the RouteToFileAlarmsEvent and delegating to file resource bean for further processing.
 */
public class RouteToFileAlarmsEventProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(RouteToFileAlarmsEventProcessor.class);

    @Inject
    private FileResourceBeanProvider fileResourceBeanProvider;

    @Inject
    private AlarmRoutesHolder alarmRoutesHolder;

    @Inject
    private DpsHelper dpsHelper;

    public void processRouteToFileAlarmsEvent(final RouteToFileAlarmsEvent routeToFileAlarmsEvent) {
        // From route name get alarmRoute details from the Cache.
        final Map<String, Object> alarmsToBeWrittenToFile = routeToFileAlarmsEvent.getAlarmsToBeWrittenToFile();
        for (final Map.Entry<String, Object> entry : alarmsToBeWrittenToFile.entrySet()) {
            final String routeId = entry.getKey();
            // Get Route details from Cache
            final Map<String, Object> routeDetails = getAlarmRouteDetails(routeId);
            final String fileNameInRoute = (String) routeDetails.get(FILE_NAME);
            LOGGER.debug("Details retrieved for {} is {}", routeId, routeDetails);
            if (fileNameInRoute != null) {
                final List<String> alarmAttributesNames = (List<String>) routeDetails.get(FILE_HEADERS);
                final String fileType = (String) routeDetails.get(FILE_TYPE);
                // Convert ProcessedAlarmEvent object to a map.
                final List<ProcessedAlarmEvent> processedAlarmEvents = (List<ProcessedAlarmEvent>) entry.getValue();
                final List<Map<String, Object>> alarmsAsMap = convertProcessedAlarmEventsToMap(processedAlarmEvents);
                // Invoke TextFileResource.
                final FileResource fileResource = fileResourceBeanProvider.getImplementationBean(fileType);
                fileResource.delegate(alarmsAsMap, alarmAttributesNames, fileNameInRoute);
            } else {
                LOGGER.warn("fileNameInRoute doesn't exist for the route ID : {}. Alarms are not processed further", routeId);
            }
        }
    }

    private Map<String, Object> getAlarmRouteDetails(final String routeId) {
        final Long routeIdentifier = Long.parseLong(routeId);
        AlarmRouteAssociationData alarmRouteAssociationData = alarmRoutesHolder.getAlarmRouteAssociation(routeIdentifier);
        if (alarmRouteAssociationData == null) {
            alarmRouteAssociationData = dpsHelper.fetchAlarmRouteFileAssociationData(routeIdentifier);
            alarmRoutesHolder.addAlarmRouteAssociation(routeIdentifier, alarmRouteAssociationData);
        }
        return alarmRouteAssociationData.getAssociationAttributes();
    }

    private List<Map<String, Object>> convertProcessedAlarmEventsToMap(final List<ProcessedAlarmEvent> processedAlarmEvents) {
        final List<Map<String, Object>> alarms = new ArrayList<Map<String, Object>>();
        final ProcessedAlarmToMapConverter processedAlarmToMapConverter = new ProcessedAlarmToMapConverter();
        for (final ProcessedAlarmEvent processedAlarmEvent : processedAlarmEvents) {
            final Map<String, Object> alarmAsMap = processedAlarmToMapConverter.prepareAttributeMap(processedAlarmEvent);
            alarms.add(alarmAsMap);
        }
        return alarms;
    }
}
