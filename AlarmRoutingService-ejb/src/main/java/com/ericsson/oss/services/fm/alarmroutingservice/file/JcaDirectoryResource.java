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

package com.ericsson.oss.services.fm.alarmroutingservice.file;

import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.FINAL_ALARM_ROUTE_LOCATION;
import static com.ericsson.oss.services.fm.common.constants.GeneralConstants.SLASH_DELIMITER;

import java.util.Collection;
import java.util.HashSet;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.itpf.sdk.resources.Resource;
import com.ericsson.oss.services.fm.alarmroutingservice.configuration.ConfigurationChangeListener;

/**
 * This Class for CRUD operations on a Resource.
 */
@Stateless
public class JcaDirectoryResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(JcaDirectoryResource.class);

    @Inject
    private ConfigurationChangeListener configurationChangeListener;

    @Inject
    private ResourceRetryManager resourceRetryManager;

    private Resource fileSystemResource;

    /**
     * Method that delete a file that are eligible for deletion.
     * @param String
     *            absolutePath
     */
    public void deleteFile(final String absolutePath) {
        try {
            fileSystemResource = resourceRetryManager.tryToGetResource(absolutePath);
            if (fileSystemResource.exists()) {
                fileSystemResource.setURI(absolutePath);
                final boolean isDeleted = fileSystemResource.delete();
                LOGGER.debug("resource {} deleted = {} ", absolutePath, isDeleted);
            }
        } catch (final Exception exception) {
            LOGGER.error("Exception occured while deleting files : {}", exception.getMessage());
        }
    }

    /**
     * Method that returns the list of files in the SFS AlarmRouteFileLocation area.
     * @return
     *         Collection of Resource
     **/
    public Collection<Resource> getFileList() {
        final Collection<Resource> fileList = new HashSet<>();
        try {
            final String directoryPath = configurationChangeListener.getAlarmRouteFileLocation();
            final String dataDirectoryPath = directoryPath + SLASH_DELIMITER + FINAL_ALARM_ROUTE_LOCATION;
            fileSystemResource = resourceRetryManager.tryToGetResource(dataDirectoryPath);
            fileList.addAll(fileSystemResource.listFiles());
        } catch (final Exception exception) {
            LOGGER.error("Exception occured while reading file Size in Alarm Route File Location area {}", exception.getMessage());
        }
        return fileList;
    }

    /**
     * Method that returns the last modification of the passed resource.
     * @return
     *         Long representing timestamp of last modification
     **/
    public Long getLastModification(final Resource file) {
        long lastFileModificationTs = 0L;
        final String fullFilePath =
                configurationChangeListener.getAlarmRouteFileLocation() + SLASH_DELIMITER + FINAL_ALARM_ROUTE_LOCATION + SLASH_DELIMITER
                        + file.getName();
        try {
            file.setURI(fullFilePath);
            if (file.exists()) {
                lastFileModificationTs = file.getLastModificationTimestamp();
            }
        } catch (final Exception exception) {
            LOGGER.error("Exception occured while reading files in Alarm Route File Location area {}", exception.getMessage());
        }
        return lastFileModificationTs;
    }
}
