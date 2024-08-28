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

package com.ericsson.oss.services.fm.alarmroutingservice.file.cache.timer;

import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.DELIMITER_UNDERSCORE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.itpf.sdk.eventbus.model.EventSender;
import com.ericsson.oss.itpf.sdk.eventbus.model.annotation.Modeled;
import com.ericsson.oss.services.fm.alarmroutingservice.configuration.ConfigurationChangeListener;
import com.ericsson.oss.services.fm.alarmroutingservice.file.cache.manager.AlarmFileRouteCacheManager;
import com.ericsson.oss.services.fm.alarmroutingservice.instrumentation.AlarmRouteCounters;
import com.ericsson.oss.services.fm.models.RouteToFileAlarmsEvent;
import com.ericsson.oss.services.fm.models.processedevent.ProcessedAlarmEvent;

/**
 * A class that manages reading or writing to cache by different threads. The process of writing to the file is done asynchronously.
 */
@ApplicationScoped
public class RouteToFileAlarmsEventBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(RouteToFileAlarmsEventBuilder.class);

    private final AtomicBoolean inProcessing = new AtomicBoolean();

    @Inject
    private ConfigurationChangeListener configurationChangeListener;

    @Inject
    @Modeled
    private EventSender<RouteToFileAlarmsEvent> routeToFileAlarmsEventSender;

    /**
     * Method that reads entries from Cache and sends modeled event with the same information to queue for further processing.
     */
    public void readEntriesFromCacheAndSendRouteToFileAlarmsEvent() {
        if (inProcessing.compareAndSet(false, true)) {
            try {
                LOGGER.debug("About to read alarms from RouteToFileAlarmsCache");
                final Map<String, ProcessedAlarmEvent> alarmsFromCache = readFromRouteToFileAlarmsCache();
                if (!alarmsFromCache.isEmpty()) {
                    sendEventForWritingToFileAndDeleteFromCache(alarmsFromCache);
                    LOGGER.debug("Read from RouteToFileAlarmsCache and sent modeled event.");
                }
            } finally {
                inProcessing.set(false);
            }
        } else {
            LOGGER.debug("Previous execution is still in progress.");
        }
    }

    private Map<String, ProcessedAlarmEvent> readFromRouteToFileAlarmsCache() {
        final int batchSize = configurationChangeListener.getFileRouteAlarmsBatchSize();
        return AlarmFileRouteCacheManager.getInstance().readFromAlarmFileRouteCache(batchSize);
    }

    private void sendEventForWritingToFileAndDeleteFromCache(final Map<String, ProcessedAlarmEvent> alarmsFromCache) {
        final Set<String> keys = alarmsFromCache.keySet();
        try {
            // Send event to new queue.
            final RouteToFileAlarmsEvent routeToFileAlarmsEvent = buildRouteToFileAlarmEvent(alarmsFromCache);
            routeToFileAlarmsEventSender.send(routeToFileAlarmsEvent);
            // remove entries from Cache if no exception in sending event to queue.
            AlarmFileRouteCacheManager.getInstance().removeEntriesFromCache(keys);
        } catch (final Exception exception) {
            AlarmRouteCounters.increasedFailedAlarmCount(alarmsFromCache.size());
            LOGGER.error("Exception in sending modeledEvent is : ", exception);
        }
    }

    private RouteToFileAlarmsEvent buildRouteToFileAlarmEvent(final Map<String, ProcessedAlarmEvent> entriesFromCache) {
        final Map<String, Object> alarmsToBeWrittenToFile = segregateEntriesBasedOnAlarmRoute(entriesFromCache);
        final RouteToFileAlarmsEvent routeToFileAlarmsEvent = new RouteToFileAlarmsEvent();
        routeToFileAlarmsEvent.setAlarmsToBeWrittenToFile(alarmsToBeWrittenToFile);
        LOGGER.debug("Event built is :{}", routeToFileAlarmsEvent);
        return routeToFileAlarmsEvent;
    }

    private Map<String, Object> segregateEntriesBasedOnAlarmRoute(final Map<String, ProcessedAlarmEvent> entriesFromCache) {
        final Map<String, Object> routeToFileAlarms = new HashMap<String, Object>();
        final Set<Entry<String, ProcessedAlarmEvent>> entrySet = entriesFromCache.entrySet();
        for (final Entry<String, ProcessedAlarmEvent> entry : entrySet) {
            final String routeId = entry.getKey().split(DELIMITER_UNDERSCORE)[0];
            final ProcessedAlarmEvent processedAlarmEvent = entry.getValue();
            List<ProcessedAlarmEvent> alarms = (List<ProcessedAlarmEvent>) routeToFileAlarms.get(routeId);
            if (alarms == null) {
                alarms = new ArrayList<ProcessedAlarmEvent>();
            }
            alarms.add(processedAlarmEvent);
            routeToFileAlarms.put(routeId, alarms);
        }
        return routeToFileAlarms;
    }

}
