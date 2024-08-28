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

package com.ericsson.oss.services.fm.alarmroutingservice.models;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Container class for storing route file compression data. Serializable for passing data on timer expiration.
 */
public class AlarmRouteFileExportRequest implements Serializable {

    private static final long serialVersionUID = 2942730588042235274L;
    private static final Logger LOGGER = LoggerFactory.getLogger(AlarmRouteFileExportRequest.class);
    private String routeFileName;
    private String routeFileExtension;
    private String jobId;
    private String requestFileName;
    private String userName;
    /**
     * fileCompressionWaitCount is used to count the number of times timer for processing export request has been elapsed.
     */
    private int fileCompressionWaitCount;

    public AlarmRouteFileExportRequest(final String routeFileName, final String routeFileExtension, final String jobId, final String userName) {
        super();
        LOGGER.debug("Received route file name is :{} , ext: {} , jobId: {} for the user {}", routeFileName, routeFileExtension, jobId, userName);
        this.routeFileName = routeFileName;
        this.routeFileExtension = routeFileExtension;
        this.jobId = jobId;
        this.userName = userName;
    }

    public AlarmRouteFileExportRequest() {
    }

    public String getRequestFileName() {
        return requestFileName;
    }

    public void setRequestFileName(final String requestFileName) {
        this.requestFileName = requestFileName;
    }

    public String getRouteFileExtension() {
        return routeFileExtension;
    }

    public void setRouteFileExtension(final String routeFileExtension) {
        this.routeFileExtension = routeFileExtension;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(final String jobId) {
        this.jobId = jobId;
    }

    public String getRouteFileName() {
        return routeFileName;
    }

    public void setRouteFileName(final String routeFileName) {
        this.routeFileName = routeFileName;
    }

    public int getFileCompressionWaitCount() {
        return fileCompressionWaitCount;
    }

    public void incrementFileCompressionWaitCount() {
        fileCompressionWaitCount++;
    }

    public void resetFileCompressionWaitCount() {
        fileCompressionWaitCount = 0;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(final String userName) {
        this.userName = userName;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("AlarmRouteFileExportRequest [routeFileName=").append(routeFileName).append(", routeFileExtension=").append(routeFileExtension)
                .append(", jobId=").append(jobId).append(", requestFileName=").append(requestFileName).append(", userName=").append(userName)
                .append(", fileCompressionWaitCount=").append(fileCompressionWaitCount).append("]");
        return builder.toString();
    }

}
