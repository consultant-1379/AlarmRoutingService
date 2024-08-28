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
import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.COMPLETED;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.EXPORT_ROUTE_FILE_LOCATION;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.FILE_NAME_DELIMITER;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.ROUTE_FILE_COMPRESSION_FAILED_FILE_EXTENSION;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.ROUTE_FILE_COMPRESSION_REQUEST_FILE_EXTENSION;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.ROUTE_FILE_ZIP_FILE_EXTENSION;
import static com.ericsson.oss.services.fm.common.constants.GeneralConstants.SLASH_DELIMITER;
import static com.ericsson.oss.services.fm.common.constants.GeneralConstants.UNDER_SCORE_DELIMITER;

import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.itpf.sdk.core.retry.RetriableCommand;
import com.ericsson.oss.itpf.sdk.core.retry.RetryContext;
import com.ericsson.oss.itpf.sdk.core.retry.RetryManager;
import com.ericsson.oss.itpf.sdk.core.retry.RetryPolicy;
import com.ericsson.oss.services.fm.alarmroutingservice.api.RouteFileExport;
import com.ericsson.oss.services.fm.alarmroutingservice.configuration.ConfigurationChangeListener;
import com.ericsson.oss.services.fm.alarmroutingservice.models.AlarmRouteFileExportRequest;
import com.ericsson.oss.services.fm.alarmroutingservice.util.JcaFileResourceUtil;

/**
 * A bean for monitoring route files compression process ends asynchronously.
 * <p>
 * Process ends when the request files is removed or replaced with a process failed file or a timeout of 10 minutes expires.
 * </p>
 * Response is sent via webpush to UI. It includes a status field (ABORT/SUCCESS) and the zip file name on success
 * <p>
 * It monitors file request presence on file system and if timeout occurs.
 * <p>
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class AsyncFileRouteExportProcessHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(AsyncFileRouteExportProcessHandler.class);

    private static final long DEFAULT_WAIT_TIME = 1000L;
    private static final int MIN_RETRY_ATTEMPTS = 4;
    private static final int RETRY_ATTEMPTS = 30;
    private static final int RETRY_WAIT_INTERVAL = 2;

    @Inject
    private ConfigurationChangeListener configurationChangeListener;

    @Inject
    private RouteFileExport routeFileExport;

    @Resource
    private TimerService timerService;

    @Inject
    private JcaFileResourceUtil fileUtil;

    @Inject
    private RetryManager retryManager;

    public void monitorAsyncRouteFileCreation(final AlarmRouteFileExportRequest alarmRouteFileExportRequest) {
        final String requestFile = alarmRouteFileExportRequest.getRequestFileName();
        LOGGER.debug("Wait for compression end and request file: {} deletion", requestFile);
        startTimer(alarmRouteFileExportRequest);
    }

    private void verifyProcessEnd(final AlarmRouteFileExportRequest alarmRouteFileExportRequest, final Timer timer) {
        final String zipFileName =
                getZipFileName(alarmRouteFileExportRequest.getRequestFileName()).replace(FILE_NAME_DELIMITER, UNDER_SCORE_DELIMITER);
        final String resourceLocation =
                configurationChangeListener.getAlarmRouteFileLocation() + SLASH_DELIMITER + EXPORT_ROUTE_FILE_LOCATION + SLASH_DELIMITER;
        final String requestFailFile = getRequestFailFileName(alarmRouteFileExportRequest.getRequestFileName());
        if (fileUtil.fileExists(resourceLocation + alarmRouteFileExportRequest.getRequestFileName())) {
            LOGGER.debug("Compression job running for request {} ", alarmRouteFileExportRequest.getRequestFileName());
        } else {
            // request file removed, process ended
            alarmRouteFileExportRequest.resetFileCompressionWaitCount();
            stopTimer(timer);
            if (isFileExists(resourceLocation + requestFailFile, MIN_RETRY_ATTEMPTS)) {
                // compression failed , return it via webpush
                LOGGER.debug("Compression failed for {} ,sending failure via webpush", alarmRouteFileExportRequest.getRequestFileName());
                routeFileExport.sendRouteFileExportResponse(alarmRouteFileExportRequest.getRouteFileName(), alarmRouteFileExportRequest.getJobId(),
                        ABORTED);
            } else {
                // if no request and no fail , check zip file existence and return its name via webpush
                if (isFileExists(resourceLocation + zipFileName, RETRY_ATTEMPTS)) {
                    LOGGER.debug("Compression successful.....sending zip filename {}  via webpush", zipFileName);
                    // compression successful , return zip file name via webpush
                    routeFileExport.sendRouteFileExportResponse(zipFileName, alarmRouteFileExportRequest.getJobId(), COMPLETED);
                } else {
                    LOGGER.debug("Compression failed.....zip filename {}  NOT FOUND", zipFileName);
                    routeFileExport.sendRouteFileExportResponse(zipFileName, alarmRouteFileExportRequest.getJobId(), ABORTED);
                }
            }
        }
    }

    private boolean isFileExists(final String absoluteFilePath, final int attempts) {
        boolean result = false;
        try {
            final RetryPolicy policy =
                    RetryPolicy.builder().attempts(attempts).waitInterval(RETRY_WAIT_INTERVAL, TimeUnit.SECONDS).retryOn(Exception.class).build();
            result = retryManager.executeCommand(policy, new RetriableCommand<Boolean>() {
                @Override
                public Boolean execute(final RetryContext retryContext) throws Exception {
                    if (!fileUtil.fileExists(absoluteFilePath)) {
                        LOGGER.debug("--------RETRY----- {}", absoluteFilePath);
                        throw new Exception("File not found");
                    }
                    return true;
                }
            });
        } catch (final Exception exception) {
            LOGGER.error("Exception caught for absolute file path {} while checking the file existence : ", absoluteFilePath, exception);
        }
        return result;
    }

    private void startTimer(final AlarmRouteFileExportRequest alarmRouteFileExportRequest) {
        final TimerConfig timerConfig = new TimerConfig();
        timerConfig.setPersistent(false);
        timerConfig.setInfo(alarmRouteFileExportRequest);
        timerService.createIntervalTimer(DEFAULT_WAIT_TIME, DEFAULT_WAIT_TIME, timerConfig);
        LOGGER.debug("Started timer with timeout value of {} milliseconds", DEFAULT_WAIT_TIME);
    }

    private void stopTimer(final Timer timer) {
        if (timer != null) {
            timer.cancel();
        }
    }

    @Timeout
    private void handleTimeout(final Timer timer) {
        final AlarmRouteFileExportRequest alarmRouteFileExportRequest = (AlarmRouteFileExportRequest) timer.getInfo();
        if (alarmRouteFileExportRequest.getFileCompressionWaitCount() < configurationChangeListener.getCompressionProcessTimeout()) {
            alarmRouteFileExportRequest.incrementFileCompressionWaitCount();
            verifyProcessEnd(alarmRouteFileExportRequest, timer);
        } else {
            alarmRouteFileExportRequest.resetFileCompressionWaitCount();
            // process takes too long, abort timer elapsed
            LOGGER.debug("Compression FAILED.....compression takes too long");
            // remove request file and create fail file, so thread stop execution and send abort
            fileUtil.renameRequestFileToFailedAndRemove(alarmRouteFileExportRequest.getRequestFileName());
            // abort via webpush will be sent in the thread main loop as soon as request file is removed and failed file is created
            timer.cancel();
        }
    }

    private String getRequestFailFileName(final String requestFile) {
        return requestFile.replace(ROUTE_FILE_COMPRESSION_REQUEST_FILE_EXTENSION, ROUTE_FILE_COMPRESSION_FAILED_FILE_EXTENSION);
    }

    private String getZipFileName(final String requestFile) {
        return requestFile.replace(ROUTE_FILE_COMPRESSION_REQUEST_FILE_EXTENSION, ROUTE_FILE_ZIP_FILE_EXTENSION);
    }

}
