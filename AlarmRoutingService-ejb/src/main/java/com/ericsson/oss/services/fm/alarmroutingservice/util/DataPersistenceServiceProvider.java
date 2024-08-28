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

package com.ericsson.oss.services.fm.alarmroutingservice.util;

import javax.enterprise.context.ApplicationScoped;

import com.ericsson.oss.itpf.datalayer.dps.DataBucket;
import com.ericsson.oss.itpf.datalayer.dps.DataPersistenceService;
import com.ericsson.oss.itpf.sdk.core.annotation.EServiceRef;

/**
 * An ApplicationScoped bean, for providing a DPS instance.
 */
@ApplicationScoped
public class DataPersistenceServiceProvider {

    @EServiceRef
    private DataPersistenceService dataPersistenceService;

    public DataPersistenceService getDataPersistenceServiceInstance() {
        return dataPersistenceService;
    }

    public DataBucket getLiveBucket() {
        return dataPersistenceService.getLiveBucket();
    }
}
