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

import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.ericsson.oss.itpf.sdk.cluster.classic.ServiceClusterBean;

public class AlarmRoutingServiceClusterBeanTest {

    @InjectMocks
    AlarmRoutingServiceClusterBean alarmRoutingServiceClusterBean;

    @Mock
    ServiceClusterBean serviceClusterBean;

    public void testJoinCluster() {

    }

}
