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

import static com.ericsson.oss.services.fm.alarmroutemanagement.models.AlarmRoute.NE_FDNS;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.ALARM_ROUTE_POLICY;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.FM_NAMESPACE;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.itpf.datalayer.dps.DataBucket;
import com.ericsson.oss.itpf.datalayer.dps.persistence.PersistenceObject;
import com.ericsson.oss.itpf.datalayer.dps.query.Query;
import com.ericsson.oss.itpf.datalayer.dps.query.QueryBuilder;
import com.ericsson.oss.itpf.datalayer.dps.query.QueryExecutor;
import com.ericsson.oss.itpf.datalayer.dps.query.TypeRestrictionBuilder;
import com.ericsson.oss.services.fm.alarmroutemanagement.models.AlarmRoute;
import com.ericsson.oss.services.fm.alarmroutingservice.route.associations.handlers.AlarmRouteAssociationsHolder;
import com.ericsson.oss.services.fm.alarmroutingservice.util.DataPersistenceServiceProvider;

/**
 * Start up bean which read the alarm routes from DPS and load into a HashMap.
 */
@Singleton
@Startup
public class AlarmRouteCacheInitializer {

    public static final int TIME_MILLISECONDS = 1000;
    public static final int TIME_SECONDS = 120;
    private static final Logger LOGGER = LoggerFactory.getLogger(AlarmRouteCacheInitializer.class);

    private Timer timer;

    @Resource
    private TimerService timerService;

    @Inject
    private DataPersistenceServiceProvider dataPersistenceService;

    @Inject
    private AlarmRoutesHolder alarmRoutesHolder;

    @Inject
    private AlarmRouteAssociationsHolder alarmRouteAssociationsHolder;

    @PostConstruct
    public void startAlarmTimer() {
        LOGGER.info("Starting the alarmRouteCacheBuildTimer");
        timer = timerService.createSingleActionTimer((long) TIME_SECONDS * TIME_MILLISECONDS, createNonPersistentTimerConfig());
    }

    private TimerConfig createNonPersistentTimerConfig() {
        final TimerConfig timerConfig = new TimerConfig();
        timerConfig.setPersistent(false);
        return timerConfig;
    }

    @Timeout
    public void timeOut() {
        addRoutesToCache();
    }

    /**
     * Method fetches all the routes available in the DB and adding those routes into corresponding local caches.
     */
    public void addRoutesToCache() {
        Iterator<PersistenceObject> poListIterator = null;
        try {
            final DataBucket liveBucket = dataPersistenceService.getDataPersistenceServiceInstance().getLiveBucket();

            final QueryBuilder queryBuilder = dataPersistenceService.getDataPersistenceServiceInstance().getQueryBuilder();
            final Query<TypeRestrictionBuilder> typeQuery = queryBuilder.createTypeQuery(FM_NAMESPACE, ALARM_ROUTE_POLICY);

            final QueryExecutor queryExecutor = liveBucket.getQueryExecutor();
            poListIterator = queryExecutor.execute(typeQuery);
        } catch (final Exception exception) {
            if (timer != null) {
                timer.cancel();
                timer = null;
                startAlarmTimer();
            }
            LOGGER.error("Exception while fetching existing alarm routes : ", exception);
        }
        if (null != poListIterator) {
            while (poListIterator.hasNext()) {
                final PersistenceObject persistenceObject = poListIterator.next();
                add(persistenceObject);
            }
        }
    }

    private void add(final PersistenceObject alarmRoutePo) {
        final List<String> endPointNames = alarmRouteAssociationsHolder.getRouteAsideEndPointNames();

        final Map<String, Object> consolidatedRouteAttributes = alarmRoutePo.getAllAttributes();
        final AlarmRoute alarmRoute = new AlarmRoute(consolidatedRouteAttributes);
        final Long alarmRoutePoId = alarmRoutePo.getPoId();

        alarmRoute.setRouteId(alarmRoutePoId);
        alarmRoute.setFdns((List<String>) alarmRoutePo.getAttribute(NE_FDNS));
        // alarm route without associations data stored in alarm routes Map in AlarmRoutHolder.
        alarmRoutesHolder.addAlarmRoute(alarmRoutePoId, alarmRoute);

        // EmailDetails,PrinterDetail,SmsDetails
        for (final String endPointName : endPointNames) {
            final Collection<PersistenceObject> associationPos = alarmRoutePo.getAssociations(endPointName);
            final Iterator<PersistenceObject> associationIterator = associationPos.iterator();
            if (associationIterator.hasNext()) {
                final PersistenceObject associationPo = associationIterator.next();
                final AlarmRouteAssociationData associationData = new AlarmRouteAssociationData(associationPo.getAllAttributes());
                // alarm route associations data stored in alarm routes association Map in AlarmRoutHolder.
                alarmRoutesHolder.addAlarmRouteAssociation(alarmRoutePoId, associationData);
            }
        }
    }
}
