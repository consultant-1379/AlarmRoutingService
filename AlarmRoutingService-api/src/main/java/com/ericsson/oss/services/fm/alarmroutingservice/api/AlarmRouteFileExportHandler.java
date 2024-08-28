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

import javax.ejb.Remote;

import com.ericsson.oss.itpf.sdk.core.annotation.EService;
import com.ericsson.oss.services.fm.alarmroutingservice.models.AlarmRouteFileExportRequest;

/**
 * The interface used for export route file data.
 */
@EService
@Remote
public interface AlarmRouteFileExportHandler {

    /**
     * Method for managing asynchronous compression of route files.
     * @param alarmRouteFileExportRequest
     *            including: routeFileName name of route file to be exported routeFileExt extension of route file name jobId job id related to
     *            download
     * @return true if request is accepted, in this case route file compression is started
     */
    boolean export(final AlarmRouteFileExportRequest alarmRouteFileExportRequest);

    /**
     * Method checks if any export is already in progress or not for the same user and same route.
     * @param alarmRouteFileExportRequest
     *            An encapsulation of attributes required for alarm route file export.
     * @return true if export is already in progress for the same user and Route.
     */
    boolean isExportInProgress(final AlarmRouteFileExportRequest alarmRouteFileExportRequest);
}
