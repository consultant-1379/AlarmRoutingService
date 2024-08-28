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

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.itpf.sdk.cluster.classic.ServiceClusterBean;

/**
 * Singleton responsible to form AlarmRoutingService cluster.
 */
@Singleton
@Startup
public class AlarmRoutingServiceClusterBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(AlarmRoutingServiceClusterBean.class);

    private final ServiceClusterBean bean = new ServiceClusterBean("AlarmRoutingServiceCluster");

    @Inject
    private AlarmRoutingServiceClusterListener membershipChangeListener;

    @PostConstruct
    public void initialize() {
        joinCluster();
    }

    @PreDestroy
    public void preDestroy() {
        leaveCluster();
    }

    public void joinCluster() {
        LOGGER.info("About to join into AlarmRoutingServiceCluster");
        if (!bean.isClusterMember()) {
            bean.joinCluster(membershipChangeListener, null);
        }
        LOGGER.info("Successfully joined AlarmRoutingServiceCluster.");
    }

    public void leaveCluster() {
        LOGGER.info("AlarmRoutingService is about to leave the AlarmRoutingService Cluster Group");
        if (bean != null && bean.isClusterMember()) {
            bean.leaveCluster();
        }
        LOGGER.info("Successfully left AlarmRoutingServiceCluster.");
    }
}
