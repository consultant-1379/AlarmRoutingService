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

package com.ericsson.oss.services.fm.alarmroutingservice.api;

import java.util.List;
import java.util.Map;

import javax.ejb.Remote;

import com.ericsson.oss.itpf.sdk.core.annotation.EService;
import com.ericsson.oss.services.fm.alarmroutingservice.models.AlarmIdentificationData;
import com.ericsson.oss.services.fm.models.processedevent.AlarmMetadataInformation;

/**
 * The interface used for retrieving or updating specific problem, probable cause and event type information in DPS.
 */
@EService
@Remote
public interface AlarmMetaDataInfo {

    /**
     * Returns the map containing specific problem list, probable cause list and event type list with the keys as specific problem, probable cause and
     * event type respectively.
     * @param neType
     *            the neType for which the alarm meta data information needs to be retrieved.
     * @return return {@code Map<String, List<String>>}
     * @throws RuntimeException
     *             exception while fetching alarm meta data information.
     */
    Map<String, List<String>> get(String neType);

    /**
     * Method updates alarm meta data information in DPS.
     * @param alarmMetadataInformation
     *            The alarm meta data information to be updated in DPS.
     * @throws RuntimeException
     *             exception while fetching alarm meta data information.
     * @throws RuntimeException
     *             exception while updating alarm meta data information.
     */
    void update(AlarmMetadataInformation alarmMetadataInformation);

    /**
     * Method updates alarm meta data information along with neFdn in DPS.
     * @param alarmMetadataInformation
     *            The alarm meta data information to be updated in DPS.
     * @param neFdn
     *            The fdn of the network element.
     * @throws RuntimeException
     *             exception while updating alarm identification data.
     */
    void update(AlarmMetadataInformation alarmMetadataInformation, String neFdn);

    /**
     * Returns the map containing list of AlarmIdentificationData with the keys as specificProblem, probableCause, eventType respectively.
     * @param alarmMetadataInformation
     *            The alarm meta data information to be retrieved from DPS.
     * @return {@code Map<String,List<AlarmIdentificationData>>} alarm meta data information with keys as specificProblem, probableCause, eventType
     *         respectively.
     * @throws RuntimeException
     *             exception while fetching alarm identification data information.
     */

    Map<String, List<AlarmIdentificationData>> getAlarmIdentificationData(Long routeId, AlarmMetadataInformation alarmMetadataInformation);

    /**
     * Method remove alarm meta data information from the DPS.
     * @param alarmMetadataInformation
     *            The alarm meta data information to be deleted in DPS.
     * @throws RuntimeException
     *             exception while removing alarm meta data information.
     */
    void delete(AlarmMetadataInformation alarmMetadataInformation);

}
