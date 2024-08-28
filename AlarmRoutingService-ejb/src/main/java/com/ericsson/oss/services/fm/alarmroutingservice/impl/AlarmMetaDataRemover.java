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

import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.ET_ALARM_INFORMATION;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.EVENT_TYPE;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.NE_TYPE;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.OSS_FM;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.PC_ALARM_INFORMATION;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.PROBABLE_CAUSE;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.SPECIFIC_PROBLEM;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.SP_ALARM_INFORMATION;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.SP_PC_ET_VALUE_DELIMITER;

import java.util.Iterator;

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
import com.ericsson.oss.services.fm.alarmroutingservice.util.DataPersistenceServiceProvider;
import com.ericsson.oss.services.fm.models.processedevent.AlarmMetadataInformation;

/**
 * Bean for deletion of alarm meta data persistent objects in DPS.
 */
@ApplicationScoped
public class AlarmMetaDataRemover {

    private static final Logger LOGGER = LoggerFactory.getLogger(AlarmMetaDataRemover.class);

    @Inject
    private DataPersistenceServiceProvider dataPersistenceServiceProvider;

    /**
     * Remove the alarm meta data information as per the data given in {@link AlarmMetadataInformation}.
     * @param alarmMetadataInformation
     *            The alarm meta data information to be deleted in DPS.
     */
    public void deleteMetaData(final AlarmMetadataInformation alarmMetadataInformation) {
        if (alarmMetadataInformation.getSpecificProblem() != null) {
            deleteMetaData(alarmMetadataInformation, SP_ALARM_INFORMATION, SPECIFIC_PROBLEM);
        }
        if (alarmMetadataInformation.getProbableCause() != null) {
            deleteMetaData(alarmMetadataInformation, PC_ALARM_INFORMATION, PROBABLE_CAUSE);
        }
        if (alarmMetadataInformation.getEventType() != null) {
            deleteMetaData(alarmMetadataInformation, ET_ALARM_INFORMATION, EVENT_TYPE);
        }
    }

    private void deleteMetaData(final AlarmMetadataInformation alarmMetadataInformation, final String poType, final String attributeType) {
        try {
            final DataBucket liveBucket = dataPersistenceServiceProvider.getDataPersistenceServiceInstance().getLiveBucket();
            final QueryBuilder queryBuilder = dataPersistenceServiceProvider.getDataPersistenceServiceInstance().getQueryBuilder();
            final QueryExecutor queryExecutor = liveBucket.getQueryExecutor();

            final Query<TypeRestrictionBuilder> typeQuery = queryBuilder.createTypeQuery(OSS_FM, poType);

            final String attributeValue = alarmMetadataInformation.getAttribute(attributeType);
            if (alarmMetadataInformation.getNeType() == null || alarmMetadataInformation.getNeType().isEmpty()) {
                return;
            }
            final String[] metaDataAttributes = attributeValue.split(SP_PC_ET_VALUE_DELIMITER);
            for (final String metaDataAttribute : metaDataAttributes) {
            final Restriction attributeRestriction = typeQuery.getRestrictionBuilder().equalTo(attributeType, metaDataAttribute);
            final Restriction neTypeRestriction = typeQuery.getRestrictionBuilder().equalTo(NE_TYPE, alarmMetadataInformation.getNeType());
            final Restriction metaDataRestriction = typeQuery.getRestrictionBuilder().allOf(attributeRestriction, neTypeRestriction);
            typeQuery.setRestriction(metaDataRestriction);
            final Iterator<PersistenceObject> iterator = queryExecutor.execute(typeQuery);
            while (iterator.hasNext()) {
                final PersistenceObject persistenceObject = iterator.next();
                liveBucket.deletePo(persistenceObject);
            }
            LOGGER.debug("Deleted alarm meta data information: {} of type: {}", alarmMetadataInformation, poType);
        } 
        }catch (final Exception exception) {
            LOGGER.debug("Exception occurred while deleting alarm meta data information : ", exception);
            throw new RuntimeException("Failed to process delete alarm meta information request. Exception details are:" + exception.getMessage());
        }
    }
}
