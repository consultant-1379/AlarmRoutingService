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

import static com.ericsson.oss.services.fm.common.constants.AdditionalAttrConstants.EVENT_PO_ID;
import static com.ericsson.oss.services.fm.common.constants.AlarmAttrConstants.ACK_OPERATOR;
import static com.ericsson.oss.services.fm.common.constants.AlarmAttrConstants.ACK_TIME;
import static com.ericsson.oss.services.fm.common.constants.AlarmAttrConstants.ADDITIONAL_INFORMATION;
import static com.ericsson.oss.services.fm.common.constants.AlarmAttrConstants.ADDITIONAL_INFORMATION_MAP;
import static com.ericsson.oss.services.fm.common.constants.AlarmAttrConstants.ALARMING_OBJECT;
import static com.ericsson.oss.services.fm.common.constants.AlarmAttrConstants.ALARM_ID;
import static com.ericsson.oss.services.fm.common.constants.AlarmAttrConstants.ALARM_NUMBER;
import static com.ericsson.oss.services.fm.common.constants.AlarmAttrConstants.ALARM_STATE;
import static com.ericsson.oss.services.fm.common.constants.AlarmAttrConstants.BACKUP_OBJECT_INSTANCE;
import static com.ericsson.oss.services.fm.common.constants.AlarmAttrConstants.BACKUP_STATUS;
import static com.ericsson.oss.services.fm.common.constants.AlarmAttrConstants.CEASE_OPERATOR;
import static com.ericsson.oss.services.fm.common.constants.AlarmAttrConstants.CEASE_TIME;
import static com.ericsson.oss.services.fm.common.constants.AlarmAttrConstants.COMMENT_TEXT;
import static com.ericsson.oss.services.fm.common.constants.AlarmAttrConstants.CORRELATEDVISIBILITY;
import static com.ericsson.oss.services.fm.common.constants.AlarmAttrConstants.CORRELATED_EVENT_PO_ID;
import static com.ericsson.oss.services.fm.common.constants.AlarmAttrConstants.EVENT_TIME;
import static com.ericsson.oss.services.fm.common.constants.AlarmAttrConstants.EVENT_TYPE;
import static com.ericsson.oss.services.fm.common.constants.AlarmAttrConstants.FDN;
import static com.ericsson.oss.services.fm.common.constants.AlarmAttrConstants.FMX_GENERATED;
import static com.ericsson.oss.services.fm.common.constants.AlarmAttrConstants.INSERT_TIME;
import static com.ericsson.oss.services.fm.common.constants.AlarmAttrConstants.LAST_ALARM_OPERATION;
import static com.ericsson.oss.services.fm.common.constants.AlarmAttrConstants.LAST_UPDATED;
import static com.ericsson.oss.services.fm.common.constants.AlarmAttrConstants.MANUALCEASE;
import static com.ericsson.oss.services.fm.common.constants.AlarmAttrConstants.OBJECT_OF_REFERENCE;
import static com.ericsson.oss.services.fm.common.constants.AlarmAttrConstants.OSCILLATION_COUNT;
import static com.ericsson.oss.services.fm.common.constants.AlarmAttrConstants.PRESENT_SEVERITY;
import static com.ericsson.oss.services.fm.common.constants.AlarmAttrConstants.PREVIOUS_SEVERITY;
import static com.ericsson.oss.services.fm.common.constants.AlarmAttrConstants.PROBABLE_CAUSE;
import static com.ericsson.oss.services.fm.common.constants.AlarmAttrConstants.PROBLEM_DETAIL;
import static com.ericsson.oss.services.fm.common.constants.AlarmAttrConstants.PROBLEM_TEXT;
import static com.ericsson.oss.services.fm.common.constants.AlarmAttrConstants.PROCESSING_TYPE;
import static com.ericsson.oss.services.fm.common.constants.AlarmAttrConstants.PROPOSED_REPAIR_ACTION;
import static com.ericsson.oss.services.fm.common.constants.AlarmAttrConstants.RECORD_TYPE;
import static com.ericsson.oss.services.fm.common.constants.AlarmAttrConstants.REPEAT_COUNT;
import static com.ericsson.oss.services.fm.common.constants.AlarmAttrConstants.SPECIFIC_PROBLEM;
import static com.ericsson.oss.services.fm.common.constants.AlarmAttrConstants.TREND_INDICATION;
import static com.ericsson.oss.services.fm.common.constants.AlarmAttrConstants.VISIBILITY;
import static com.ericsson.oss.services.fm.common.constants.FmxConstants.FMX_TOKEN;

import java.util.HashMap;
import java.util.Map;

import com.ericsson.oss.services.fm.models.processedevent.FMProcessedEventType;
import com.ericsson.oss.services.fm.models.processedevent.ProcessedAlarmEvent;
import com.ericsson.oss.services.fm.models.processedevent.ProcessedEventSeverity;
import com.ericsson.oss.services.fm.models.processedevent.ProcessedEventState;
import com.ericsson.oss.services.fm.models.processedevent.ProcessedEventTrendIndication;
import com.ericsson.oss.services.fm.models.processedevent.ProcessedLastAlarmOperation;

/**
 * Reads from ProcessedAlarmEvent Object and creates map.
 */
public class ProcessedAlarmToMapConverter {

    public Map<String, Object> prepareAttributeMap(final ProcessedAlarmEvent processedAlarmEvent) {
        final Map<String, Object> alarmAttributes = new HashMap<String, Object>();
        alarmAttributes.put(OBJECT_OF_REFERENCE, processedAlarmEvent.getObjectOfReference());
        alarmAttributes.put(EVENT_PO_ID, processedAlarmEvent.getEventPOId());
        alarmAttributes.put(REPEAT_COUNT, processedAlarmEvent.getRepeatCount());
        alarmAttributes.put(OSCILLATION_COUNT, processedAlarmEvent.getOscillationCount());
        alarmAttributes.put(LAST_UPDATED, processedAlarmEvent.getLastUpdatedTime());
        alarmAttributes.put(EVENT_TIME, processedAlarmEvent.getEventTime());
        alarmAttributes.put(INSERT_TIME, processedAlarmEvent.getInsertTime());
        alarmAttributes.put(PROBABLE_CAUSE, processedAlarmEvent.getProbableCause());
        alarmAttributes.put(SPECIFIC_PROBLEM, processedAlarmEvent.getSpecificProblem());
        alarmAttributes.put(ALARM_NUMBER, processedAlarmEvent.getAlarmNumber());
        alarmAttributes.put(EVENT_TYPE, processedAlarmEvent.getEventType());
        alarmAttributes.put(BACKUP_OBJECT_INSTANCE, processedAlarmEvent.getBackupObjectInstance());
        alarmAttributes.put(BACKUP_STATUS, processedAlarmEvent.getBackupStatus());
        alarmAttributes.put(VISIBILITY, processedAlarmEvent.getVisibility());
        alarmAttributes.put(PROPOSED_REPAIR_ACTION, processedAlarmEvent.getProposedRepairAction());
        alarmAttributes.put(ALARM_ID, processedAlarmEvent.getAlarmId());
        alarmAttributes.put(CEASE_TIME, processedAlarmEvent.getCeaseTime());
        alarmAttributes.put(CEASE_OPERATOR, processedAlarmEvent.getCeaseOperator());
        alarmAttributes.put(ACK_TIME, processedAlarmEvent.getAckTime());
        alarmAttributes.put(ACK_OPERATOR, processedAlarmEvent.getAckOperator());
        alarmAttributes.put(ADDITIONAL_INFORMATION, processedAlarmEvent.getAdditionalInformationString());
        alarmAttributes.put(ADDITIONAL_INFORMATION_MAP, processedAlarmEvent.getAdditionalInformation());
        alarmAttributes.put(PROBLEM_DETAIL, processedAlarmEvent.getProblemDetail());
        alarmAttributes.put(PROBLEM_TEXT, processedAlarmEvent.getProblemText());
        alarmAttributes.put(COMMENT_TEXT, processedAlarmEvent.getCommentText());
        alarmAttributes.put(ALARMING_OBJECT, processedAlarmEvent.getAlarmingObject());
        alarmAttributes.put(FDN, processedAlarmEvent.getFdn());
        alarmAttributes.put("timeZone", processedAlarmEvent.getTimeZone());
        alarmAttributes.put(PROCESSING_TYPE, processedAlarmEvent.getProcessingType());
        alarmAttributes.put(FMX_GENERATED, processedAlarmEvent.getFmxGenerated());
        alarmAttributes.put(FMX_TOKEN, processedAlarmEvent.getAdditionalInformation().get(FMX_TOKEN));
        alarmAttributes.put(CORRELATED_EVENT_PO_ID, processedAlarmEvent.getCorrelatedPOId());
        alarmAttributes.put(CORRELATEDVISIBILITY, processedAlarmEvent.getCorrelatedVisibility());
        alarmAttributes.put(MANUALCEASE, processedAlarmEvent.getManualCease());

        if (processedAlarmEvent.getPresentSeverity() != null) {
            final ProcessedEventSeverity eventSeverity = processedAlarmEvent.getPresentSeverity();
            alarmAttributes.put(PRESENT_SEVERITY, eventSeverity.toString());
        }

        if (processedAlarmEvent.getPreviousSeverity() != null) {
            final ProcessedEventSeverity eventSeverity = processedAlarmEvent.getPreviousSeverity();
            alarmAttributes.put(PREVIOUS_SEVERITY, eventSeverity.toString());
        }

        if (processedAlarmEvent.getRecordType() != null) {
            final FMProcessedEventType alarmRecordType = processedAlarmEvent.getRecordType();
            alarmAttributes.put(RECORD_TYPE, alarmRecordType.toString());
        }

        if (processedAlarmEvent.getAlarmState() != null) {
            final ProcessedEventState alarmState = processedAlarmEvent.getAlarmState();
            alarmAttributes.put(ALARM_STATE, alarmState.toString());
        }
        if (processedAlarmEvent.getLastAlarmOperation() != null) {
            final ProcessedLastAlarmOperation lastAlarmOperation = processedAlarmEvent.getLastAlarmOperation();
            alarmAttributes.put(LAST_ALARM_OPERATION, lastAlarmOperation.toString());
        }
        if (processedAlarmEvent.getTrendIndication() != null) {
            final ProcessedEventTrendIndication trendIndication = processedAlarmEvent.getTrendIndication();
            alarmAttributes.put(TREND_INDICATION, trendIndication.toString());
        }
        return alarmAttributes;
    }
}
