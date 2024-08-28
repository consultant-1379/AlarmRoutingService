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

package com.ericsson.oss.services.fm.alarmroutingservice.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.enterprise.context.ApplicationScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.services.fm.alarmroutemanagement.models.AlarmRoute;

/**
 * Responsible for managing the alarm route data in a map based on the dps events received.
 */
@ApplicationScoped
public class AlarmRoutesHolder {

    private static final Logger LOGGER = LoggerFactory.getLogger(AlarmRoutesHolder.class);

    private final Map<Long, AlarmRoute> alarmRoutes = new ConcurrentHashMap<Long, AlarmRoute>();
    // AlarmRouteId , Email/SMS/Printer association.
    private final Map<Long, AlarmRouteAssociationData> alarmRouteAssociations = new ConcurrentHashMap<Long, AlarmRouteAssociationData>();

    public void addAlarmRoute(final Long routeId, final AlarmRoute alarmRoute) {
        alarmRoutes.put(routeId, alarmRoute);
        LOGGER.debug("Added alarm route {} with routeId :{} in alarmRoutes map.", alarmRoute, routeId);
    }

    /**
     * Method takes alarm route poid and changed association attributes, and it updates in the association local cache.
     * @param routeId
     *            alarm route poid.
     * @param associationData
     *            changed association attributes.
     */
    public boolean updateAlarmRouteAssociation(final Long routeId, final Map<String, Object> associationData) {
        final AlarmRouteAssociationData alarmRouteAssociationData = alarmRouteAssociations.get(routeId);
        if (null != alarmRouteAssociationData) {
            alarmRouteAssociationData.updateAssociationAttributes(associationData);
            LOGGER.debug("Updated AlarmRouteAssociation Map with routeId: {} and alarm associationData: {}", routeId, associationData);
            return true;
        }
        return false;
    }

    /**
     * Method takes alarm route poid and changed alarmRoute attributes, and it updates in the alarm route local cache.
     * @param routeId
     *            alarm route poid.
     * @param alarmRoute
     *            changed alarm route attributes {@link AlarmRoute}
     */
    public boolean updateAlarmRoute(final Long routeId, final AlarmRoute alarmRoute) {
        if (alarmRoutes.containsKey(routeId)) {
            alarmRoutes.put(routeId, alarmRoute);
            LOGGER.debug("Updated AlarmRoutes Map with routeId: {} and  AlarmRoute data: {}", routeId, alarmRoute);
            return true;
        }
        return false;
    }

    /**
     * Method takes alarm route poid and removes alarm route and its association information from respective local caches.
     * @param routeId
     *            alarm route poid.
     */
    public boolean removeAlarmRoute(final Long routeId) {
        if (alarmRoutes.containsKey(routeId)) {
            alarmRoutes.remove(routeId);
            removeAlarmRouteAssociation(routeId);
            LOGGER.debug("Removed routeId matched entry from alarmRoutes map and its alarmRouteAssoication map for the routeId: {}", routeId);
            return true;
        }
        return false;
    }

    /**
     * Method takes alarm route poid and removes association information from respective local caches.
     * @param routeId
     *            alarm route poid.
     */
    public boolean removeAlarmRouteAssociation(final Long routeId) {
        boolean isEntryRemoved = false;
        if (alarmRouteAssociations.containsKey(routeId)) {
            alarmRouteAssociations.remove(routeId);
            LOGGER.debug("Removed routeId matched entry from alarmRouteAssoication map for the routeId: {}", routeId);
            isEntryRemoved = true;
        }
        return isEntryRemoved;
    }

    /**
     * Method takes alarm route poid and fetches alarm route information from local cache.
     * @param routeId
     *            alarm route poid.
     */
    public AlarmRoute getAlarmRoute(final Long routeId) {
        final AlarmRoute alarmRoute = alarmRoutes.get(routeId);
        LOGGER.debug("Fetched alarmRoute details :{} for the routeId: {}", alarmRoute, routeId);
        return alarmRoute;
    }

    /**
     * Method takes alarm route poid and fetches alarm route association information from local cache.
     * @param routeId
     *            alarm route poid.
     */
    public AlarmRouteAssociationData getAlarmRouteAssociation(final Long routeId) {
        final AlarmRouteAssociationData alarmRouteAssociationData = alarmRouteAssociations.get(routeId);
        LOGGER.debug("Fetched alarmRouteAssociationData details :{} for the routeId: {}", alarmRouteAssociationData, routeId);
        return alarmRouteAssociationData;
    }

    public Map<Long, AlarmRoute> getAlarmRoutes() {
        return alarmRoutes;
    }

    public Map<Long, AlarmRouteAssociationData> getAlarmRouteAssociations() {
        return alarmRouteAssociations;
    }

    /**
     * Method takes alarm route poid and its associations information {@link AlarmRouteAssociationData} and stores in local association cache.
     * @param alarmRouteId
     *            alarm route poid.
     * @param alarmRouteAssociationData
     *            alarm route association data like email details...etc.
     */
    public void addAlarmRouteAssociation(final Long alarmRouteId, final AlarmRouteAssociationData alarmRouteAssociationData) {
        alarmRouteAssociations.put(alarmRouteId, alarmRouteAssociationData);
    }

}