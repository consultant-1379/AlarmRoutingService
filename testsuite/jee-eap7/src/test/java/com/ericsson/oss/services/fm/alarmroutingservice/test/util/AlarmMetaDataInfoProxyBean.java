/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2020
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.oss.services.fm.alarmroutingservice.test.util;

import com.ericsson.oss.itpf.sdk.core.annotation.EServiceRef;
import com.ericsson.oss.services.fm.alarmroutingservice.api.AlarmMetaDataInfo;

public class AlarmMetaDataInfoProxyBean implements AlarmMetaDataInfoProxy {

    @EServiceRef
    private AlarmMetaDataInfo alarmMetaDataInfo;

    @Override
    public AlarmMetaDataInfo getAlarmMetaInfo() {
        return alarmMetaDataInfo;
    }

}
