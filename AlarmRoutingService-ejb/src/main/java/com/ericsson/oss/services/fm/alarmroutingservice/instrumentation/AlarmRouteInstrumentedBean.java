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

package com.ericsson.oss.services.fm.alarmroutingservice.instrumentation;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.ericsson.oss.itpf.sdk.instrument.annotation.InstrumentedBean;
import com.ericsson.oss.itpf.sdk.instrument.annotation.MonitoredAttribute;
import com.ericsson.oss.itpf.sdk.instrument.annotation.MonitoredAttribute.CollectionType;
import com.ericsson.oss.itpf.sdk.instrument.annotation.MonitoredAttribute.Visibility;
import com.ericsson.oss.itpf.sdk.instrument.annotation.Profiled;
import com.ericsson.oss.services.fm.alarmroutingservice.cluster.AlarmRoutingServiceClusterListener;

/**
 * Bean responsible for managing the counts of AlarmRoutePolicy objects.
 */
@InstrumentedBean(description = "Alarm Routing Metrics", displayName = "Alarm Routing Metrics")
@ApplicationScoped
@Profiled
public class AlarmRouteInstrumentedBean {
    private int activeRouteCount;

    private int deActiveRouteCount;

    private int activeFileRouteCount;

    private int deActiveFileRouteCount;

    private int alarmCount;

    private int failedAlarmCount;

    @Inject
    private AlarmRoutingServiceClusterListener alarmRoutingServiceClusterListener;

    @MonitoredAttribute(displayName = "Number of Active routes for Auto-Ack", visibility = Visibility.ALL, collectionType = CollectionType.DYNAMIC)
    public int getActiveRouteCount() {
        if (alarmRoutingServiceClusterListener.getMasterState()) {
            activeRouteCount = AlarmRouteCounters.getActiveRouteCount();
        }
        return activeRouteCount;
    }

    @MonitoredAttribute(displayName = "Number of DeActivated routes for Auto-Ack", visibility = Visibility.ALL,
            collectionType = CollectionType.DYNAMIC)
    public int getDeActiveRouteCount() {
        if (alarmRoutingServiceClusterListener.getMasterState()) {
            deActiveRouteCount = AlarmRouteCounters.getDeActiveRouteCount();
        }
        return deActiveRouteCount;
    }

    @MonitoredAttribute(displayName = "Number of Active routes for Save To File", visibility = Visibility.ALL,
            collectionType = CollectionType.DYNAMIC)
    public int getActiveFileRouteCount() {
        if (alarmRoutingServiceClusterListener.getMasterState()) {
            activeFileRouteCount = AlarmRouteCounters.getActiveFileRouteCount();
        }
        return activeFileRouteCount;
    }

    @MonitoredAttribute(displayName = "Number of DeActivated routes for Save To File", visibility = Visibility.ALL,
            collectionType = CollectionType.DYNAMIC)
    public int getDeActiveFileRouteCount() {
        if (alarmRoutingServiceClusterListener.getMasterState()) {
            deActiveFileRouteCount = AlarmRouteCounters.getDeActiveFileRouteCount();
        }
        return deActiveFileRouteCount;
    }

    @MonitoredAttribute(displayName = "Number of alarms added To File", visibility = Visibility.ALL, collectionType = CollectionType.TRENDSUP)
    public int getAlarmcount() {
        alarmCount = AlarmRouteCounters.getAlarmCount();
        return alarmCount;
    }

    @MonitoredAttribute(displayName = "Number of alarms failed to add To File", visibility = Visibility.ALL, collectionType = CollectionType.TRENDSUP)
    public int getFailedAlarmcount() {
        failedAlarmCount = AlarmRouteCounters.getFailedAlarmCount();
        return failedAlarmCount;
    }

}
