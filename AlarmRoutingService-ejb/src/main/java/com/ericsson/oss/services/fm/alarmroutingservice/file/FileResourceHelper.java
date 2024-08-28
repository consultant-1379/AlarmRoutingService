/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2017
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/

package com.ericsson.oss.services.fm.alarmroutingservice.file;

import com.ericsson.oss.itpf.sdk.resources.Resource;

/**
 * Helper class to do CRUD operations on File.
 */
public class FileResourceHelper {

    public void writeToFile(final Resource resource, final String alarm, final String fileNameInRoute, final String resourceUri) {
        resource.setURI(resourceUri);
        if (resource.supportsWriteOperations()) {
            resource.write(alarm.getBytes(), true);
        }
    }
}
