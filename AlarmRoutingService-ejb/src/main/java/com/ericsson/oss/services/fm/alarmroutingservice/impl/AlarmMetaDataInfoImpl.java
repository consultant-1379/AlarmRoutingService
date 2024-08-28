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

import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.services.fm.alarmroutingservice.api.AlarmMetaDataInfo;
import com.ericsson.oss.services.fm.alarmroutingservice.models.AlarmIdentificationData;
import com.ericsson.oss.services.fm.models.processedevent.AlarmMetadataInformation;

/**
 * AlarmMetaDataInfoImpl provides functionality to perform fetch,update and delete operation on alarm meta data information.
 */
@Stateless
public class AlarmMetaDataInfoImpl implements AlarmMetaDataInfo {

    private static final Logger LOGGER = LoggerFactory.getLogger(AlarmMetaDataInfoImpl.class);
    @Inject
    private AlarmMetaDataReader alarmMetaDataReader;

    @Inject
    private AlarmMetaDataWriter alarmMetaDataWriter;

    @Inject
    private AlarmMetaDataRemover alarmIdentificationDataRemover;

    @Override
    public Map<String, List<String>> get(final String neType) {
        LOGGER.debug("Request received to get  the alarm meta data information for the neType: {}", neType);
        return alarmMetaDataReader.getMetaDataInformation(neType);
    }

    @Override
    public void update(final AlarmMetadataInformation alarmMetadataInformation) {
        LOGGER.debug("Update request received with alarm meta data information: {}", alarmMetadataInformation);
        try {
            alarmMetaDataWriter.updateMetaDataInformation(alarmMetadataInformation);
        } catch (final Exception exception) {
            LOGGER.error("Exception occured while updating alarm meta data information :{}", exception.getMessage());
            throw new RuntimeException("Failed to update alarm meta data information. Exception details are:" + exception.getMessage());
        }
    }

    @Override
    public void update(final AlarmMetadataInformation alarmMetadataInformation, final String neFdn) {
        LOGGER.debug("Update request received with the alarm meta data information: {} and neFdn: {}", alarmMetadataInformation, neFdn);
        try {
            if ((alarmMetadataInformation.getNeType() == null || alarmMetadataInformation.getNeType().isEmpty())
                    && (neFdn != null && !neFdn.isEmpty())) {
                final Map<String, List<String>> neTypes = alarmMetaDataWriter.constructNeType(neFdn);
                for (final String nodeType : neTypes.keySet()) {
                    alarmMetadataInformation.setNeType(nodeType);
                    alarmMetaDataWriter.updateMetaDataInformation(alarmMetadataInformation);
                }
            } else {
                alarmMetaDataWriter.updateMetaDataInformation(alarmMetadataInformation);
            }
        } catch (final Exception exception) {
            LOGGER.error("Exception occured while updating alarm meta data information :{}", exception.getMessage());
        }
    }

    @Override
    public Map<String, List<AlarmIdentificationData>> getAlarmIdentificationData(final Long routeId,
            final AlarmMetadataInformation alarmMetadataInformation) {
        LOGGER.debug("Request to get AlarmIdentificationData with the alarmmetadata information: {}, routeId: {}", alarmMetadataInformation, routeId);
        return alarmMetaDataReader.getAlarmIdentificationData(routeId, alarmMetadataInformation);
    }

    @Override
    public void delete(final AlarmMetadataInformation alarmMetadataInformation) {
        LOGGER.info("Delete request received for with alarm meta data information: {}", alarmMetadataInformation);
        alarmIdentificationDataRemover.deleteMetaData(alarmMetadataInformation);
    }
}