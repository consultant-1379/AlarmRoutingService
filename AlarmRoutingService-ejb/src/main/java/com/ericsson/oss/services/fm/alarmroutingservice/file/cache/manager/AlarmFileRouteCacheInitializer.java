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

package com.ericsson.oss.services.fm.alarmroutingservice.file.cache.manager;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.services.fm.alarmroutingservice.file.cache.timer.AlarmFileRouteCacheReadTimer;

/**
 * A timer class for initializing the cache after a specific time interval. It will call {@link AlarmFileRouteCacheManager#initializeCache()} method
 * on timeout.
 */
@Singleton
@Startup
public class AlarmFileRouteCacheInitializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(AlarmFileRouteCacheReadTimer.class);

    private static final long WAIT_TIME = 500;

    private Timer cacheInitializerTimer;

    @Resource
    private TimerService timerService;

    @PostConstruct
    public void initialize() {
        startTimer(WAIT_TIME);
    }

    @Timeout
    public void handleTimeout(final Timer timer) {
        LOGGER.info("Initializing Cache");
        AlarmFileRouteCacheManager.getInstance().initializeCache();
        LOGGER.info("Initialized Cache.");
    }

    @PreDestroy
    public void cancelTimer() {
        if (cacheInitializerTimer != null) {
            LOGGER.info("Cancelling the cacheInitializerTimer");
            cacheInitializerTimer.cancel();
        }
    }

    private void startTimer(final long timeInterval) {
        final TimerConfig timerConfig = new TimerConfig();
        timerConfig.setPersistent(false);
        cacheInitializerTimer = timerService.createSingleActionTimer(timeInterval, timerConfig);
        LOGGER.info("The timer for initializing AlarmFileRouteCache with duration (In MilliSeconds) {}", timeInterval);
    }
}
