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

package com.ericsson.oss.services.fm.alarmroutingservice.route.processors;

import static com.ericsson.oss.services.fm.alarmroutemanagement.models.AlarmRoute.SUBJECT;
import static com.ericsson.oss.services.fm.alarmroutemanagement.models.AlarmRoute.TO_ADDRESS;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.EmailConstants.RELAY_HOST;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.EmailConstants.SMTP_HOST;

import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.itpf.sdk.core.retry.RetriableCommand;
import com.ericsson.oss.itpf.sdk.core.retry.RetriableCommandException;
import com.ericsson.oss.itpf.sdk.core.retry.RetryContext;
import com.ericsson.oss.itpf.sdk.core.retry.RetryManager;
import com.ericsson.oss.itpf.sdk.core.retry.RetryPolicy;
import com.ericsson.oss.itpf.sdk.recording.EventLevel;
import com.ericsson.oss.itpf.sdk.recording.SystemRecorder;
import com.ericsson.oss.services.fm.alarmroutemanagement.models.AlarmRoute;
import com.ericsson.oss.services.fm.alarmroutingservice.cache.AlarmRouteAssociationData;
import com.ericsson.oss.services.fm.alarmroutingservice.cache.AlarmRoutesHolder;
import com.ericsson.oss.services.fm.alarmroutingservice.configuration.ConfigurationChangeListener;
import com.ericsson.oss.services.fm.alarmroutingservice.eventhandlers.SignedEmailHandler;
import com.ericsson.oss.services.fm.models.processedevent.ProcessedAlarmEvent;

/**
 * EmailSender sends email message to the external client by taking email route {@link AlarmRoute} and alarm {@link ProcessedAlarmEvent} information
 * from EmailClientHandler.
 */
@ApplicationScoped
public class EmailSender {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailSender.class);

    private static final String EMAILRELAY = System.getProperty(RELAY_HOST, RELAY_HOST);

    @Inject
    private ConfigurationChangeListener configurationsChangeListener;

    @Inject
    private SystemRecorder systemRecorder;

    @Inject
    private RetryManager retryManager;

    @Inject
    private AlarmRoutesHolder alarmRouteHolder;

    @Inject
    private SignedEmailHandler signedEmailHandler;

    private InternetAddress fromAddress;

    private Transport transport;

    /**
     * Method takes email route and email route matched alarm and process the alarm as per the email route policy.
     *
     * @param emailRoute
     *        --{@link AlarmRoute} email route.
     * @param processedAlarmEvent
     *        -- {@link ProcessedAlarmEvent} email route matched alarm.
     */
    public void sendEmail(final AlarmRoute emailRoute, final ProcessedAlarmEvent processedAlarmEvent) {
        final String alarm = processedAlarmEvent.toFormattedString();
        final AlarmRouteAssociationData alarmRouteAssociationData = alarmRouteHolder.getAlarmRouteAssociation(emailRoute.getRouteId());

        LOGGER.debug("alarmRouteAssociationData: {} with poid : {}", alarmRouteAssociationData, emailRoute.getRouteId());
        if (null != alarmRouteAssociationData) {
            final String subject = (String) alarmRouteAssociationData.getAssociationAttributes().get(SUBJECT);
            final List<String> toAddresses = (List<String>) alarmRouteAssociationData.getAssociationAttributes().get(TO_ADDRESS);

            if (null != subject && (null != toAddresses && !toAddresses.isEmpty())) {
                final MimeMessage emailMessage = prepareEmailMessage(toAddresses, subject, alarm);

                if (null != emailMessage) {
                    try {
                        trySendMail(emailMessage);
                    } catch (final RetriableCommandException rce) {
                        systemRecorder.recordEvent("Email sending to External clients is failed, Max retries to send an alarm as email reached ",
                                EventLevel.DETAILED, " and message is :" + emailMessage, " and toAddresses are :", toAddresses.toString());
                    }
                }
            }
        }
    }

    /**
     * Method takes email message and sends message to the mentioned toAddresses in the message via emailRelay of lvsrouter.
     *
     * @param message
     *        {@link MimeMessage} which contains spontaneous alarm information.
     * @throws Exception
     *         Exception while sending email to the destination addresses.
     */
    private void sendEmail(final MimeMessage message) throws Exception {
        if (!transport.isConnected()) {
            transport.connect();
        }
        transport.sendMessage(message, message.getAllRecipients());
    }

    /**
     * Method takes {@link MimeMessage} message and try to send multiple times using retry mechanism {@link RetryManager} as per the retryPolicy
     * {@link RetryPolicy} configured.
     *
     * @param message
     *        {@link MimeMessage}
     */
    @SuppressWarnings("unchecked")
    public void trySendMail(final MimeMessage message) {
        final RetryPolicy policy = RetryPolicy.builder().attempts(configurationsChangeListener.getEmailRetryAttempts())
                .waitInterval(configurationsChangeListener.getEmailRetryInterval(), TimeUnit.MILLISECONDS)
                .exponentialBackoff(configurationsChangeListener.getEmailRetryExponentialBackOff()).retryOn(Exception.class).build();

        retryManager.executeCommand(policy, new RetriableCommand() {
            @Override
            public Object execute(final RetryContext retryContext) throws Exception {
                LOGGER.debug("In the execute method trying to send email to external clients . CurrentAttempt is {}",
                        retryContext.getCurrentAttempt());

                sendEmail(message);
                return null;
            }
        });
    }

    /**
     * Method takes toAddresses list,subject and alarm to be sent to the toAddresses and using JavaMail client alarm sent to external mail clients via
     * emailRelay of lvsrouter. Also inserts the connection information.
     *
     * @param toAddresses
     *        {@link Listlt&;String gt&;} it represents toAddresses available in the email route.
     * @param subject
     *        it represents subject available in the email route.
     * @param alarm
     *        it represents matched alarm with email route.
     */
    public MimeMessage prepareEmailMessage(final List<String> toAddresses, final String subject, final String alarm) {
        final Properties props = System.getProperties();
        props.put(SMTP_HOST, EMAILRELAY);
        MimeMessage message = null;
        try {
            final Session session = Session.getDefaultInstance(props, null);
            if (transport == null) {
                transport = session.getTransport("smtp");
            }
            message = new MimeMessage(session);
            final InternetAddress fromInternetAddress = getFromAddress();
            LOGGER.debug("FromAddress is  {}", fromInternetAddress);

            message.setFrom(fromInternetAddress);

            final InternetAddress[] toInternetAddress = new InternetAddress[toAddresses.size()];
            for (int count = 0; count < toAddresses.size(); count++) {
                toInternetAddress[count] = new InternetAddress(toAddresses.get(count));
            }

            message.setRecipients(Message.RecipientType.TO, toInternetAddress);
            message.setSubject(subject);
            message.setContent(subject, "text/plain");
            message.setSentDate(new Date());
            message.setText(alarm);
            message = signedEmailHandler.getSignedMessage(message);
        } catch (final Exception exception) {
            message = null;
            LOGGER.error("Exception occured while preparing email message : ", exception);
        }
        return message;
    }

    /**
     * Method returns {@link InternetAddress} by taking email fromAddress from configuration properties {@link ConfigurationChangeListener} and
     * prepare internetAddress.
     *
     * @return {@link InternetAddress}
     * @throws AddressException
     *         --invalid email address.
     */
    private InternetAddress getFromAddress() throws AddressException {
        if (configurationsChangeListener.isEmailFromAddressChanged() || null == fromAddress) {
            fromAddress = new InternetAddress(configurationsChangeListener.getEmailFromAddress());
            configurationsChangeListener.setEmailFromAddressChanged(false);
        }
        return fromAddress;
    }
}
