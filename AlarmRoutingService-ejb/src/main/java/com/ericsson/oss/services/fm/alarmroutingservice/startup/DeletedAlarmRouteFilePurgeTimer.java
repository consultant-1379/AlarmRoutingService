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

import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.FINAL_ALARM_ROUTE_LOCATION;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.MILLISEC_IN_ONE_HOUR;
import static com.ericsson.oss.services.fm.common.constants.GeneralConstants.SLASH_DELIMITER;

import java.util.ArrayList;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.services.fm.alarmroutingservice.cluster.AlarmRoutingServiceClusterListener;
import com.ericsson.oss.services.fm.alarmroutingservice.configuration.ConfigurationChangeListener;
import com.ericsson.oss.services.fm.alarmroutingservice.file.DeletableAlarmRouteFilesProvider;
import com.ericsson.oss.services.fm.alarmroutingservice.file.JcaDirectoryResource;

/**
 * Class responsible for purging the alarm route files which are not present in database and exists in SFS location for more than the configured
 * retention period.Purging will be done by master instance.
 */
@Singleton
@Startup
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class DeletedAlarmRouteFilePurgeTimer {
    private static final Logger LOGGER = LoggerFactory.getLogger(DeletedAlarmRouteFilePurgeTimer.class);
    private static final Long INTERVAL_TIME = 60L;
    private Timer deletedAlarmRouteFilePurgeTimer;

    @Inject
    private JcaDirectoryResource directoryResource;

    @Inject
    private ConfigurationChangeListener configurationChangeListener;

    @Resource
    private TimerService timerService;

    @Inject
    private DeletableAlarmRouteFilesProvider deletableAlarmRouteFilesProvider;

    @Inject
    private AlarmRoutingServiceClusterListener alarmRoutingServiceClusterListener;

    @PostConstruct
    public void init() {
        startTimer(configurationChangeListener.getDeletedAlarmRouteFilePurgeInterval());
    }

    public void startTimer(final Integer timeInterval) {
        LOGGER.debug("started timer to purge the Alarm Route files");
        createTimer(getMillisecondsFromHours(timeInterval));
    }

    @Timeout
    public void timeout() {
        if (alarmRoutingServiceClusterListener.getMasterState()) {
            ArrayList<com.ericsson.oss.itpf.sdk.resources.Resource> resourceList = new ArrayList<com.ericsson.oss.itpf.sdk.resources.Resource>();
            LOGGER.info("Purging Alarm Route files at the StartUp in master instance.");
            try {
                resourceList = deletableAlarmRouteFilesProvider.fetchOldFilesToBeDeleted(resourceList);
                final String path = configurationChangeListener.getAlarmRouteFileLocation() + SLASH_DELIMITER
                        + FINAL_ALARM_ROUTE_LOCATION + SLASH_DELIMITER;
                if (!resourceList.isEmpty()) {
                    for (final com.ericsson.oss.itpf.sdk.resources.Resource toBeDeleted : resourceList) {
                        directoryResource.deleteFile(path + toBeDeleted.getName());
                    }
                }
            } catch (final Exception exception) {
                LOGGER.error("Exception occured while purging Alarm Route files : ", exception);
            }
        }
    }

    public void recreateTimerWithNewInterval(final Integer newTimeInterval) {
        if (alarmRoutingServiceClusterListener.getMasterState()) {
            stopTimers();
            startTimer(newTimeInterval);
        }
    }

    @PreDestroy
    public void stopTimers() {
        if (deletedAlarmRouteFilePurgeTimer != null) {
            deletedAlarmRouteFilePurgeTimer.cancel();
        }
    }

    private long getMillisecondsFromHours(final long timeInterval) {
        return timeInterval * MILLISEC_IN_ONE_HOUR;
    }

    private void createTimer(final long triggerTimeValue) {
        // for the first time timer is up and dps not running we will get issues. so to DPS up and running we are waiting for 10* intervalTimeValue
        // and later on triggerTimeValue.
        deletedAlarmRouteFilePurgeTimer =
                timerService.createIntervalTimer(10 * INTERVAL_TIME * 1000, triggerTimeValue, createNonPersistentTimerConfig());
    }

    private TimerConfig createNonPersistentTimerConfig() {
        final TimerConfig timerConfig = new TimerConfig();
        timerConfig.setPersistent(false);
        return timerConfig;
    }
}
