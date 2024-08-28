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

import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.MILLISEC_IN_ONE_HOUR;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.ROTATED_LABEL;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.transaction.SystemException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.itpf.sdk.resources.Resource;
import com.ericsson.oss.services.fm.alarmroutingservice.configuration.ConfigurationChangeListener;
import com.ericsson.oss.services.fm.alarmroutingservice.dps.DpsHelper;

/**
 * Class used to fetch the old deleted alarm route files from SFS location.
 */
public class DeletableAlarmRouteFilesProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(DeletableAlarmRouteFilesProvider.class);

    @Inject
    private JcaDirectoryResource directoryResource;

    @Inject
    private ConfigurationChangeListener configurationChangeListener;

    @Inject
    private DpsHelper dpsHelper;

    /**
     * Method to fetch old alarm route files list which does not exists in database.
     * @param resourceList
     *            resources
     * @return List of old resources
     * @throws Exception
     *             IllegalStateException
     * @throws Exception
     *             SecurityException
     * @throws Exception
     *             SystemException
     */
    public ArrayList<Resource> fetchOldFilesToBeDeleted(final ArrayList<Resource> resourceList) throws IllegalStateException, SecurityException,
            SystemException {
        final long currentTimeInMillis = new Date().getTime();
        final Collection<Resource> fileList = directoryResource.getFileList();
        final List<String> fileNamesFromDatabase = dpsHelper.fetchFileNamesFromDatabase();
        for (final Resource resource : fileList) {
            final String fileName = resource.getName();
            if (!fileName.contains(ROTATED_LABEL)) {
                final long lastModificationTime = directoryResource.getLastModification(resource);
                if ((currentTimeInMillis - lastModificationTime) > configurationChangeListener.getDeletedAlarmRouteFileRetentionPeriod()
                        * MILLISEC_IN_ONE_HOUR
                        && !fileNamesFromDatabase.contains(fileName.substring(0, fileName.lastIndexOf('.')))) {
                    for (final Resource fileResource : fileList) {
                        if (fileResource.getName().contains(fileName)) {
                            resourceList.add(fileResource);
                        }
                    }
                }
            }
        }
        LOGGER.debug("Files which needs to be purged are {}", resourceList.size());
        return resourceList;
    }
}
