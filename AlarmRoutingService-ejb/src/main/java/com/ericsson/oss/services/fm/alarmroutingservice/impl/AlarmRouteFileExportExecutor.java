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

package com.ericsson.oss.services.fm.alarmroutingservice.impl;

import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.ABORTED;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.EXPORT_ROUTE_FILE_LOCATION;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.FILE_NAME_DELIMITER;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.ROUTE_FILE_COMPRESSION_REQUEST_FILE_EXTENSION;

import java.util.Collection;
import java.util.Date;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.itpf.sdk.resources.Resource;
import com.ericsson.oss.services.fm.alarmroutingservice.api.RouteFileExport;
import com.ericsson.oss.services.fm.alarmroutingservice.models.AlarmRouteFileExportRequest;
import com.ericsson.oss.services.fm.alarmroutingservice.util.JcaFileResourceUtil;

/**
 * A bean for starting route files compression process, creating an empty request file (routeFile_timestamp.REQUEST) on the route files destination
 * directory and demanding process monitoring to an asynchronous bean.
 * <p>
 * fm-alarm-route-file-loader service is responsible for files compression.
 * </p>
 * When compressed file is ready for download the request file is removed and the asynchronous monitoring process signals to UI, via webpush,
 * compressed file availability on the destination file system.
 * <p>
 * It is expected the destination directory is one upper the source directory (i.e. source = /ericsson/enm/dumps, dest = /ericsson/enm/dumps/data) in
 * order to distinguish between zip of route files and zip of rotated route file
 * </p>
 * <p>
 * It creates request file and start asynchronous process.
 * <p>
 */
@Stateless
public class AlarmRouteFileExportExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(AlarmRouteFileExportExecutor.class);

    @Inject
    private RouteFileExport routeFileExport;

    @Inject
    private AsyncFileRouteExportProcessHandler asynProcessHandler;

    @Inject
    private JcaFileResourceUtil jcaFileResourceUtil;

    /**
     * Method for creating the route files compression request file.
     * @param alarmRouteFileExportRequest
     *            container of route file export information {@link AlarmRouteFileExportRequest}.
     */
    public void createRouteExportRequestFile(final AlarmRouteFileExportRequest alarmRouteFileExportRequest) {
        final String requestFile =
                buildRouteFileExportRequest(alarmRouteFileExportRequest.getRouteFileName(), alarmRouteFileExportRequest.getUserName());
        alarmRouteFileExportRequest.setRequestFileName(requestFile);
        LOGGER.debug("Route Export Request File created for : {} ", alarmRouteFileExportRequest);

        final StringBuilder routeFileNameBuilder = new StringBuilder().append(alarmRouteFileExportRequest.getRouteFileName())
                .append(FILE_NAME_DELIMITER).append(alarmRouteFileExportRequest.getUserName());

        if (jcaFileResourceUtil.createRequestFileAndAbortPreviousExports(requestFile, routeFileNameBuilder.toString())) {
            waitforRouteFileExport(alarmRouteFileExportRequest);
        } else {
            // return error via webpush
            LOGGER.warn("Request file : {} was not created in ../export directory. Aborting the export for file : {} ", requestFile,
                    alarmRouteFileExportRequest.getRouteFileName());
            routeFileExport.sendRouteFileExportResponse(alarmRouteFileExportRequest.getRouteFileName(), alarmRouteFileExportRequest.getJobId(),
                    ABORTED);
        }
    }

    /**
     * Responsible for checking the running exports for the current user and route.
     * @param alarmRouteFileExportRequest
     *            - Alarm Route File Export Request
     * @return - Returns true in cases of running exports present for current User and Route.
     */
    public boolean isExportInProgress(final AlarmRouteFileExportRequest alarmRouteFileExportRequest) {
        final StringBuilder requestFileNameBuilder = new StringBuilder().append(alarmRouteFileExportRequest.getRouteFileName())
                .append(FILE_NAME_DELIMITER).append(alarmRouteFileExportRequest.getUserName());
        LOGGER.debug("Checking running exports for {} ", requestFileNameBuilder.toString());
        final Collection<Resource> resources = jcaFileResourceUtil.getFiles(EXPORT_ROUTE_FILE_LOCATION);
        for (final Resource resouce : resources) {
            final String name = resouce.getName();
            if (name.contains(ROUTE_FILE_COMPRESSION_REQUEST_FILE_EXTENSION) ? (name.contains(requestFileNameBuilder.toString()) ? true : false)
                    : false) {
                return true;
            }
        }
        return false;
    }

    private void waitforRouteFileExport(final AlarmRouteFileExportRequest alarmRouteFileExportRequest) {
        if (asynProcessHandler != null) {
            asynProcessHandler.monitorAsyncRouteFileCreation(alarmRouteFileExportRequest);
        }
    }

    /**
     * Method for building the request filename required to start the compression process of route files.
     * @param routeFileName
     *            name of the route file
     * @return the route files compression request filename
     */
    private String buildRouteFileExportRequest(final String routeFileName, final String userName) {
        return new StringBuilder().append(routeFileName).append(FILE_NAME_DELIMITER).append(userName).append(FILE_NAME_DELIMITER)
                .append(new Date().getTime()).append(ROUTE_FILE_COMPRESSION_REQUEST_FILE_EXTENSION).toString();
    }
}
