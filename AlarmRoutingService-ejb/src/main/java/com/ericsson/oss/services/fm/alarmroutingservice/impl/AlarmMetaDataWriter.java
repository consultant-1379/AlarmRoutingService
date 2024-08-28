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

import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.ALL;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.DELIMITER_COMMA;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.DELIMITER_SC;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.ET_ALARM_INFORMATION;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.EVENT_TYPE;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.MANAGEMENT_SYSTEM;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.MECONTEXT;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.MS_TYPE;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.NETWORK_ELEMENT;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.SP_PC_ET_VALUE_DELIMITER;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.NE_TYPE;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.OSS_FM;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.PC_ALARM_INFORMATION;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.PROBABLE_CAUSE;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.SPECIFIC_PROBLEM;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.SP_ALARM_INFORMATION;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.USERDEFINED;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.VERSION;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.VM_TYPE;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.VNFM;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.VIM_TYPE;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.VIM;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.itpf.datalayer.dps.DataBucket;
import com.ericsson.oss.itpf.datalayer.dps.persistence.ManagedObject;
import com.ericsson.oss.itpf.datalayer.dps.query.Query;
import com.ericsson.oss.itpf.datalayer.dps.query.QueryBuilder;
import com.ericsson.oss.itpf.datalayer.dps.query.QueryExecutor;
import com.ericsson.oss.itpf.datalayer.dps.query.Restriction;
import com.ericsson.oss.itpf.datalayer.dps.query.RestrictionBuilder;
import com.ericsson.oss.itpf.datalayer.dps.query.TypeRestrictionBuilder;
import com.ericsson.oss.itpf.datalayer.dps.query.projection.Projection;
import com.ericsson.oss.itpf.datalayer.dps.query.projection.ProjectionBuilder;
import com.ericsson.oss.services.fm.alarmroutingservice.util.DataPersistenceServiceProvider;
import com.ericsson.oss.services.fm.models.processedevent.AlarmMetadataInformation;

/**
 * Bean for creating or updating the alarm meta data persistent objects in DPS.
 */
@ApplicationScoped
public class AlarmMetaDataWriter {

    private static final Logger LOGGER = LoggerFactory.getLogger(AlarmMetaDataWriter.class);

    @Inject
    private DataPersistenceServiceProvider dataPersistenceServiceProvider;

    @Inject
    private AlarmMetaDataRemover alarmMetaDataRemover;

    public void updateMetaDataInformation(final AlarmMetadataInformation alarmMetadataInformation) {
        if (alarmMetadataInformation != null) {
            if (alarmMetadataInformation.getSpecificProblem() != null) {
                insertMetaDataInformation(alarmMetadataInformation, SP_ALARM_INFORMATION, SPECIFIC_PROBLEM);
            }
            if (alarmMetadataInformation.getProbableCause() != null) {
                insertMetaDataInformation(alarmMetadataInformation, PC_ALARM_INFORMATION, PROBABLE_CAUSE);
            }
            if (alarmMetadataInformation.getEventType() != null) {
                insertMetaDataInformation(alarmMetadataInformation, ET_ALARM_INFORMATION, EVENT_TYPE);
            }

            // As user defined meta data need to be overwritten by system defined, removing user defined meta data.
            if (alarmMetadataInformation.getNeType() != null && !alarmMetadataInformation.getNeType().isEmpty()
                    && !alarmMetadataInformation.getNeType().contains(USERDEFINED)) {
                final AlarmMetadataInformation deleteMetadataInformation = alarmMetadataInformation;
                deleteMetadataInformation.setNeType(USERDEFINED);
                alarmMetaDataRemover.deleteMetaData(deleteMetadataInformation);
            }
        }
    }

    private void insertMetaDataInformation(final AlarmMetadataInformation alarmMetadataInformation, final String poType, final String attributeType) {
        try {
            final DataBucket liveBucket = dataPersistenceServiceProvider.getDataPersistenceServiceInstance().getLiveBucket();
            final QueryBuilder queryBuilder = dataPersistenceServiceProvider.getDataPersistenceServiceInstance().getQueryBuilder();
            final QueryExecutor queryExecutor = liveBucket.getQueryExecutor();

            final Query<TypeRestrictionBuilder> typeQuery = queryBuilder.createTypeQuery(OSS_FM, poType);
            final RestrictionBuilder restrictionBuilder = typeQuery.getRestrictionBuilder();
            final String attributeValue = alarmMetadataInformation.getAttribute(attributeType);

            if (alarmMetadataInformation.getNeType() == null || alarmMetadataInformation.getNeType().isEmpty()) {
                LOGGER.debug("Setting up neType for the ENM alarm meta data information {}", alarmMetadataInformation);
                setNeTypeForEnmAlarms(alarmMetadataInformation, attributeType, queryExecutor, typeQuery, attributeValue);
                if (!ALL.equals(alarmMetadataInformation.getNeType())) {
                    return;
                }
            }
            LOGGER.debug("Before inserting, poType: {} ,attributeType: {}, attributeValue: {} ", poType, attributeType, attributeValue);
            insertMetaData(alarmMetadataInformation, poType, attributeType, typeQuery, restrictionBuilder, attributeValue);
        } catch (final Exception exception) {
            LOGGER.error("Exception occurred while inserting alarm meta data information: {}", exception.getMessage());
            throw new RuntimeException("Failed to process update alarm meta information request. Exception details are: " + exception.getMessage());
        }
    }

    /**
     * Checks if POs for each meta attribute value are present in DPS and creates new PO if not exists.
     */
    private void insertMetaData(final AlarmMetadataInformation alarmMetadataInformation, final String poType, final String attributeType,
            final Query<TypeRestrictionBuilder> typeQuery, final RestrictionBuilder restrictionBuilder, final String attributeValue) {
        final DataBucket liveBucket = dataPersistenceServiceProvider.getDataPersistenceServiceInstance().getLiveBucket();
        final QueryExecutor queryExecutor = liveBucket.getQueryExecutor();
        Restriction metaDataRestrition;
        final String[] metaDataAttributes = attributeValue.split(SP_PC_ET_VALUE_DELIMITER);
        for (final String metaDataAttribute : metaDataAttributes) {
            if (metaDataAttribute != null && !metaDataAttribute.isEmpty()) {
                metaDataRestrition = typeQuery.getRestrictionBuilder().equalTo(attributeType, metaDataAttribute);
                final List<Restriction> metaDataRestritions = new ArrayList<Restriction>();
                metaDataRestritions
                        .addAll(prepareNeTypeRestriction(alarmMetadataInformation.getNeType(), metaDataRestrition, typeQuery, restrictionBuilder));

                for (int count = 0; count < metaDataRestritions.size(); count++) {
                    typeQuery.setRestriction(metaDataRestritions.get(count));
                    final Long poCount = queryExecutor.executeCount(typeQuery);

                    if (poCount == 0) {
                        final Map<String, Object> alarmMetaData = new HashMap<String, Object>();
                        alarmMetaData.put(attributeType, metaDataAttribute);
                        createPersistanceObject(liveBucket, alarmMetaData, poType, alarmMetadataInformation.getNeType());
                        LOGGER.debug("Successfully created the poType: {} with attributes: {} ", poType, alarmMetaData);
                    }
                }
            } else {
                LOGGER.warn("attributeType {} with {} is not allowed to create.", attributeType, metaDataAttribute);
            }
        }
    }

    private void createPersistanceObject(final DataBucket liveBucket, final Map<String, Object> alarmInformationMap, final String poType,
            final String neTypes) {
        final String[] neType = neTypes.split(DELIMITER_COMMA);
        for (int counter = 0; counter < neType.length; counter++) {
            alarmInformationMap.put(NE_TYPE, neType[counter]);
            liveBucket.getPersistenceObjectBuilder().namespace(OSS_FM).type(poType).addAttributes(alarmInformationMap).version(VERSION).create();
        }
    }

    /**
     * Sets ne type to ALL when the alarm meta data attribute (sp,pc or et) received is related to ENM internal alarms.
     */
    private void setNeTypeForEnmAlarms(final AlarmMetadataInformation alarmMetadataInformation, final String attributeType,
            final QueryExecutor queryExecutor, final Query<TypeRestrictionBuilder> typeQuery, final String attributeValue) {
        Restriction restriction;
        final Object[] valueArray = attributeValue.split(SP_PC_ET_VALUE_DELIMITER);
        restriction = typeQuery.getRestrictionBuilder().in(attributeType, valueArray);
        typeQuery.setRestriction(restriction);
        final Projection typeProjection = ProjectionBuilder.attribute(attributeType);
        final List<Long> poIds = queryExecutor.executeProjection(typeQuery, typeProjection);
        if (poIds.isEmpty()) {
            alarmMetadataInformation.setNeType(ALL);
            LOGGER.debug("Setting NE Type to ALL{}", alarmMetadataInformation.getNeType());
        }
    }

    private List<Restriction> prepareNeTypeRestriction(final String neTypes, final Restriction metaDataRestrition,
            final Query<TypeRestrictionBuilder> typeQuery, final RestrictionBuilder restrictionBuilder) {
        Restriction restrictionForNeType;
        final List<Restriction> restrictions = new ArrayList<Restriction>();
        for (final String neType : neTypes.split(DELIMITER_COMMA)) {
            restrictionForNeType = typeQuery.getRestrictionBuilder().equalTo(NE_TYPE, neType);
            final Restriction finalRestriction = restrictionBuilder.allOf(restrictionForNeType, metaDataRestrition);
            restrictions.add(finalRestriction);
        }
        return restrictions;
    }

    /**
     * Constructs NeType for the FDN provided.
     * @param neFdn
     *            the network elements with a delimiter ";" for which neType needs to be constructed
     * @return a map with the unique network element types as key and list of the nodes as value with the network element type.
     */
    public Map<String, List<String>> constructNeType(final String neFdn) {
        LOGGER.debug("Constuct neType for the neFdn: {}", neFdn);
        final DataBucket liveBucket = dataPersistenceServiceProvider.getDataPersistenceServiceInstance().getLiveBucket();
        final Map<String, List<String>> neTypes = new HashMap<String, List<String>>();
        ManagedObject managedObject;
        String[] nodeArray = null;
        final List<String> nodes = new ArrayList<String>();
        if (neFdn.contains(MECONTEXT) && neFdn.contains(DELIMITER_SC)) {
            nodeArray = neFdn.split(DELIMITER_SC);
        } else if ((neFdn.contains(NETWORK_ELEMENT) && neFdn.contains(DELIMITER_COMMA))
                || (neFdn.contains(VNFM) && neFdn.contains(DELIMITER_COMMA)) ||(neFdn.contains(VIM) && neFdn.contains(DELIMITER_COMMA)) ) {
            nodeArray = neFdn.split(DELIMITER_COMMA);
        } else {
            nodeArray = new String[] { neFdn };
        }

        for (final String objectOfReference : nodeArray) {
            managedObject = liveBucket.findMoByFdn(objectOfReference);
            if (managedObject != null) {
                String neType = "";
                if (managedObject.getFdn().contains(MANAGEMENT_SYSTEM)) {
                    neType = managedObject.getAttribute(MS_TYPE);
                } else {
                    if (managedObject.getFdn().contains(VNFM)) {
                        neType = managedObject.getAttribute(VM_TYPE);
                    }
                    else if (managedObject.getFdn().contains(VIM))
                    {
                        neType = managedObject.getAttribute(VIM_TYPE);
                    }
                    else {
                        neType = managedObject.getAttribute(NE_TYPE);
                    }
                }
                if (neTypes.get(neType) != null && !neTypes.get(neType).contains(objectOfReference)) {
                    neTypes.get(neType).add(objectOfReference);
                } else {
                    nodes.add(objectOfReference);
                    neTypes.put(neType, nodes);
                }
            }
        }
        return neTypes;
    }
}
