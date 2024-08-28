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

package com.ericsson.oss.services.fm.alarmroutingservice.route.processors;

import static com.ericsson.oss.services.fm.alarmroutemanagement.models.AlarmRoute.NE_FDNS;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.itpf.datalayer.dps.notification.event.AssociatedSideData;
import com.ericsson.oss.itpf.datalayer.dps.notification.event.DpsAssociationCreatedEvent;
import com.ericsson.oss.itpf.datalayer.dps.notification.event.DpsAssociationEvent;
import com.ericsson.oss.itpf.datalayer.dps.notification.event.DpsAssociationRemovedEvent;
import com.ericsson.oss.itpf.datalayer.dps.notification.event.DpsAttributeChangedEvent;
import com.ericsson.oss.itpf.datalayer.dps.notification.event.DpsDataChangedEvent;
import com.ericsson.oss.itpf.datalayer.dps.persistence.PersistenceObject;
import com.ericsson.oss.services.fm.alarmroutemanagement.models.AlarmRoute;
import com.ericsson.oss.services.fm.alarmroutingservice.cache.AlarmRouteAssociationData;
import com.ericsson.oss.services.fm.alarmroutingservice.cache.AlarmRoutesHolder;
import com.ericsson.oss.services.fm.alarmroutingservice.route.associations.handlers.AlarmRouteAssociationsHolder;
import com.ericsson.oss.services.fm.alarmroutingservice.util.AlarmRouteBuilder;
import com.ericsson.oss.services.fm.alarmroutingservice.util.DataPersistenceServiceProvider;

/**
 * AlarmRouteAssociationEventsProcessor process create and delete and delegate for further processing.
 */
public class AlarmRouteAssociationProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(AlarmRouteAssociationProcessor.class);

    @Inject
    private AlarmRoutesHolder alarmRoutesHolder;

    @Inject
    private DataPersistenceServiceProvider dps;

    @Inject
    private AlarmRouteBuilder alarmRouteBuilder;

    @Inject
    private AlarmRouteAssociationsHolder alarmRouteAssociationsHolder;

    /**
     * Method takes {@link DpsDataChangedEvent} and identify the association end point name forwarded to accordingly for further processing.
     * @param routeAssociationEvent
     *            The alarm rote association event received in DPS notification.
     */
    public void processAssociationEvents(final DpsDataChangedEvent routeAssociationEvent) {
        final DpsAssociationEvent dpsAssociationEvent = (DpsAssociationEvent) routeAssociationEvent;

        if (dpsAssociationEvent instanceof DpsAssociationCreatedEvent) {
            processCreateEvent((DpsAssociationCreatedEvent) dpsAssociationEvent);
        } else if (dpsAssociationEvent instanceof DpsAssociationRemovedEvent) {
            processDeleteEvent((DpsAssociationRemovedEvent) dpsAssociationEvent);
        }
    }

    /**
     * Method takes {@link DpsAssociationCreatedEvent} and creates email route with email details in the {@link AlarmRoutesHolder}.
     * @param associationEvent
     *            {@link DpsAssociationCreatedEvent}
     */
    public void processCreateEvent(final DpsAssociationCreatedEvent associationEvent) {
        final Long alarmRoutePoId = associationEvent.getPoId();
        Long associationPoId = null;
        LOGGER.debug("AlarmRoute Association create event received with poid :{} ", alarmRoutePoId);
        final Collection<AssociatedSideData> associations = associationEvent.getNewAssociations();
        final Iterator<AssociatedSideData> sideDataIterator = associations.iterator();
        if (sideDataIterator.hasNext()) {
            final AssociatedSideData association = sideDataIterator.next();
            associationPoId = association.getToPoId();
        }
        try {
            final PersistenceObject associationPo = dps.getDataPersistenceServiceInstance().getLiveBucket().findPoById(associationPoId);
            final AlarmRoute alarmRoute = alarmRoutesHolder.getAlarmRoute(alarmRoutePoId);
            // Here we are checking alarm route exist in the AlarmRoutHolder.
            // If alarm route exist in the AlarmRouteHolder, we are just updating email details in the route.
            if (null != alarmRoute) {
                if (null != associationPo) {
                    alarmRoutesHolder.addAlarmRouteAssociation(alarmRoutePoId, new AlarmRouteAssociationData(associationPo.getAllAttributes()));
                }
            } else {
                // If alarm route not exist in the AlarmRouteHolder, we are fetching alarm route from DB and create new alarm route record with all
                // the
                // details and storing in alarm routes local cache and associations stored in alarm route association local cache.
                // And storing with alarm rout poid in AlarmRouteHolder.

                final PersistenceObject alarmRoutePo = dps.getDataPersistenceServiceInstance().getLiveBucket().findPoById(alarmRoutePoId);

                if (null != alarmRoutePo) {
                    final Map<String, Object> consolidatedRouteAttributes = new HashMap<String, Object>();
                    consolidatedRouteAttributes.putAll(alarmRoutePo.getAllAttributes());

                    final AlarmRoute alarmRouteDetails = new AlarmRoute(consolidatedRouteAttributes);
                    alarmRouteDetails.setRouteId(alarmRoutePoId);
                    alarmRouteDetails.setFdns((List<String>) alarmRoutePo.getAttribute(NE_FDNS));

                    alarmRoutesHolder.addAlarmRoute(alarmRoutePoId, alarmRouteDetails);

                    if (null != associationPo) {
                        alarmRoutesHolder.addAlarmRouteAssociation(alarmRoutePoId, new AlarmRouteAssociationData(associationPo.getAllAttributes()));
                    }
                }
            }
        } catch (final Exception exception) {
            LOGGER.error("Exception occurred while processing alarm route create event : ", exception);
        }
    }

    /**
     * Method takes {@link DpsAttributeChangedEvent} and updates email route with updated email details in the {@link AlarmRoutesHolder}.
     * @param changedEvent
     *            {@link DpsAttributeChangedEvent}
     */
    public void processUpdatedEvent(final DpsAttributeChangedEvent changedEvent) {
        final Long assocaitionPoId = changedEvent.getPoId();
        LOGGER.debug("AlarmRoute Association update event received with poid {} and Changed attributes: {} ", assocaitionPoId,
                changedEvent.getChangedAttributes());
        try {
            final PersistenceObject associationPo = dps.getDataPersistenceServiceInstance().getLiveBucket().findPoById(assocaitionPoId);
            Collection<PersistenceObject> alarmRoutePo = null;
            final Iterator<String> alarmRouteAssociationsIterator = alarmRouteAssociationsHolder.getRouteBsideEndPointNames().iterator();
            if (alarmRouteAssociationsIterator.hasNext()) {
                final String associatonEndPointName = alarmRouteAssociationsIterator.next();
                alarmRoutePo = associationPo.getAssociations(associatonEndPointName);
            }
            if (null != alarmRoutePo && !alarmRoutePo.isEmpty()) {
                PersistenceObject alarmRoutePolicyPo = null;
                final Iterator<PersistenceObject> alarmRoutePoIterator = alarmRoutePo.iterator();
                if (alarmRoutePoIterator.hasNext()) {
                    final PersistenceObject alarmRoute = alarmRoutePoIterator.next();
                    alarmRoutePolicyPo = alarmRoute;
                }

                // retrieve from cache
                AlarmRoute alarmRouteData = null;
                if (alarmRoutePolicyPo != null) {
                    alarmRouteData = alarmRoutesHolder.getAlarmRoute(alarmRoutePolicyPo.getPoId());
                }
                LOGGER.debug("Existing AlarmRoute info:{}", alarmRouteData);
                if (null != alarmRouteData) {
                    final Map<String, Object> changedAlarmRoute = alarmRouteBuilder.buildAlarmRouteAssociationChangedAttributes(changedEvent
                            .getChangedAttributes());
                    if (!alarmRoutesHolder.updateAlarmRouteAssociation(alarmRoutePolicyPo.getPoId(), changedAlarmRoute)) {
                        LOGGER.warn("Update of Route failed as alarmRouteAssociation with poid {} doesn't exist ", alarmRoutePolicyPo.getPoId());
                    }
                } else {
                    LOGGER.debug("To Update a route,route not found in the AlarmRouterHolder local cache {}", changedEvent);
                }
            }
        } catch (final Exception exception) {
            LOGGER.error("Exception occurred while processing alarm route update event : ", exception);
        }
    }

    /**
     * Method takes {@link DpsAssociationRemovedEvent} and deletes email details in the {@link AlarmRoutesHolder}.
     * @param deleteEvent
     *            {@link DpsAssociationRemovedEvent}
     */
    public void processDeleteEvent(final DpsAssociationRemovedEvent deleteEvent) {
        final Long alarmRoutePoId = deleteEvent.getPoId();
        LOGGER.debug("AlarmRoute Association delete event received with poid :{} ", alarmRoutePoId);
        if (!alarmRoutesHolder.removeAlarmRouteAssociation(alarmRoutePoId)) {
            LOGGER.warn("Removimg of alarmRouteAssociation failed with poid {} doesn't exist ", alarmRoutePoId);
        }
    }
}
