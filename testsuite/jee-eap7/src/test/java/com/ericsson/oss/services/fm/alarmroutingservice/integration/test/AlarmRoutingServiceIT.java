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
package com.ericsson.oss.services.fm.alarmroutingservice.integration.test;

import static com.ericsson.oss.services.fm.alarmroutingservice.test.util.TestConstants.NETWORK_ELEMENT_PREFIX;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.ContainerController;
import org.jboss.arquillian.container.test.api.Deployer;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.itpf.sdk.eventbus.model.EventSender;
import com.ericsson.oss.itpf.sdk.eventbus.model.annotation.Modeled;
import com.ericsson.oss.services.fm.alarmroutingservice.test.base.AlarmRoutingServiceTestBase;
import com.ericsson.oss.services.fm.alarmroutingservice.test.base.DummyDataCreator;
import com.ericsson.oss.services.fm.alarmroutingservice.test.util.Artifact;
import com.ericsson.oss.services.fm.models.processedevent.ATRInputEvent;
import com.ericsson.oss.services.fm.models.processedevent.FMProcessedEventType;
import com.ericsson.oss.services.fm.models.processedevent.ProcessedEventSeverity;
import com.ericsson.oss.services.fm.models.processedevent.ProcessedEventState;

@RunWith(Arquillian.class)
public class AlarmRoutingServiceIT {

    private static final Logger logger = LoggerFactory.getLogger(AlarmRoutingServiceIT.class);
    private static final String ALARM_ROUTING_SERVICE_TEST_WAR = "alarmrouting_test";
    private static final String ALARM_ACTION_SERVICE_EAR = "AlarmActionService";
    private static final String NE_NAME = "ARBSARQTEST01";

    @ArquillianResource
    private ContainerController controller;

    @Inject
    private DummyDataCreator dummyDataCreator;

    @ArquillianResource
    private Deployer deployer;

    @Inject
    @Modeled
    private EventSender<ATRInputEvent> sender;

    @Deployment(name = ALARM_ACTION_SERVICE_EAR, testable = false, managed = true, order = 1)
    public static Archive<?> createAlarmActionServiceDeployment() {
        logger.info("******Creating AlarmActionService deployment and deploying it to server******");
        return AlarmRoutingServiceTestBase.createEnterpriseArchiveDeployment(Artifact.ALARM_ACTION_SERVICE_EAR);
    }

    @Deployment(name = ALARM_ROUTING_SERVICE_TEST_WAR, testable = true, managed = true, order = 2)
    public static WebArchive createAlarmRoutingServiceTestDeployment() {
        logger.info("******Creating AlarmRoutingServiceTest deployment and deploying it to server******");
        return AlarmRoutingServiceTestBase.createTestArchive();
    }

    @Test
    @OperateOnDeployment(ALARM_ROUTING_SERVICE_TEST_WAR)
    @InSequence(1)
    public void createTestObjects() throws InterruptedException {
        logger.info(" Creating The Network Element  and Alarms Under it for Testing ");
        for (Integer i = 1; i < 2; i++) {
            final String fdn = "LTE01ERBS0000" + i.toString();
            dummyDataCreator.createNetworkElement(fdn);
        }
    }

    @Test
    @OperateOnDeployment(ALARM_ROUTING_SERVICE_TEST_WAR)
    @InSequence(2)
    public void assertAutoAckSuccess() throws InterruptedException {
        logger.info("Sending INPUT event to ATR flow for autoack");
        for (final long poId : dummyDataCreator.getPoIdList()) {
            sender.send(fillATRInputEvents(poId, "NetworkElement=LTE01ERBS00001", "SpecificProblem1"));
        }
        Thread.sleep(60000); //NOSONAR
        for (final long ackPoId : dummyDataCreator.getPoIdList()) {
            Assert.assertEquals(ProcessedEventState.ACTIVE_ACKNOWLEDGED.toString(), dummyDataCreator.getAlarmState(ackPoId));
        }
    }

    @Test
    @OperateOnDeployment(ALARM_ROUTING_SERVICE_TEST_WAR)
    @InSequence(3)
    public void assertAutoAckNotDone() throws InterruptedException {
        final Long poId = dummyDataCreator.createAlarm(NETWORK_ELEMENT_PREFIX.concat(NE_NAME), 1,
                ProcessedEventState.ACTIVE_UNACKNOWLEDGED.toString(), "CRITICAL", "SpecificProblem1", "eventType1", "ProbableCause1", 111);
        sender.send(fillATRInputEvents(poId, NETWORK_ELEMENT_PREFIX.concat(NE_NAME), "SpecificProblem1"));
        Assert.assertEquals(ProcessedEventState.ACTIVE_UNACKNOWLEDGED.toString(), dummyDataCreator.getAlarmState(poId));

    }

    @Test
    @OperateOnDeployment(ALARM_ROUTING_SERVICE_TEST_WAR)
    @InSequence(4)
    public void assertAutoAckNotDoneWithWrongSpecificProblem() throws InterruptedException {
        final Long poId = dummyDataCreator.createAlarm(NETWORK_ELEMENT_PREFIX.concat("LTE01ERBS00002"), 1,
                ProcessedEventState.ACTIVE_UNACKNOWLEDGED.toString(), "CRITICAL", "testalarm", "eventType1", "ProbableCause1", 111);
        sender.send(fillATRInputEvents(poId, NETWORK_ELEMENT_PREFIX.concat("LTE01ERBS00002"), "testalarm"));
        Assert.assertEquals(ProcessedEventState.ACTIVE_UNACKNOWLEDGED.toString(), dummyDataCreator.getAlarmState(poId));

    }

    @Test
    @OperateOnDeployment(ALARM_ROUTING_SERVICE_TEST_WAR)
    @InSequence(5)
    public void assertAutoAckSuccessWithCorrectSpecificProblem() throws InterruptedException {
        final Long poId = dummyDataCreator.createAlarm(NETWORK_ELEMENT_PREFIX.concat("LTE01ERBS00002"), 1,
                ProcessedEventState.ACTIVE_UNACKNOWLEDGED.toString(), "CRITICAL", "ACKSUCCESSSP", "eventType1", "ProbableCause1", 111);
        sender.send(fillATRInputEvents(poId, NETWORK_ELEMENT_PREFIX.concat("LTE01ERBS00002"), "ACKSUCCESSSP"));
        Thread.sleep(2000); //NOSONAR
        Assert.assertEquals(ProcessedEventState.ACTIVE_ACKNOWLEDGED.toString(), dummyDataCreator.getAlarmState(poId));

    }

    @Test
    @OperateOnDeployment(ALARM_ROUTING_SERVICE_TEST_WAR)
    @InSequence(6)
    public void assertAutoAckFailsWithWrongOorNoSub() throws InterruptedException {
        final Long poId = dummyDataCreator.createAlarm(NETWORK_ELEMENT_PREFIX.concat("LTE01ERBS00003").concat(",ManagedElement=1"), 1,
                ProcessedEventState.ACTIVE_UNACKNOWLEDGED.toString(), "CRITICAL", "ACKSUCCESSSP", "eventType1", "ProbableCause1", 111);
        sender.send(fillATRInputEvents(poId, NETWORK_ELEMENT_PREFIX.concat("LTE01ERBS00003").concat(",ManagedElement=1"), "ACKFAILSP"));
        Assert.assertEquals(ProcessedEventState.ACTIVE_UNACKNOWLEDGED.toString(), dummyDataCreator.getAlarmState(poId));

    }

    @Test
    @OperateOnDeployment(ALARM_ROUTING_SERVICE_TEST_WAR)
    @InSequence(7)
    public void assertAutoAckSuccessWithCorrectOorNoSub() throws InterruptedException {
        final Long poId = dummyDataCreator.createAlarm(NETWORK_ELEMENT_PREFIX.concat("LTE01ERBS00003"), 1,
                ProcessedEventState.ACTIVE_UNACKNOWLEDGED.toString(), "CRITICAL", "ACKSUCCESSSP", "eventType1", "ProbableCause1", 111);
        sender.send(fillATRInputEvents(poId, NETWORK_ELEMENT_PREFIX.concat("LTE01ERBS00003"), "ACKFAILSP"));
        Thread.sleep(2000); //NOSONAR
        Assert.assertEquals(ProcessedEventState.ACTIVE_ACKNOWLEDGED.toString(), dummyDataCreator.getAlarmState(poId));

    }

    @Test
    @OperateOnDeployment(ALARM_ROUTING_SERVICE_TEST_WAR)
    @InSequence(8)
    public void assertAutoAckNotHappenForNodeNotPresentInRule() throws InterruptedException {
        final Long poId = dummyDataCreator.createAlarm(NETWORK_ELEMENT_PREFIX.concat("LTE01"), 1,
                ProcessedEventState.ACTIVE_UNACKNOWLEDGED.toString(), "CRITICAL", "test", "eventType1", "ProbableCause1", 111);
        sender.send(fillATRInputEvents(poId, NETWORK_ELEMENT_PREFIX.concat("LTE01"), "test"));
        Thread.sleep(2000); //NOSONAR
        Assert.assertEquals(ProcessedEventState.ACTIVE_UNACKNOWLEDGED.toString(), dummyDataCreator.getAlarmState(poId));

    }

    private ATRInputEvent fillATRInputEvents(final long poId, final String fdn, final String specificProblem) {
        final ATRInputEvent atrInputEvent = new ATRInputEvent();
        List<String> fndList = new ArrayList<String>(1);
        fndList.add(fdn);
        atrInputEvent.setAlarmId(111L);
        atrInputEvent.setAlarmNumber(111L);
        atrInputEvent.setAlarmState(ProcessedEventState.ACTIVE_UNACKNOWLEDGED);
        atrInputEvent.setEventPOId(poId);
        atrInputEvent.setEventTime(new Date());
        atrInputEvent.setEventType("eventType1");
        atrInputEvent.setFdn(fdn);
        atrInputEvent.setObjectOfReference(fdn);
        atrInputEvent.setPresentSeverity(ProcessedEventSeverity.CRITICAL);
        atrInputEvent.setProbableCause("ProbableCause1");
        atrInputEvent.setRecordType(FMProcessedEventType.ALARM);
        atrInputEvent.setSpecificProblem(specificProblem);
        final Map<String, String> additionalInformation = new HashMap<String, String>(5);
        additionalInformation.put("fdn", fdn);
        atrInputEvent.setAdditionalInformation(additionalInformation);
        logger.info("Returning the event :{}", atrInputEvent);
        return atrInputEvent;
    }

}
