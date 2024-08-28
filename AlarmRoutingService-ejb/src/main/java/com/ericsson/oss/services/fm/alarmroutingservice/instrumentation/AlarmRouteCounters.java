/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2012
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/

package com.ericsson.oss.services.fm.alarmroutingservice.instrumentation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Alarm Routes Counter responsible for managing the counts of AlarmRoutePolicy objects.
 */
public class AlarmRouteCounters {
    private static final Logger LOGGER = LoggerFactory.getLogger(AlarmRouteInstrumentedBean.class);

    private static int activeRouteCount;

    private static int deActiveRouteCount;

    private static int activeFileRouteCount;

    private static int deActiveFileRouteCount;

    private static int alarmCount;

    private static int failedAlarmCount;

    public static int getActiveRouteCount() {
        return activeRouteCount;
    }

    public static int getDeActiveRouteCount() {
        return deActiveRouteCount;
    }

    public static int getActiveFileRouteCount() {
        return activeFileRouteCount;
    }

    public static int getDeActiveFileRouteCount() {
        return deActiveFileRouteCount;
    }

    public static int getAlarmCount() {
        return alarmCount;
    }

    public static int getFailedAlarmCount() {
        return failedAlarmCount;
    }

    public static void increaseActiveRouteCount() {
        activeRouteCount++;
        LOGGER.debug("Increased the activeRoutesCountPerMinute: {}", activeRouteCount);
    }

    public static void increaseDeActiveRouteCount() {
        deActiveRouteCount++;
        LOGGER.debug("Increased the deactiveRoutesCountPerMinute: {}", deActiveRouteCount);
    }

    public static void decrementActiveRouteCount() {
        if (activeRouteCount > 0) {
            activeRouteCount--;
        }
        LOGGER.debug("Decreased the activeRoutesCountPerMinute: {}", activeRouteCount);
    }

    public static void decrementDeActiveRouteCount() {
        if (deActiveRouteCount > 0) {
            deActiveRouteCount--;
        }
        LOGGER.debug("Decreased the deactiveRoutesCountPerMinute: {}", deActiveRouteCount);
    }

    public static void increaseActiveFileRouteCount() {
        activeFileRouteCount++;
        LOGGER.debug("Increased the activeFileRouteCountPerMinute: {}", activeFileRouteCount);
    }

    public static void increaseDeActiveFileRouteCount() {
        deActiveFileRouteCount++;
        LOGGER.debug("Increased the deActiveFileRouteCountPerMinute: {}", deActiveFileRouteCount);
    }

    public static void decrementDeActiveFileRouteCount() {
        if (deActiveFileRouteCount > 0) {
            deActiveFileRouteCount--;
        }
        LOGGER.debug("Decreased the deActiveFileRouteCountPerMinute: {}", deActiveFileRouteCount);
    }

    public static void decrementActiveFileRouteCount() {
        if (activeFileRouteCount > 0) {
            activeFileRouteCount--;
        }
        LOGGER.debug("Decreased the activeFileRouteCountPerMinute: {}", activeFileRouteCount);
    }

    public static void increasedAlarmCount(final int count) {
        alarmCount += count;
        LOGGER.debug("Increased the alarmCountPerMinute: {}", alarmCount);
    }

    public static void increasedFailedAlarmCount(final int count) {
        failedAlarmCount += count;
        LOGGER.debug("Increased the failedAlarmCountPerMinute: {}", failedAlarmCount);
    }
}
