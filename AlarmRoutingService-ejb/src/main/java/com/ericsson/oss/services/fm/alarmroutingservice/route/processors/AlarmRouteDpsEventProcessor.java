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

package com.ericsson.oss.services.fm.alarmroutingservice.route.processors;

import static com.ericsson.oss.services.fm.alarmroutemanagement.models.AlarmRoute.ENABLE_POLICY;
import static com.ericsson.oss.services.fm.alarmroutemanagement.models.AlarmRoute.NE_FDNS;
import static com.ericsson.oss.services.fm.alarmroutemanagement.models.AlarmRoute.SPECIFIC_PROBLEM;
import static com.ericsson.oss.services.fm.alarmroutemanagement.models.AlarmRoute.SPECIFIC_PROBLEMS;
import static com.ericsson.oss.services.fm.alarmroutemanagement.models.AlarmRoute.PROBABLE_CAUSE;
import static com.ericsson.oss.services.fm.alarmroutemanagement.models.AlarmRoute.PROBABLE_CAUSES;
import static com.ericsson.oss.services.fm.alarmroutemanagement.models.AlarmRoute.EVENT_TYPE;
import static com.ericsson.oss.services.fm.alarmroutemanagement.models.AlarmRoute.EVENT_TYPES;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.ALARM_ROUTE_POLICY;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.EMAIL_DETAILS;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.FILE_DETAILS;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.itpf.datalayer.dps.notification.event.AttributeChangeData;
import com.ericsson.oss.itpf.datalayer.dps.notification.event.DpsAttributeChangedEvent;
import com.ericsson.oss.itpf.datalayer.dps.notification.event.DpsDataChangedEvent;
import com.ericsson.oss.itpf.datalayer.dps.notification.event.DpsObjectCreatedEvent;
import com.ericsson.oss.itpf.datalayer.dps.notification.event.DpsObjectDeletedEvent;
import com.ericsson.oss.services.fm.alarmroutemanagement.models.AlarmRoute;
import com.ericsson.oss.services.fm.alarmroutingservice.cache.AlarmRoutesHolder;
import com.ericsson.oss.services.fm.alarmroutingservice.instrumentation.AlarmRouteCounters;
import com.ericsson.oss.services.fm.alarmroutingservice.util.AlarmRouteBuilder;
import com.ericsson.oss.services.models.alarm.RouteType;

/**
 * Bean responsible for processing the alarm route created or updated or deleted events.
 */
@Stateless
public class AlarmRouteDpsEventProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(AlarmRouteDpsEventProcessor.class);

    @Inject
    private AlarmRoutesHolder alarmRoutesHolder;

    @Inject
    private AlarmRouteBuilder alarmRouteBuilder;

    @Inject
    private AlarmRouteAssociationProcessor alarmRouteAssociationEventsProcessor;

    /**
     * Updates the AlarmPolicyMapcache based on the alarm route event received from DPS.
     * @param dpsAttributeNotificationEvent
     *            alarm route event received from DPS channel
     */
    public void processAlarmRouteDpsEvent(final DpsDataChangedEvent dpsAttributeNotificationEvent) {
        if (dpsAttributeNotificationEvent instanceof DpsObjectCreatedEvent) {
            processAlarmRouteCreateEvent((DpsObjectCreatedEvent) dpsAttributeNotificationEvent);
        } else if (dpsAttributeNotificationEvent instanceof DpsAttributeChangedEvent) {
            processUpdateEvent((DpsAttributeChangedEvent) dpsAttributeNotificationEvent);
        } else if (dpsAttributeNotificationEvent instanceof DpsObjectDeletedEvent) {
            processAlarmRouteDeleteEvent((DpsObjectDeletedEvent) dpsAttributeNotificationEvent);
        } else {
            processAssociationEvents(dpsAttributeNotificationEvent);
        }
    }

    /**
     * Method takes {@link DpsAttributeChangedEvent} and delegate to the corresponding updateEvent processor method.
     * @param updateEvent
     *            {@link DpsAttributeChangedEvent} might be ALARM_ROUTE_POLICY,EMAIL_DETAILS ..etc
     */
    private void processUpdateEvent(final DpsAttributeChangedEvent updateEvent) {
        final String updateEventType = updateEvent.getType();
        LOGGER.info("update event type: {}", updateEventType);
        switch (updateEventType) {
            case ALARM_ROUTE_POLICY:
                processAlarmRouteUpdateEvent(updateEvent);
                break;
            case EMAIL_DETAILS:
            case FILE_DETAILS:
                alarmRouteAssociationEventsProcessor.processUpdatedEvent(updateEvent);
                break;
            default:
                break;
        }
    }

    /**
     * Creates the {@link AlarmRoutesHolder} cache entry against to event poid received as event.
     * @param createAlarmRouteEvent
     *            alarm route event received from DPS channel
     */
    private void processAlarmRouteCreateEvent(final DpsObjectCreatedEvent createAlarmRouteEvent) {
        final DpsObjectCreatedEvent dpsAttributeEvent = createAlarmRouteEvent;

        if (null == alarmRoutesHolder.getAlarmRoute(dpsAttributeEvent.getPoId())) {
            final Map<String, Object> attributeValues = dpsAttributeEvent.getAttributeValues();
            LOGGER.debug("CreateRoute event received with poid:{} and all atributes:{} ", dpsAttributeEvent.getPoId(), attributeValues);
            final AlarmRoute alarmRoute = new AlarmRoute(attributeValues);
            alarmRoute.setRouteId(dpsAttributeEvent.getPoId());
            alarmRoute.setFdns((List<String>) attributeValues.get(NE_FDNS));
            setSpPcEt(attributeValues, alarmRoute);
            alarmRoutesHolder.addAlarmRoute(dpsAttributeEvent.getPoId(), alarmRoute);
            if ((boolean) attributeValues.get(ENABLE_POLICY)) {
                AlarmRouteCounters.increaseActiveRouteCount();
                if (RouteType.FILE.equals(alarmRoute.getRouteType())) {
                    AlarmRouteCounters.increaseActiveFileRouteCount();
                }
            } else {
                AlarmRouteCounters.increaseDeActiveRouteCount();
                if (RouteType.FILE.equals(alarmRoute.getRouteType())) {
                    AlarmRouteCounters.increaseDeActiveFileRouteCount();
                }
            }
        }
    }

    /**
     * Deletes the {@link AlarmRoutesHolder} cache entry against to event poid received as event.
     * @param deleteAlarmRouteEvent
     *            alarm route event received from DPS channel
     */

    private void processAlarmRouteDeleteEvent(final DpsObjectDeletedEvent deleteAlarmRouteEvent) {
        final DpsObjectDeletedEvent dpsObjectDeleteEvent = deleteAlarmRouteEvent;
        LOGGER.debug("DeleteRoute request received with poid:{} ", deleteAlarmRouteEvent.getPoId());
        final AlarmRoute alarmRouteData = alarmRoutesHolder.getAlarmRoute(deleteAlarmRouteEvent.getPoId());
        alarmRoutesHolder.removeAlarmRoute(dpsObjectDeleteEvent.getPoId());
        alarmRoutesHolder.removeAlarmRouteAssociation(dpsObjectDeleteEvent.getPoId());
        AlarmRouteCounters.decrementDeActiveRouteCount();
        if (RouteType.FILE.equals(alarmRouteData.getRouteType())) {
            AlarmRouteCounters.decrementDeActiveFileRouteCount();
        }
    }

    /**
     * Updates the {@link AlarmRoutesHolder} cache entry against to event poid received as event.
     * @param updateAlarmRouteEvent
     *            alarm route event received from DPS channel
     */
    private void processAlarmRouteUpdateEvent(final DpsAttributeChangedEvent updateAlarmRouteEvent) {
        final DpsDataChangedEvent dpsDataChangeEvent = updateAlarmRouteEvent;
        final AlarmRoute alarmRouteData = alarmRoutesHolder.getAlarmRoute(dpsDataChangeEvent.getPoId());
        if (alarmRouteData != null) {
            final Set<AttributeChangeData> changedAttributes = updateAlarmRouteEvent.getChangedAttributes();
            final AlarmRoute changedAlarmRouteData = alarmRouteBuilder.buildAlarmRouteFromChangedAttributes(alarmRouteData, changedAttributes);

            LOGGER.debug("Alarm Route Update event received with poid: {} and Changed AlarmRouteAttributes:{} ", dpsDataChangeEvent.getPoId(),
                    changedAlarmRouteData);
            if (!alarmRoutesHolder.updateAlarmRoute(dpsDataChangeEvent.getPoId(), changedAlarmRouteData)) {
                LOGGER.warn("Update of Route failed as alarmRoute with poid {} doesn't exist ", dpsDataChangeEvent.getPoId());
            }
            if (changedAlarmRouteData.getEnablePolicy()) {
                AlarmRouteCounters.increaseActiveRouteCount();
                AlarmRouteCounters.decrementDeActiveRouteCount();
                if (RouteType.FILE.equals(alarmRouteData.getRouteType())) {
                    AlarmRouteCounters.increaseActiveFileRouteCount();
                    AlarmRouteCounters.decrementDeActiveFileRouteCount();
                }
            } else {
                AlarmRouteCounters.increaseDeActiveRouteCount();
                AlarmRouteCounters.decrementActiveRouteCount();
                if (RouteType.FILE.equals(alarmRouteData.getRouteType())) {
                    AlarmRouteCounters.increaseDeActiveFileRouteCount();
                    AlarmRouteCounters.decrementActiveFileRouteCount();
                }
            }
        } else {
            LOGGER.debug("Update of Route failed as AlarmRoute with poid : {} is not found in the AlarmRouterHolder local cache",
                    dpsDataChangeEvent.getPoId());
        }
    }

    /**
     * Delegates alarm route association event to {@link AlarmRouteAssociationProcessor} for further processing.
     * @param routeAssociationEvent
     *            alarm route association event received from DPS channel
     */
    private void processAssociationEvents(final DpsDataChangedEvent routeAssociationEvent) {
        alarmRouteAssociationEventsProcessor.processAssociationEvents(routeAssociationEvent);
    }
    
    private void setSpPcEt(final Map<String, Object> alarmRouteAttributes, final AlarmRoute alarmRoute){
    	List<String> spList = new ArrayList<>();
     	if(null != alarmRouteAttributes.get(SPECIFIC_PROBLEM)){
     		spList.addAll(Arrays.asList(alarmRouteAttributes.get(SPECIFIC_PROBLEM).toString().split(",")));
     	}
     	if(null != alarmRouteAttributes.get(SPECIFIC_PROBLEMS)){
     		spList.addAll((List<String>)alarmRouteAttributes.get(SPECIFIC_PROBLEMS));
     	}
     	alarmRoute.setSpecificProblem(spList);
     
     	List<String> etList = new ArrayList<>();
     	if(null != alarmRouteAttributes.get(EVENT_TYPE)){
     		etList.addAll(Arrays.asList(alarmRouteAttributes.get(EVENT_TYPE).toString().split(",")));
     	}
     	if(null != alarmRouteAttributes.get(EVENT_TYPES)){
     		etList.addAll((List<String>)alarmRouteAttributes.get(EVENT_TYPES));
     	}
     	alarmRoute.setEventType(etList);
     	
     	List<String> pcList = new ArrayList<>();
     	if(null != alarmRouteAttributes.get(PROBABLE_CAUSE)){
     		pcList.addAll(Arrays.asList(alarmRouteAttributes.get(PROBABLE_CAUSE).toString().split(",")));
     	}
     	if(null != alarmRouteAttributes.get(PROBABLE_CAUSES)){
     		pcList.addAll((List<String>)alarmRouteAttributes.get(PROBABLE_CAUSES));
     	}
     	alarmRoute.setProbableCause(pcList);
        
    }
}
