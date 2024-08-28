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

package com.ericsson.oss.services.fm.alarmroutingservice.api;

import javax.ejb.Local;

import com.ericsson.oss.itpf.sdk.core.annotation.EService;

/**
 * Local interface to initiate the export request based on the type of export bean provider.
 */
@EService
@Local
public interface RouteFileExport {

    /**
     * Alarm export query request based on the type of export bean provider type.
     *
     * @param fileName
     *        fileName of the zip file containing roiute files data
     * @param jobId
     *        job identifier of the export request
     * @param statusMessage
     *        response message
     */
    void sendRouteFileExportResponse(final String fileName, final String jobId, final String statusMessage);
}
