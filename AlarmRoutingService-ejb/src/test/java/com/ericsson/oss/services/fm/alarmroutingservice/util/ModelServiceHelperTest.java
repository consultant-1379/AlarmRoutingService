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

package com.ericsson.oss.services.fm.alarmroutingservice.util;

import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.ALARM_ROUTE_ASSOCIATIONS_VERSION;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.ALARM_ROUTE_ASSOCIATION_NAME;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.DPS_RELATIONSHIP;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.EDI_ROUTE_TYPE;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.OSS_EDT;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.OSS_FM;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.ROUTE_TYPE_VERSION;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_relationship.PrimaryTypeAssociation;
import com.ericsson.oss.itpf.modeling.modelservice.ModelService;

@RunWith(MockitoJUnitRunner.class)
public class ModelServiceHelperTest {

    @InjectMocks
    private ModelServiceHelper modelServiceHelper;

    @Mock
    private ModelService modelService;

    @Test
    public void test_ReadEdtModelInformation() {
        final List<String> routTypes = modelServiceHelper.readEdtModelInformation(OSS_EDT, OSS_FM, EDI_ROUTE_TYPE, ROUTE_TYPE_VERSION);
        Assert.assertNotNull(routTypes);
    }

    @Test
    public void test_ReadEndpointAssociations() {
        final List<PrimaryTypeAssociation> alarmRoutePolicyAssociations = modelServiceHelper.readEndpointAssociations(DPS_RELATIONSHIP, OSS_FM,
                ALARM_ROUTE_ASSOCIATION_NAME, ALARM_ROUTE_ASSOCIATIONS_VERSION);
        Assert.assertNotNull(alarmRoutePolicyAssociations);
    }

}
