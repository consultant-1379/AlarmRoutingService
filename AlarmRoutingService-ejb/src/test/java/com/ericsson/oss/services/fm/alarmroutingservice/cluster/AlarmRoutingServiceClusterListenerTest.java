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

import static org.mockito.Mockito.when;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.ericsson.oss.itpf.sdk.cluster.MembershipChangeEvent;
import com.ericsson.oss.services.fm.alarmroutingservice.file.cache.timer.AlarmFileRouteCacheReadTimer;

@RunWith(MockitoJUnitRunner.class)
public class AlarmRoutingServiceClusterListenerTest {

    @InjectMocks
    private AlarmRoutingServiceClusterListener alarmRoutingServiceClusterListener;

    @Mock
    private MembershipChangeEvent membershipChangeEvent;

    @Mock
    private AlarmFileRouteCacheReadTimer alarmFileRouteCacheReadTimer;

    @Test
    public void testOnMembershipChange() {
        when(membershipChangeEvent.isMaster()).thenReturn(true);
        alarmRoutingServiceClusterListener.onMembershipChange(membershipChangeEvent);
    }

    @Test
    public void testOnMembershipChangeFalse() {
        alarmRoutingServiceClusterListener.onMembershipChange(membershipChangeEvent);
    }

    @Test
    public void testGetMasterState() {
        final boolean masterState = alarmRoutingServiceClusterListener.getMasterState();
        Assert.assertFalse(masterState);
    }
}
