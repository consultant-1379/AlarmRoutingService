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

package com.ericsson.oss.services.fm.alarmroutingservice.dps;

import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.ALARM_ROUTE_POLICY;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.FILE_DETAILS;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.FILE_NAME;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.OSS_FM;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.itpf.datalayer.dps.DataBucket;
import com.ericsson.oss.itpf.datalayer.dps.persistence.PersistenceObject;
import com.ericsson.oss.itpf.datalayer.dps.query.Query;
import com.ericsson.oss.itpf.datalayer.dps.query.QueryBuilder;
import com.ericsson.oss.itpf.datalayer.dps.query.QueryExecutor;
import com.ericsson.oss.itpf.datalayer.dps.query.TypeRestrictionBuilder;
import com.ericsson.oss.services.fm.alarmroutingservice.cache.AlarmRouteAssociationData;
import com.ericsson.oss.services.fm.alarmroutingservice.util.DataPersistenceServiceProvider;

/**
 * Class for database operations.
 */
@Stateless
public class DpsHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(DpsHelper.class);

    @Inject
    private DataPersistenceServiceProvider dpsProxy;

    public List<String> fetchFileNamesFromDatabase() {
        final List<String> fileNamesInDatabase = new ArrayList<String>();
        try {
            final QueryBuilder queryBuilder = dpsProxy.getDataPersistenceServiceInstance().getQueryBuilder();
            final DataBucket liveBucket = dpsProxy.getLiveBucket();
            final Query<TypeRestrictionBuilder> typeQuery = queryBuilder.createTypeQuery(OSS_FM, ALARM_ROUTE_POLICY);
            final QueryExecutor queryExecutor = liveBucket.getQueryExecutor();
            final Iterator<PersistenceObject> alarmRoutePos = queryExecutor.execute(typeQuery);
            while (alarmRoutePos.hasNext()) {
                final PersistenceObject alarmRoutePo = alarmRoutePos.next();
                final Collection<PersistenceObject> fileRoutePos = alarmRoutePo.getAssociations(FILE_DETAILS);
                for (final PersistenceObject fileRoutePo : fileRoutePos) {
                    if (fileRoutePo.getAttribute(FILE_NAME) != null) {
                        fileNamesInDatabase.add(fileRoutePo.getAttribute(FILE_NAME).toString());
                    }
                }
            }
        } catch (final Exception exception) {
            LOGGER.error("Exception occured while fetching AlarmRoutePos and fileDetails : ", exception);
        }
        return fileNamesInDatabase;
    }

    /**
     * Method fetches the file association data for a given route ID.
     * @param routeIdentifier
     *            route ID.
     * @return File details associated to the given route.
     */
    public AlarmRouteAssociationData fetchAlarmRouteFileAssociationData(final Long routeIdentifier) {
        AlarmRouteAssociationData alarmRouteAssociationData = new AlarmRouteAssociationData(Collections.<String, Object>emptyMap());
        final DataBucket liveBucket = dpsProxy.getLiveBucket();
        final PersistenceObject alarmRoutePo = liveBucket.findPoById(routeIdentifier);
        Collection<PersistenceObject> fileDetailsPos = null;
        if (alarmRoutePo != null) {
            fileDetailsPos = alarmRoutePo.getAssociations(FILE_DETAILS);
        }
        if (fileDetailsPos != null) {
            alarmRouteAssociationData = new AlarmRouteAssociationData(fileDetailsPos.iterator().next().getAllAttributes());
        }
        return alarmRouteAssociationData;
    }
}
