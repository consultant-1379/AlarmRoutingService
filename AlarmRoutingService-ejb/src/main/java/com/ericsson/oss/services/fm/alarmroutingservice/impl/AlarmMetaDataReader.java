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

package com.ericsson.oss.services.fm.alarmroutingservice.impl;

import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.DELIMITER_COMMA;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.ET_ALARM_INFORMATION;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.EVENT_TYPE;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.NE_TYPE;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.OSS_FM;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.PC_ALARM_INFORMATION;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.PROBABLE_CAUSE;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.SPECIFIC_PROBLEM;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.SP_ALARM_INFORMATION;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.USERDEFINED;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.itpf.datalayer.dps.DataBucket;
import com.ericsson.oss.itpf.datalayer.dps.persistence.PersistenceObject;
import com.ericsson.oss.itpf.datalayer.dps.query.Query;
import com.ericsson.oss.itpf.datalayer.dps.query.QueryBuilder;
import com.ericsson.oss.itpf.datalayer.dps.query.QueryExecutor;
import com.ericsson.oss.itpf.datalayer.dps.query.Restriction;
import com.ericsson.oss.itpf.datalayer.dps.query.TypeRestrictionBuilder;
import com.ericsson.oss.itpf.datalayer.dps.query.projection.Projection;
import com.ericsson.oss.itpf.datalayer.dps.query.projection.ProjectionBuilder;
import com.ericsson.oss.services.fm.alarmroutemanagement.models.AlarmRoute;
import com.ericsson.oss.services.fm.alarmroutingservice.cache.AlarmRoutesHolder;
import com.ericsson.oss.services.fm.alarmroutingservice.models.AlarmIdentificationData;
import com.ericsson.oss.services.fm.alarmroutingservice.util.DataPersistenceServiceProvider;
import com.ericsson.oss.services.fm.models.processedevent.AlarmMetadataInformation;

/**
 * Bean for reading the alarm meta data from DPS.
 */
@ApplicationScoped
public class AlarmMetaDataReader {

    private static final Logger LOGGER = LoggerFactory.getLogger(AlarmMetaDataReader.class);

    @Inject
    private DataPersistenceServiceProvider dataPersistenceServiceProvider;

    @Inject
    private AlarmRoutesHolder alarmRouteHolder;

    /**
     * Reads alarm metadata information for the provided
     * {@link AlarmMetadataInformation}.
     *
     * @param routeId
     *            Route Id used to extract route from cache
     * @param alarmMetaDataInformation
     *            The alarm meta data information to be retrieved from DPS.
     * @return Returns the map containing list of AlarmIdentificationData with
     *         the keys as specificProblem, probableCause, eventType
     *         respectively.
     */
    public Map<String, List<AlarmIdentificationData>> getAlarmIdentificationData(final Long routeId,
            final AlarmMetadataInformation alarmMetadataInformation) {
        final String neType = alarmMetadataInformation.getNeType();
        final Map<String, List<AlarmIdentificationData>> responseMap = new HashMap<String, List<AlarmIdentificationData>>();

        Map<String, Object> routeAttributes = new HashMap<String, Object>();
        //routeId of 0 is passed if creating route, in this case we don't attempt to pull from cache
        if (routeId != null && routeId > 0) {
            final AlarmRoute alarmRoute = alarmRouteHolder.getAlarmRoute(routeId);
            if (alarmRoute != null) {
                routeAttributes = alarmRoute.getAllAttributes();
            }
        }

        if (alarmMetadataInformation.getSpecificProblem() == null) {
            getMetaDataInformation(neType, SP_ALARM_INFORMATION, SPECIFIC_PROBLEM, responseMap,
                    (List<String>) routeAttributes.get(SPECIFIC_PROBLEM));
        }
        if (alarmMetadataInformation.getProbableCause() == null) {
            getMetaDataInformation(neType, PC_ALARM_INFORMATION, PROBABLE_CAUSE, responseMap,
                    (List<String>) routeAttributes.get(PROBABLE_CAUSE));
        }
        if (alarmMetadataInformation.getEventType() == null) {
            getMetaDataInformation(neType, ET_ALARM_INFORMATION, EVENT_TYPE, responseMap,
                    (List<String>) routeAttributes.get(EVENT_TYPE));
        }
        LOGGER.debug("Alarm meta data information for the neType: {} and count is: {}", neType,
                responseMap.values().size());
        return responseMap;
    }

    /**
     * Reads alarm meta data information for the provided neType.
     *
     * @param neType
     *            The neType for which the alarm meta data information needs to
     *            be retrieved.
     * @return Returns the map containing specific problem list, probable cause
     *         list and event type list with the keys as specific problem,
     *         probable cause and event type respectively.
     */
    public Map<String, List<String>> getMetaDataInformation(final String neType) {
        Map<String, List<String>> specificProblemMappings = new HashMap<>();
        Map<String, List<String>> probableCauseMappings = new HashMap<>();

        specificProblemMappings = getAlarmMetaDataAttributes(neType, SP_ALARM_INFORMATION, SPECIFIC_PROBLEM,
                specificProblemMappings);
        probableCauseMappings = getAlarmMetaDataAttributes(neType, PC_ALARM_INFORMATION, PROBABLE_CAUSE,
                specificProblemMappings);

        final Map<String, List<String>> eventTypeInformationMappings = getAlarmMetaDataAttributes(neType,
                ET_ALARM_INFORMATION, EVENT_TYPE, probableCauseMappings);
        LOGGER.debug("The neType :{} matched total meta data information count:{}", neType,
                eventTypeInformationMappings.values().size());
        return eventTypeInformationMappings;
    }

    private void getMetaDataInformation(final String neType, final String poType, final String attributeType,
            final Map<String, List<AlarmIdentificationData>> responseMap, final List<String> alarmMetaDataAttributes) {
        try {
            final DataBucket liveBucket = dataPersistenceServiceProvider.getDataPersistenceServiceInstance()
                    .getLiveBucket();
            final QueryBuilder queryBuilder = dataPersistenceServiceProvider.getDataPersistenceServiceInstance()
                    .getQueryBuilder();
            final Query<TypeRestrictionBuilder> typeQuery = queryBuilder.createTypeQuery(OSS_FM, poType);
            Restriction restriction;

            if (neType != null && !neType.isEmpty()) {
                if (neType.contains(DELIMITER_COMMA)) {
                    final Object[] neTypeArray = neType.split(DELIMITER_COMMA);
                    restriction = typeQuery.getRestrictionBuilder().in(NE_TYPE, neTypeArray);
                } else {
                    restriction = typeQuery.getRestrictionBuilder().equalTo(NE_TYPE, neType);
                }
                typeQuery.setRestriction(restriction);
            }

            final Projection neTypeProjection = ProjectionBuilder.attribute(NE_TYPE);
            final Projection attributeProjection = ProjectionBuilder.attribute(attributeType);
            final List<Object[]> attributeValues = liveBucket.getQueryExecutor().executeProjection(typeQuery,
                    neTypeProjection, attributeProjection);
            buildAlarmIdentificationData(attributeType, responseMap, attributeValues, alarmMetaDataAttributes);
        } catch (final Exception exception) {
            LOGGER.error("Exception occurred while fetching alarm meta data information : ", exception);
            throw new RuntimeException(
                    "Failed to fetch alarm meta data informaiton. Exception details are:" + exception.getMessage());
        }
    }

    private Map<String, List<String>> getAlarmMetaDataAttributes(final String neType, final String poType,
            final String attributeType, final Map<String, List<String>> alarmMetaDataAttributes) {
        final Iterator<PersistenceObject> poListIterator = getAlarmMetaDataPersistentObjects(neType, poType);
        final List<String> metaDataList = new ArrayList<String>();
        while (poListIterator.hasNext()) {
            final PersistenceObject po = poListIterator.next();
            final String metaDataAttributeValue = po.getAttribute(attributeType).toString();

            if (!metaDataAttributeValue.isEmpty() && !metaDataList.contains(metaDataAttributeValue)) {
                metaDataList.add(metaDataAttributeValue);
            }
        }
        if (alarmMetaDataAttributes.get(attributeType) == null) {
            alarmMetaDataAttributes.put(attributeType, metaDataList);
        }
        LOGGER.debug("Meta data information for poType :{} data count is:{}", poType,
                alarmMetaDataAttributes.values().size());
        return alarmMetaDataAttributes;
    }

    /**
     * Method fetches alarm meta data PO's from the given input(neType,POType).
     *
     * @param neType
     *            The network element type received
     * @param poType
     *            The type of meta data information PO to be retrieved
     *            (SpecificProblemInformation or ProbableCauseInformation or
     *            EventTypeInformation)
     * @return the iterator for the matching alarm meta data PO's.
     */
    private Iterator<PersistenceObject> getAlarmMetaDataPersistentObjects(final String neType, final String poType) {
        Iterator<PersistenceObject> poListIterator = null;
        try {
            final DataBucket liveBucket = dataPersistenceServiceProvider.getDataPersistenceServiceInstance()
                    .getLiveBucket();
            final QueryBuilder queryBuilder = dataPersistenceServiceProvider.getDataPersistenceServiceInstance()
                    .getQueryBuilder();
            final QueryExecutor queryExecutor = liveBucket.getQueryExecutor();

            final Query<TypeRestrictionBuilder> typeQuery = queryBuilder.createTypeQuery(OSS_FM, poType);

            Restriction neTypeRestriction;
            if (neType != null && !neType.isEmpty()) {
                LOGGER.debug("fetching available {} information for NodeType {}", poType, neType);
                if (neType.contains(DELIMITER_COMMA)) {
                    final Object[] neTypes = neType.split(DELIMITER_COMMA);
                    neTypeRestriction = typeQuery.getRestrictionBuilder().in(NE_TYPE, neTypes);
                } else {
                    neTypeRestriction = typeQuery.getRestrictionBuilder().equalTo(NE_TYPE, neType);
                }
                typeQuery.setRestriction(neTypeRestriction);
            }
            poListIterator = queryExecutor.execute(typeQuery);
        } catch (final Exception exception) {
            LOGGER.error("Exception occurred while fetching alarm meta data PO's from DPS : ", exception);
            throw new RuntimeException(
                    "Failed to fetch alarm meta data. Exception details are:" + exception.getMessage());
        }
        return poListIterator;
    }

    /**
     * @param attributeType
     *            attributeType can be specificProblem,probableCause or
     *            eventType.
     * @param attributeValues
     *            attributeValues retrieved from DPS (List of Object[]).
     * @param responseMap
     *            updates the map containing String and List of
     *            AlarmIdentificationData.
     * @return Builds the AlarmIdentificationData upon retrieving the data from
     *         DPS.
     */
    private void buildAlarmIdentificationData(final String attributeType,
            final Map<String, List<AlarmIdentificationData>> responseMap, final List<Object[]> attributeValues,
            final List<String> alarmMetaDataAttributes) {
        LOGGER.debug("Fetching Information for the attribute : {}", attributeType);
        final Set<AlarmIdentificationData> alarmIdentificationDataSet = new HashSet<AlarmIdentificationData>();
        int index = 1;

        final List<String> alarmRouteAttributes = new ArrayList<String>();

        if (alarmMetaDataAttributes != null && !alarmMetaDataAttributes.isEmpty()) {
            alarmRouteAttributes.addAll(alarmMetaDataAttributes);
        }
        for (final Object[] attributeValue : attributeValues) {
            final String nodeType = (String) attributeValue[0];
            final String attribute = (String) attributeValue[1];
            if (attribute != null && !attribute.isEmpty()) {
                final AlarmIdentificationData alarmIdentificationData = new AlarmIdentificationData();
                alarmIdentificationData.setValue(attribute);
                if (nodeType.equalsIgnoreCase(USERDEFINED)) {
                    alarmIdentificationData.setUserDefined(true);
                }
                alarmIdentificationData.setItemId(index++);
                alarmIdentificationDataSet.add(alarmIdentificationData);
                if (alarmRouteAttributes != null && !alarmRouteAttributes.isEmpty()
                        && alarmRouteAttributes.contains(attribute)) {
                    alarmRouteAttributes.remove(attribute);
                }
            }
        }
        if (alarmRouteAttributes != null && !alarmRouteAttributes.isEmpty()) {
            for (final String uniqueAttribute : alarmRouteAttributes) {
                final AlarmIdentificationData temporaryAlarmIdentificationData = new AlarmIdentificationData();
                temporaryAlarmIdentificationData.setValue(uniqueAttribute);
                temporaryAlarmIdentificationData.setItemId(index++);
                temporaryAlarmIdentificationData.setUserDefined(false);
                alarmIdentificationDataSet.add(temporaryAlarmIdentificationData);
            }
        }
        final List<AlarmIdentificationData> alarmIdentificationDataList = new ArrayList<AlarmIdentificationData>(alarmIdentificationDataSet);
        responseMap.put(attributeType, alarmIdentificationDataList);
    }
}