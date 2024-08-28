/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2016
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/

package com.ericsson.oss.services.fm.alarmroutingservice.util;

/**
 * Utility class for constants.
 */
public final class Constants {
    public static final String SYNC_STATE = "syncState";
    public static final String FDN = "fdn";

    public static final String OSS_FM = "FM";
    public static final String OPEN_ALARM = "OpenAlarm";
    public static final String HISTORY_ALARM = "HistoryAlarm";

    public static final String ALARM_NUMBER = "alarmNumber";
    public static final String PRESENT_SEVERITY = "presentSeverity";
    public static final String PROBABLE_CAUSE = "probableCause";
    public static final String SPECIFIC_PROBLEM = "specificProblem";
    public static final String PROBABLE_CAUSES = "probableCauses";
    public static final String SPECIFIC_PROBLEMS = "specificProblems";
    public static final String RECORD_TYPE = "recordType";
    public static final String EVENT_TYPE = "eventType";
    public static final String EVENT_TYPES = "eventTypes";
    public static final String MECONTEXT = "MeContext";
    public static final String OBJECT_OF_REFERENCE = "objectOfReference";
    public static final String OBJECT_OF_REFERENCES = "objectOfReferences";

    public static final String TECHNICIANPRESENT_SP = "FieldTechnicianPresent";
    public static final String ALARMSUPPRESSED_SP = "AlarmSuppressedMode";
    public static final String TAG_SEPARATOR = "#";

    public static final String FIELD_SEPARATOR = ":"; // "&";

    public static final String EVENT_OUTPUT = "eventOutput";
    public static final String EXT_ACKNOWLEDGER = "extacknowledger";
    public static final String EXT_ACKNOWLEDGE_TIME = "extacknowledgetime";
    public static final String EXT_PRA = "extproposedRepairActions";
    public static final String UNDEFINED = "UNDEFINED";
    public static final String NEW = "NEW";
    public static final String CLEAR = "CLEAR";
    public static final String CHANGE = "CHANGE";
    public static final String ACKSTATE_CHANGE = "ACKSTATE_CHANGE";
    public static final String COMMENT = "COMMENT";
    public static final String ALARMROUTINGSERVICE = "AlarmRoutingService";
    public static final String COMPLETED = "COMPLETED";
    public static final String ABORTED = "ABORTED";

    // metadataInformation
    public static final String SP_ALARM_INFORMATION = "SpecificProblemInformation";
    public static final String PC_ALARM_INFORMATION = "ProbableCauseInformation";
    public static final String ET_ALARM_INFORMATION = "EventTypeInformation";
    public static final String NE_TYPE = "neType";
    public static final String MS_TYPE = "msType";
    public static final String VM_TYPE = "vmType";
    public static final String VIM_TYPE = "vimType";
    public static final String VERSION = "1.0.1";
    public static final String ALL = "ALL";
    public static final String NETWORK_ELEMENT = "NetworkElement";
    public static final String VNFM = "VirtualNetworkFunctionManager";
    public static final String VIM = "VirtualInfrastructureManager";
    public static final String MANAGEMENT_SYSTEM = "ManagementSystem";
    public static final String DELIMITER_COMMA = ",";
    public static final String DELIMITER_SC = ";";
    public static final String DELIMITER_EQUAL = "=";
    public static final String DELIMITER_UNDERSCORE = "_";
    public static final String SP_PC_ET_VALUE_DELIMITER = "¡¿§";

    public static final String INTERNAL_ALARM_FDN = "ENM";

    // ATR handlers constants
    public static final String ALARM_ROUTE_ASSOCIATION_NAME = "AlarmRouteAssociation";
    public static final String FM_NAMESPACE = "FM";
    public static final String USERDEFINED = "USERDEFINED";
    public static final String ALARM_ROUTE_POLICY = "AlarmRoutePolicy";
    public static final String EMAIL_DETAILS = "EmailDetails";
    public static final String FILE_DETAILS = "FileDetails";
    public static final String FILE_NAME = "fileName";
    public static final String FILE_HEADERS = "fileHeaders";
    public static final String FILE_TYPE = "fileType";

    public static final String ROUTE_TYPE_VERSION = "1.0.1";
    public static final String EDI_ROUTE_TYPE = "RouteType";
    public static final String DPS_RELATIONSHIP = "dps_relationship";
    public static final String ALARM_ROUTE_ASSOCIATIONS_VERSION = "1.0.1";
    public static final String OSS_EDT = "OSS_EDT";

    public static final int PAUSE_EVENT_VALUE = 42;
    public static final String NORMAL_ALARM_START = "--------------ALARM FROM ";
    public static final String CLEARED_ALARM_START = "--------------ALARM CLEARING FROM ";
    public static final String ALARM_END = " --------------ALARM END-------------- ";
    public static final String MULTIPLE_HIPHEN = "--------------";
    public static final String TEXT_FILE_EXTENSION = ".txt";
    public static final String CSV_FILE_EXTENSION = ".csv";
    public static final String DATE_FORMAT = "dd-MM-yyyy_hh-mm-ss";
    public static final String STAGING_FILE_LOCATION = "/staging";
    public static final String FILE_NAME_DELIMITER = "$!&";
    public static final String ROTATED_LABEL = "_Rotated_";

    public static final int RETRY_ATEMPTS = 3;
    public static final int WAIT_INTERVAL = 2;

    public static final String FILE_ROUTE_ALARMS_BATCH_SIZE = "FILE_ROUTE_ALARMS_BATCH_SIZE";
    public static final String FILE_ROUTE_CACHE_FLUSH_TIMEOUT = "FILE_ROUTE_CACHE_FLUSH_TIMEOUT";
    public static final String MAX_FILE_TYPE_ROUTES_ALLOWED = "MAX_FILE_TYPE_ROUTES_ALLOWED";
    public static final String ALARM_ROUTE_FILE_LOCATION = "ALARM_ROUTE_FILE_LOCATION";
    public static final String RETRY_EXP_BACKOFF_FILE_MOVE = "RETRY_EXPONENTIAL_BACKOFF_FOR_MOVING_FILES";
    public static final String RETRY_INT_FILE_MOVE = "RETRY_INTERVAL_FOR_MOVING_FILES";
    public static final String RETRY_ATTEMPTS_FILE_MOVE = "RETRY_ATTEMPTS_FOR_MOVING_FILES";
    public static final String COMPRESSION_PROCESS_TIMEOUT = "compressionProcessTimeout";
    public static final String FINAL_ALARM_ROUTE_LOCATION = "data";
    public static final String EXPORT_ROUTE_FILE_LOCATION = "export";
    public static final String ROUTE_FILE_COMPRESSION_REQUEST_FILE_EXTENSION = ".REQUEST";
    public static final String ROUTE_FILE_COMPRESSION_FAILED_FILE_EXTENSION = ".FAILED";
    public static final String ROUTE_FILE_ZIP_FILE_EXTENSION = ".zip";
    public static final String DELETED_ALARM_ROUTE_FILE_PURGE_INTERVAL = "deletedAlarmRouteFilePurgeInterval";
    public static final String DELETED_ALARM_ROUTE_FILE_RETENTION_PERIOD = "deletedAlarmRouteFileRetentionPeriod";
    public static final long MILLISEC_IN_ONE_HOUR = 3600000L;

    private Constants() {
    }
}
