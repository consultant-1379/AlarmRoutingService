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

package com.ericsson.oss.services.fm.alarmroutingservice.configuration;

import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.ALARM_ROUTE_FILE_LOCATION;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.COMPRESSION_PROCESS_TIMEOUT;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.DELETED_ALARM_ROUTE_FILE_PURGE_INTERVAL;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.DELETED_ALARM_ROUTE_FILE_RETENTION_PERIOD;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.FILE_ROUTE_ALARMS_BATCH_SIZE;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.FILE_ROUTE_CACHE_FLUSH_TIMEOUT;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.MAX_FILE_TYPE_ROUTES_ALLOWED;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.RETRY_ATTEMPTS_FILE_MOVE;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.RETRY_EXP_BACKOFF_FILE_MOVE;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.RETRY_INT_FILE_MOVE;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.EmailConstants.EMAIL_FROM_ADDRESS;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.EmailConstants.EMAIL_RETRY_ATTEMPTS;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.EmailConstants.EMAIL_RETRY_EXPONENTIAL_BACK_OFF;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.EmailConstants.EMAIL_RETRY_INTERVAL;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.EmailConstants.ENABLE_OUTBOUND_EMAILS;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.itpf.sdk.config.annotation.ConfigurationChangeNotification;
import com.ericsson.oss.itpf.sdk.config.annotation.Configured;
import com.ericsson.oss.services.fm.alarmroutingservice.file.cache.timer.AlarmFileRouteCacheReadTimer;
import com.ericsson.oss.services.fm.alarmroutingservice.startup.DeletedAlarmRouteFilePurgeTimer;

/**
 * This class listens to the changes of all the configuration parameters related to the email service and Triggers respective actions.
 **/
@ApplicationScoped
public class ConfigurationChangeListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationChangeListener.class);
    private boolean isEmailFromAddressChanged;

    @Inject
    private AlarmFileRouteCacheReadTimer alarmFileRouteCacheReadTimer;

    @Inject
    private DeletedAlarmRouteFilePurgeTimer deletedAlarmRouteFilePurgeTimer;

    @Inject
    @Configured(propertyName = ENABLE_OUTBOUND_EMAILS)
    // Default value is false.
    private boolean enableOutBoundEmails;

    @Inject
    @Configured(propertyName = EMAIL_RETRY_ATTEMPTS)
    // Default value is 3.
    private int emailRetryAttempts;

    @Inject
    @Configured(propertyName = EMAIL_RETRY_INTERVAL)
    // Default value is 500 mill seconds
    private int emailRetryInterval;

    @Inject
    @Configured(propertyName = EMAIL_RETRY_EXPONENTIAL_BACK_OFF)
    // Default value is 1.0 sec
    private double emailRetryExponentialBackOff;

    @Inject
    @Configured(propertyName = EMAIL_FROM_ADDRESS)
    // Default value is enmfaultmanagement@ericsson.com
    private String emailFromAddress;

    @Inject
    @Configured(propertyName = FILE_ROUTE_ALARMS_BATCH_SIZE)
    // Default value 15
    private Integer fileRouteAlarmsBatchSize;

    @Inject
    @Configured(propertyName = FILE_ROUTE_CACHE_FLUSH_TIMEOUT)
    // Default value 200 milliseconds
    private Integer fileRouteCacheFlushTimeout;

    @Inject
    @Configured(propertyName = MAX_FILE_TYPE_ROUTES_ALLOWED)
    // Default value 20
    private Integer maxFileTypeRoutesAllowed;

    @Inject
    @Configured(propertyName = ALARM_ROUTE_FILE_LOCATION)
    private String alarmRouteFileLocation;

    // Default value is 3.
    @Inject
    @Configured(propertyName = RETRY_ATTEMPTS_FILE_MOVE)
    private Integer retryAttempts;

    // Default value is 3 secs.
    @Inject
    @Configured(propertyName = RETRY_INT_FILE_MOVE)
    private Integer retryInterval;

    // Default value is 1.5.
    @Inject
    @Configured(propertyName = RETRY_EXP_BACKOFF_FILE_MOVE)
    private Double retryExponentialBackoff;

    //Default value is 600.
    @Inject
    @Configured(propertyName = COMPRESSION_PROCESS_TIMEOUT)
    private Integer compressionProcessTimeout;

    // Default value is 4 hour.
    @Inject
    @Configured(propertyName = DELETED_ALARM_ROUTE_FILE_PURGE_INTERVAL)
    private Integer deletedAlarmRouteFilePurgeInterval;

    // Default value is 72 hours.
    @Inject
    @Configured(propertyName = DELETED_ALARM_ROUTE_FILE_RETENTION_PERIOD)
    private Integer deletedAlarmRouteFileRetentionPeriod;

    public void observeForEnableOutBoundEmails(
            @Observes @ConfigurationChangeNotification(propertyName = ENABLE_OUTBOUND_EMAILS) final Boolean changedValue) {
        LOGGER.info(" ENABLE_OUTBOUND_EMAILS state changed to  {} ", changedValue);
        setEnableOutBoundEmails(changedValue);
    }

    public void observeForEmailRetryAttempts(
            @Observes @ConfigurationChangeNotification(propertyName = EMAIL_RETRY_ATTEMPTS) final Integer changedValue) {
        LOGGER.info(" EMAIL_RETRY_ATTEMPTS state changed to  {} ", changedValue);
        setEmailRetryAttempts(changedValue);
    }

    public void observeForEmailRetryWaitingInterval(
            @Observes @ConfigurationChangeNotification(propertyName = EMAIL_RETRY_INTERVAL) final Integer changedValue) {
        LOGGER.info(" EMAIL_RETRY_INTERVAL state changed to  {} ", changedValue);
        setEmailRetryInterval(changedValue);
    }

    public void observeForEmailRetryExponentialBackOff(
            @Observes @ConfigurationChangeNotification(propertyName = EMAIL_RETRY_EXPONENTIAL_BACK_OFF) final Double changedValue) {
        LOGGER.info(" EMAIL_RETRY_EXPONENTIAL_BACK_OFF state changed to  {} ", changedValue);
        setEmailRetryExponentialBackOff(changedValue);
    }

    public void observeForEmailFromAddress(@Observes @ConfigurationChangeNotification(propertyName = EMAIL_FROM_ADDRESS) final String changedValue) {
        LOGGER.info(" EMAIL_FROM_ADDRESS state changed to  {} ", changedValue);
        setEmailFromAddress(changedValue);
        setEmailFromAddressChanged(true);
    }

    public void observeForFileRouteAlarmsBatchSize(
            @Observes @ConfigurationChangeNotification(propertyName = FILE_ROUTE_ALARMS_BATCH_SIZE) final Integer changedValue) {
        LOGGER.info(" FILE_ROUTE_ALARMS_BATCH_SIZE  changed to  {} ", changedValue);
        fileRouteAlarmsBatchSize = changedValue;
    }

    public void observeForFileRouteCacheFlushTimeout(
            @Observes @ConfigurationChangeNotification(propertyName = FILE_ROUTE_CACHE_FLUSH_TIMEOUT) final Integer changedValue) {
        LOGGER.info(" FILE_ROUTE_CACHE_FLUSH_TIMEOUT  changed to  {} ", changedValue);
        if (fileRouteCacheFlushTimeout != changedValue) {
            fileRouteCacheFlushTimeout = changedValue;
            alarmFileRouteCacheReadTimer.recreateTimerWithNewInterval(fileRouteCacheFlushTimeout);
        }
    }

    public void observeForMaxFileTypeRoutesAllowed(
            @Observes @ConfigurationChangeNotification(propertyName = MAX_FILE_TYPE_ROUTES_ALLOWED) final Integer changedValue) {
        LOGGER.info(" MAX_FILE_TYPE_ROUTES_ALLOWED  changed to  {} ", changedValue);
        maxFileTypeRoutesAllowed = changedValue;
    }

    public void listenForAlRouteFileLocation(
            @Observes @ConfigurationChangeNotification(propertyName = ALARM_ROUTE_FILE_LOCATION) final String changedValue) {
        LOGGER.info(" ALARM_ROUTE_FILE_LOCATION  changed to  {} ", changedValue);
        alarmRouteFileLocation = changedValue;
    }

    public void listenRetryAttempts(@Observes @ConfigurationChangeNotification(propertyName = RETRY_ATTEMPTS_FILE_MOVE) final Integer changedValue) {
        LOGGER.info("retryAttempts value is changed to  {} :", changedValue);
        this.retryAttempts = changedValue;
    }

    public void listenRetryInterval(@Observes @ConfigurationChangeNotification(propertyName = RETRY_INT_FILE_MOVE) final Integer changedValue) {
        LOGGER.info("retryInterval value is changed to : {} ", changedValue);
        this.retryInterval = changedValue;
    }

    public void listenRetryExpBackoff(@Observes @ConfigurationChangeNotification(propertyName = RETRY_EXP_BACKOFF_FILE_MOVE)
                                      final Double changedValue) {
        LOGGER.info("exponentialBackoff value is changed to : {} ", changedValue);
        this.retryExponentialBackoff = changedValue;
    }

    public void listenForCompressionProcessTimeout(
            @Observes @ConfigurationChangeNotification(propertyName = COMPRESSION_PROCESS_TIMEOUT) final Integer changedValue) {
        LOGGER.info("compressionProcessTimeout value is changed to : {} ", changedValue);
        this.compressionProcessTimeout = changedValue;
    }

    public void listenForDeletedAlarmRouteFilePurgeInterval(@Observes @ConfigurationChangeNotification(
            propertyName = DELETED_ALARM_ROUTE_FILE_PURGE_INTERVAL) final Integer changedValue) {
        LOGGER.info("deletedAlarmRouteFilePurgeInterval value is changed to : {} ", changedValue);
        if (deletedAlarmRouteFilePurgeInterval != changedValue) {
            deletedAlarmRouteFilePurgeInterval = changedValue;
            deletedAlarmRouteFilePurgeTimer.recreateTimerWithNewInterval(deletedAlarmRouteFilePurgeInterval);
        }
    }

    public void listenForDeletedAlarmRouteFileRetentionPeriod(@Observes @ConfigurationChangeNotification(
            propertyName = DELETED_ALARM_ROUTE_FILE_RETENTION_PERIOD) final Integer changedValue) {
        LOGGER.info("deletedAlarmRouteFileRetentionPeriod value is changed to : {} ", changedValue);
        this.deletedAlarmRouteFileRetentionPeriod = changedValue;
    }

    public boolean isEnableOutBoundEmails() {
        return enableOutBoundEmails;
    }

    public void setEnableOutBoundEmails(final boolean enableOutBoundEmails) {
        this.enableOutBoundEmails = enableOutBoundEmails;
    }

    public int getEmailRetryAttempts() {
        return emailRetryAttempts;
    }

    public void setEmailRetryAttempts(final int emailRetryAttempts) {
        this.emailRetryAttempts = emailRetryAttempts;
    }

    public int getEmailRetryInterval() {
        return emailRetryInterval;
    }

    public void setEmailRetryInterval(final int emailRetryInterval) {
        this.emailRetryInterval = emailRetryInterval;
    }

    public double getEmailRetryExponentialBackOff() {
        return emailRetryExponentialBackOff;
    }

    public void setEmailRetryExponentialBackOff(final double emailRetryExponentialBackOff) {
        this.emailRetryExponentialBackOff = emailRetryExponentialBackOff;
    }

    public String getEmailFromAddress() {
        return emailFromAddress;
    }

    public void setEmailFromAddress(final String emailFromAddress) {
        this.emailFromAddress = emailFromAddress;
    }

    public boolean isEmailFromAddressChanged() {
        return isEmailFromAddressChanged;
    }

    public void setEmailFromAddressChanged(final boolean isEmailFromAddressChanged) {
        this.isEmailFromAddressChanged = isEmailFromAddressChanged;
    }

    public void setDeletedAlarmRouteFilePurgeInterval(final Integer deletedAlarmRouteFilePurgeInterval) {
        this.deletedAlarmRouteFilePurgeInterval = deletedAlarmRouteFilePurgeInterval;
    }

    public void setDeletedAlarmRouteFileRetentionPeriod(final Integer deletedAlarmRouteFileRetentionPeriod) {
        this.deletedAlarmRouteFileRetentionPeriod = deletedAlarmRouteFileRetentionPeriod;
    }

    public Integer getFileRouteAlarmsBatchSize() {
        return fileRouteAlarmsBatchSize;
    }

    public Integer getFileRouteCacheFlushTimeout() {
        return fileRouteCacheFlushTimeout;
    }

    public Integer getMaxFileTypeRoutesAllowed() {
        return maxFileTypeRoutesAllowed;
    }

    public String getAlarmRouteFileLocation() {
        return alarmRouteFileLocation;
    }

    public Integer getRetryInterval() {
        return retryInterval;
    }

    public Integer getRetryAttempts() {
        return retryAttempts;
    }

    public Double getRetryExponentialBackoff() {
        return retryExponentialBackoff;
    }

    public Integer getCompressionProcessTimeout() {
        return compressionProcessTimeout;
    }

    public Integer getDeletedAlarmRouteFilePurgeInterval() {
        return deletedAlarmRouteFilePurgeInterval;
    }

    public Integer getDeletedAlarmRouteFileRetentionPeriod() {
        return deletedAlarmRouteFileRetentionPeriod;
    }
}
