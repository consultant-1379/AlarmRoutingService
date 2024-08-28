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

import static com.ericsson.oss.services.fm.alarmroutemanagement.models.AlarmRoute.BEGIN_TIME;
import static com.ericsson.oss.services.fm.alarmroutemanagement.models.AlarmRoute.DAYS;
import static com.ericsson.oss.services.fm.alarmroutemanagement.models.AlarmRoute.ENABLE_POLICY;
import static com.ericsson.oss.services.fm.alarmroutemanagement.models.AlarmRoute.END_TIME;
import static com.ericsson.oss.services.fm.alarmroutemanagement.models.AlarmRoute.NAME;
import static com.ericsson.oss.services.fm.alarmroutemanagement.models.AlarmRoute.NE_FDN;
import static com.ericsson.oss.services.fm.alarmroutemanagement.models.AlarmRoute.NE_FDNS;
import static com.ericsson.oss.services.fm.alarmroutemanagement.models.AlarmRoute.OUTPUT_TYPE;
import static com.ericsson.oss.services.fm.alarmroutemanagement.models.AlarmRoute.PERCEIVED_SEVERITY;
import static com.ericsson.oss.services.fm.alarmroutemanagement.models.AlarmRoute.ROUTE_TYPE;
import static com.ericsson.oss.services.fm.alarmroutemanagement.models.AlarmRoute.SUBORDINATE_TYPE;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.EVENT_TYPES;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.PROBABLE_CAUSES;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.SPECIFIC_PROBLEMS;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.itpf.datalayer.dps.notification.event.AttributeChangeData;
import com.ericsson.oss.services.fm.alarmroutemanagement.models.AlarmRoute;
import com.ericsson.oss.services.fm.alarmroutemanagement.models.AlarmRoute.RouteSubordinateObjects;
import com.ericsson.oss.services.models.alarm.RouteType;

/**
 * AlarmRouteBuilder builds alarm route data against changeAttributes came as updates.
 */
public class AlarmRouteBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(AlarmRouteBuilder.class);

    /**
     * Method takes existing alarm route data and changed route attributes data and update the existing route with changed attribute data.
     */
    public AlarmRoute buildAlarmRouteFromChangedAttributes(final AlarmRoute alarmRouteData, final Set<AttributeChangeData> changedAttributeData) {
        for (final AttributeChangeData attributeData : changedAttributeData) {
            final String attributeName = attributeData.getName();
            final Object attributeValue = attributeData.getNewValue();
            LOGGER.debug("Changed alarm route attribute name {} and  value {} ", attributeName, attributeValue);
            if (null != attributeValue) {
                switch (attributeName) {
                    case NAME:
                        alarmRouteData.setName(attributeValue.toString());
                        break;
                    case NE_FDNS:
                        alarmRouteData.setFdns((List<String>) attributeValue);
                        break;
                    case BEGIN_TIME:
                        alarmRouteData.setBeginTime(attributeValue.toString());
                        break;
                    case ENABLE_POLICY:
                        alarmRouteData.setEnablePolicy((Boolean) attributeValue);
                        break;
                    case END_TIME:
                        alarmRouteData.setEndTime(attributeValue.toString());
                        break;
                    case OUTPUT_TYPE:
                        alarmRouteData.setOutputType(attributeValue.toString());
                        break;
                    case ROUTE_TYPE:
                        alarmRouteData.setRouteType(RouteType.valueOf((String) attributeValue));
                        break;
                    case DAYS:
                        alarmRouteData.setDays(attributeValue.toString());
                        break;
                    case SUBORDINATE_TYPE:
                        if (attributeValue.toString().equalsIgnoreCase(RouteSubordinateObjects.All_SUBORDINATES.toString())) {
                            alarmRouteData.setSubordinateType(RouteSubordinateObjects.All_SUBORDINATES);
                        } else {
                            alarmRouteData.setSubordinateType(RouteSubordinateObjects.NO_SUBORDINATES);
                        }
                        break;
                    case SPECIFIC_PROBLEMS:
                        alarmRouteData.setSpecificProblem((List<String>)attributeValue);
                        break;
                    case EVENT_TYPES:
                        alarmRouteData.setEventType((List<String>)attributeValue);
                        break;
                    case PROBABLE_CAUSES:
                        alarmRouteData.setProbableCause((List<String>)attributeValue);
                        break;
                    case PERCEIVED_SEVERITY:
                        alarmRouteData.setPerceivedSeverity(attributeValue.toString());
                        break;
                    case NE_FDN:
                        alarmRouteData.setNeFdn(attributeValue.toString());
                        break;
                    default:
                        break;
                }
            }
        }
        return alarmRouteData;
    }

    /**
     * Method takes changed alarm route association attributes data {@code Set<@link AttributeChangeData>} and returns changed alarm route association
     * fields map.
     */
    public Map<String, Object> buildAlarmRouteAssociationChangedAttributes(final Set<AttributeChangeData> changedAttributeData) {
        final Map<String, Object> alarmRouteAssociationChangedAttributes = new HashMap<String, Object>();
        for (final AttributeChangeData attributeData : changedAttributeData) {
            final String attributeName = attributeData.getName();
            final Object attributeValue = attributeData.getNewValue();
            alarmRouteAssociationChangedAttributes.put(attributeName, attributeValue);
        }
        LOGGER.debug("Changed alarm route associations attributes {} ", alarmRouteAssociationChangedAttributes);
        return alarmRouteAssociationChangedAttributes;
    }
}
