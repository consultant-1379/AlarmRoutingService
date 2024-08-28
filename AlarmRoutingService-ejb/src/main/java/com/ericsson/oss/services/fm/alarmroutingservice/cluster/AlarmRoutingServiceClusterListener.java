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

package com.ericsson.oss.services.fm.alarmroutingservice.cluster;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.itpf.sdk.cluster.MembershipChangeEvent;
import com.ericsson.oss.itpf.sdk.cluster.classic.MembershipChangeListener;
import com.ericsson.oss.services.fm.alarmroutingservice.file.cache.timer.AlarmFileRouteCacheReadTimer;

/**
 * Listen for membership changes in the cluster.
 */
@ApplicationScoped
public class AlarmRoutingServiceClusterListener implements MembershipChangeListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(AlarmRoutingServiceClusterListener.class);

    @Inject
    private AlarmFileRouteCacheReadTimer alarmFileRouteCacheReadTimer;

    private boolean isMaster;

    public boolean getMasterState() {
        return isMaster;
    }

    @Override
    public void onMembershipChange(final MembershipChangeEvent membershipChangeEvent) {
        final boolean masterState = membershipChangeEvent.isMaster();
        if (masterState) {
            LOGGER.info("Received membership change event [{}], setting current AlarmRoutingService instance to master", masterState);
            if (!isMaster) {
                isMaster = true;
                alarmFileRouteCacheReadTimer.start();
            }
        } else {
            LOGGER.info("Received membership change event [{}], setting current AlarmRoutingService instance to redundant", masterState);
            isMaster = false;
        }
    }
}
