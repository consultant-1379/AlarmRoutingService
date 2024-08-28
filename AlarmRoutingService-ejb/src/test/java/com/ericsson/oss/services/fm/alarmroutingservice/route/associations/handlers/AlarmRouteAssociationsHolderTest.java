/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2016
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.oss.services.fm.alarmroutingservice.route.associations.handlers;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.ericsson.oss.services.fm.alarmroutingservice.util.ModelServiceHelper;

@RunWith(MockitoJUnitRunner.class)
public class AlarmRouteAssociationsHolderTest {

    @InjectMocks
    private AlarmRouteAssociationsHolder alarmRouteAssociationsHolder;
    @Mock
    private ModelServiceHelper modelServiceHelper;

    @Test
    public void test_GetRouteTypes() {
        Assert.assertNotNull(alarmRouteAssociationsHolder.getRouteTypes());
    }

    @Test
    public void test_GetAlarmRoutePolicyAssociations() {
        Assert.assertNotNull(alarmRouteAssociationsHolder.getAlarmRoutePolicyAssociations());
    }

    @Test
    public void test_GetRouteAsideEndPointNames() {
        Assert.assertNotNull(alarmRouteAssociationsHolder.getRouteAsideEndPointNames());
    }

    @Test
    public void test_GetRouteBsideEndPointNames() {
        Assert.assertNotNull(alarmRouteAssociationsHolder.getRouteAsideEndPointNames());
    }

}
