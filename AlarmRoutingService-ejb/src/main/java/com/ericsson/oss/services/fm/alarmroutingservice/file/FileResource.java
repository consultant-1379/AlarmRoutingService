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

import java.util.List;
import java.util.Map;

/**
 * An interface for writing the Alarms to File.
 */
public interface FileResource {

    void delegate(final List<Map<String, Object>> alarms, final List<String> alarmAttributesNames, final String fileNameInRoute);
}