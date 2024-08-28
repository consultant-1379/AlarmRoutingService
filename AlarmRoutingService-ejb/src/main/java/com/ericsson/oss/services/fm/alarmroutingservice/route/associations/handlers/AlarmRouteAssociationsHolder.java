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

import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.ALARM_ROUTE_ASSOCIATIONS_VERSION;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.ALARM_ROUTE_ASSOCIATION_NAME;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.DPS_RELATIONSHIP;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.EDI_ROUTE_TYPE;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.OSS_EDT;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.OSS_FM;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.ROUTE_TYPE_VERSION;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_relationship.PrimaryTypeAssociation;
import com.ericsson.oss.services.fm.alarmroutingservice.util.ModelServiceHelper;

/**
 * AlarmRouteAssociationsHolder holds alarm route associations information and route types available in the system.
 */
@ApplicationScoped
public class AlarmRouteAssociationsHolder {

    @Inject
    private ModelServiceHelper modelServiceHelper;

    private List<String> routeTypes;
    private List<PrimaryTypeAssociation> alarmRoutePolicyAssociations;

    public List<String> getRouteTypes() {
        if (null == routeTypes || routeTypes.isEmpty()) {
            routeTypes = modelServiceHelper.readEdtModelInformation(OSS_EDT, OSS_FM, EDI_ROUTE_TYPE, ROUTE_TYPE_VERSION);
        }
        return routeTypes;
    }

    public List<PrimaryTypeAssociation> getAlarmRoutePolicyAssociations() {
        if (null == alarmRoutePolicyAssociations || alarmRoutePolicyAssociations.isEmpty()) {
            alarmRoutePolicyAssociations = modelServiceHelper.readEndpointAssociations(DPS_RELATIONSHIP, OSS_FM, ALARM_ROUTE_ASSOCIATION_NAME,
                    ALARM_ROUTE_ASSOCIATIONS_VERSION);
        }
        return alarmRoutePolicyAssociations;
    }

    public List<String> getRouteAsideEndPointNames() {
        final List<String> aSideEndPointNames = new ArrayList<String>();
        for (final PrimaryTypeAssociation primaryTypeAssociation : getAlarmRoutePolicyAssociations()) {
            aSideEndPointNames.add(primaryTypeAssociation.getASide().getEndpointName());
        }
        return aSideEndPointNames;
    }

    public Set<String> getRouteBsideEndPointNames() {
        final Set<String> bSideEndPointNames = new HashSet<String>();
        for (final PrimaryTypeAssociation primaryTypeAssociation : getAlarmRoutePolicyAssociations()) {
            bSideEndPointNames.add(primaryTypeAssociation.getBSide().getEndpointName());
        }
        return bSideEndPointNames;
    }

}
