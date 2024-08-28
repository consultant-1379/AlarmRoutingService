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

package com.ericsson.oss.services.fm.alarmroutingservice.util;

import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.EXPORT_ROUTE_FILE_LOCATION;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.ROUTE_FILE_COMPRESSION_FAILED_FILE_EXTENSION;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.ROUTE_FILE_COMPRESSION_REQUEST_FILE_EXTENSION;
import static com.ericsson.oss.services.fm.common.constants.GeneralConstants.SLASH_DELIMITER;

import java.util.Collection;
import java.util.HashSet;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.itpf.sdk.resources.Resource;
import com.ericsson.oss.itpf.sdk.resources.Resources;
import com.ericsson.oss.services.fm.alarmroutingservice.configuration.ConfigurationChangeListener;

/**
 * A bean that represents a File resource ({@link Resource}), a Singleton is considered because no two threads will access the methods of this class
 * in parallel.
 * <p>
 * It manages the reading, update and deletion of files in transaction.
 * <p>
 * ApplicationScoped bean is considered , as it is returning a new bean for every injection point if we keep only singleton.To avoid it and to have
 * only one bean per application ,application scoped bean is considered.
 */
@Stateless
public class JcaFileResourceUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(JcaFileResourceUtil.class);

    @Inject
    private ConfigurationChangeListener configurationChangeListener;

    /**
     * Method for checking file existence.
     * @param absoluteFilePath
     *            absolute file path of the file.
     * @return boolean
     **/
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public boolean fileExists(final String absoluteFilePath) {
        final Resource resource = Resources.getFileSystemResource(absoluteFilePath);
        if (resource == null) {
            LOGGER.debug("File {} not found or not accesible ", absoluteFilePath);
            return false;
        } else {
            return resource.exists();
        }
    }

    /**
     * Method for creating the route file compression request files.
     * @param requestFile
     *            route file compression request file name.
     * @return boolean
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public boolean createRequestFile(final String requestFile) {
        boolean operationResult = false;
        final String sourceDir =
                configurationChangeListener.getAlarmRouteFileLocation() + SLASH_DELIMITER + EXPORT_ROUTE_FILE_LOCATION + SLASH_DELIMITER;
        operationResult = createResource(sourceDir + requestFile);
        return operationResult;
    }

    /**
     * Method for removing route file compression request files and post route file request failed files in case of failure.
     * @param requestFile
     *            route file compression request file name.
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void renameRequestFileToFailedAndRemove(final String requestFile) {
        boolean operationResult = false;
        final String sourceDir =
                configurationChangeListener.getAlarmRouteFileLocation() + SLASH_DELIMITER + EXPORT_ROUTE_FILE_LOCATION + SLASH_DELIMITER;
        final String responseFileName =
                requestFile.replace(ROUTE_FILE_COMPRESSION_REQUEST_FILE_EXTENSION, ROUTE_FILE_COMPRESSION_FAILED_FILE_EXTENSION);
        final Resource resource = Resources.getFileSystemResource(sourceDir + responseFileName);

        operationResult = createResource(resource);
        resource.setURI(sourceDir + requestFile);
        if (operationResult) {
            operationResult = deleteResource(resource);
        }
        LOGGER.debug("file renamed from : {}  to : {} ", requestFile, responseFileName);
    }

    /**
     * Method that returns the list of files in the SFS AlarmRouteFileLocation area.
     * @return Collection of Resources.
     **/
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Collection<Resource> getFiles(final String finalFilesLocation) {
        final Collection<Resource> fileList = new HashSet<>();
        try {
            String dataDirectoryPath = configurationChangeListener.getAlarmRouteFileLocation();
            if (finalFilesLocation != null && !finalFilesLocation.isEmpty()) {
                dataDirectoryPath = dataDirectoryPath + SLASH_DELIMITER + finalFilesLocation;
                LOGGER.debug(" get files list from {} ", dataDirectoryPath);
            }
            final Resource fileSystemResource = Resources.getFileSystemResource(dataDirectoryPath);
            fileList.addAll(fileSystemResource.listFiles());
        } catch (final Exception exception) {
            LOGGER.error("Exception occured while listing files in {}: directory is: ", finalFilesLocation, exception);
        }
        return fileList;
    }

    /**
     * Responsible for creating Filed files for running exports and creating Requestfor current request.
     * @param requestFile
     *            route file compression request file name.
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public boolean createRequestFileAndAbortPreviousExports(final String requestFile, final String fileNamAndUserName) {
        boolean operationResult = false;
        final String sourceDir =
                configurationChangeListener.getAlarmRouteFileLocation() + SLASH_DELIMITER + EXPORT_ROUTE_FILE_LOCATION + SLASH_DELIMITER;
        final Resource fileSystemResource = Resources.getFileSystemResource(sourceDir);

        for (final Resource resource : fileSystemResource.listFiles()) {
            final String runningExportFileName = resource.getName();
            final String responseFileName =
                    runningExportFileName.replace(ROUTE_FILE_COMPRESSION_REQUEST_FILE_EXTENSION, ROUTE_FILE_COMPRESSION_FAILED_FILE_EXTENSION);
            LOGGER.debug("checking for file with name {}", runningExportFileName);
            if (runningExportFileName.contains(ROUTE_FILE_COMPRESSION_REQUEST_FILE_EXTENSION)
                    ? (runningExportFileName.contains(fileNamAndUserName) ? true : false) : false) {
                fileSystemResource.setURI(sourceDir + responseFileName);
                operationResult = createResource(fileSystemResource);
                fileSystemResource.setURI(sourceDir + runningExportFileName);
                if (operationResult) {
                    operationResult = deleteResource(fileSystemResource);
                }
                LOGGER.debug("Filed file {} created and request file deleted for route {} ", responseFileName, runningExportFileName,
                        fileNamAndUserName);
            }
        }
        fileSystemResource.setURI(sourceDir + requestFile);
        operationResult = createResource(fileSystemResource);
        return operationResult;
    }

    private boolean createResource(final String absoluteFilePath) {
        boolean isFileCreated = false;
        final Resource resource = Resources.getFileSystemResource(absoluteFilePath);
        resource.setURI(absoluteFilePath);
        if (resource.supportsWriteOperations()) {
            resource.write(null, true);
            isFileCreated = resource.exists();
        }
        LOGGER.debug("file {} created {}", absoluteFilePath, isFileCreated);
        return isFileCreated;
    }

    private boolean createResource(final Resource resource) {
        boolean isFileCreated = false;
        if (resource.supportsWriteOperations()) {
            resource.write(null, true);
            isFileCreated = resource.exists();
        }
        LOGGER.debug("file {} created {}", resource.getName(), isFileCreated);
        return isFileCreated;
    }

    private boolean deleteResource(final Resource resource) {

        if (resource != null && resource.exists()) {
            if (resource.supportsWriteOperations()) {
                final boolean isDeleted = resource.delete();
                LOGGER.debug("file {} deleted = {} ", resource.getName(), isDeleted);
            } else {
                LOGGER.debug("file {} not exists", resource.getName());
            }
        } else {
            LOGGER.debug("File {} not found or not accesible ", resource);
            return false;
        }
        return resource.exists();
    }

}
