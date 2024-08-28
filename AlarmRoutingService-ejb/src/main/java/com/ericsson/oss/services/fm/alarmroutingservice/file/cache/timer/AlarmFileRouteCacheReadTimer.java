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

import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.services.fm.alarmroutingservice.configuration.ConfigurationChangeListener;

/**
 * A timer class for reading the cache after a specific time interval.It will call
 * {@link RouteToFileAlarmsEventBuilder#readEntriesFromCacheAndSendRouteToFileAlarmsEvent()} method on timeout.
 */
@Singleton
public class AlarmFileRouteCacheReadTimer {

    private static final Logger LOGGER = LoggerFactory.getLogger(AlarmFileRouteCacheReadTimer.class);

    private static final long MILLIS_TO_SECONDS = 1000;

    private Timer cacheReadTimer;

    @Resource
    private TimerService timerService;

    @Inject
    private ConfigurationChangeListener configurationChangesListener;

    @Inject
    private RouteToFileAlarmsEventBuilder routeToFileAlarmsEventBuilder;

    public void start() {
        startTimer(configurationChangesListener.getFileRouteCacheFlushTimeout());
    }

    @Timeout
    public void handleTimeout(final Timer timer) {
        routeToFileAlarmsEventBuilder.readEntriesFromCacheAndSendRouteToFileAlarmsEvent();
    }

    /**
     * Cancels the existing timer and recreates timer with given new duration.
     * @param newInterval
     *            Time interval in milliseconds.
     */
    public void recreateTimerWithNewInterval(final Integer newInterval) {
        cancelTimer();
        startTimer(newInterval);
    }

    @PreDestroy
    public void cancelTimer() {
        if (cacheReadTimer != null) {
            LOGGER.info("Cancelling the timer");
            cacheReadTimer.cancel();
        }
    }

    private void startTimer(final long timeInterval) {
        final TimerConfig timerConfig = new TimerConfig();
        timerConfig.setPersistent(false);
        // Default value is 200 milliseconds. Setting initial timeout as 30 seconds to make sure that timer triggers after deployment is proper.
        cacheReadTimer = timerService.createIntervalTimer(30 * MILLIS_TO_SECONDS, timeInterval, timerConfig);
        LOGGER.info("The timer for cache reading  is started with the value (In MilliSeconds) {}", timeInterval);
    }
}
