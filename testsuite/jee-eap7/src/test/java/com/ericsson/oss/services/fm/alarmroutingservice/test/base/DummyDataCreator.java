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
package com.ericsson.oss.services.fm.alarmroutingservice.test.base;

import static com.ericsson.oss.services.fm.alarmroutingservice.test.util.TestConstants.ALARM_ROUTE_POLICY;
import static com.ericsson.oss.services.fm.alarmroutingservice.test.util.TestConstants.ALARM_STATE;
import static com.ericsson.oss.services.fm.alarmroutingservice.test.util.TestConstants.ENABLE_POLICY;
import static com.ericsson.oss.services.fm.alarmroutingservice.test.util.TestConstants.FDN;
import static com.ericsson.oss.services.fm.alarmroutingservice.test.util.TestConstants.FM;
import static com.ericsson.oss.services.fm.alarmroutingservice.test.util.TestConstants.NETWORK_ELEMENT_PREFIX;
import static com.ericsson.oss.services.fm.alarmroutingservice.test.util.TestConstants.OPEN_ALARM;
import static com.ericsson.oss.services.fm.alarmroutingservice.test.util.TestConstants.OSS_NE_DEF;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.itpf.datalayer.dps.DataBucket;
import com.ericsson.oss.itpf.datalayer.dps.DataPersistenceService;
import com.ericsson.oss.itpf.datalayer.dps.persistence.ManagedObject;
import com.ericsson.oss.itpf.datalayer.dps.persistence.PersistenceObject;
import com.ericsson.oss.itpf.datalayer.dps.query.Query;
import com.ericsson.oss.itpf.datalayer.dps.query.QueryBuilder;
import com.ericsson.oss.itpf.datalayer.dps.query.QueryExecutor;
import com.ericsson.oss.itpf.datalayer.dps.query.TypeRestrictionBuilder;
import com.ericsson.oss.itpf.sdk.core.annotation.EServiceRef;
import com.ericsson.oss.services.fm.models.processedevent.ProcessedEventState;

@Singleton
@Startup
public class DummyDataCreator {

    private final static Logger LOGGER = LoggerFactory.getLogger(DummyDataCreator.class);

    @EServiceRef
    private DataPersistenceService service;

    private QueryExecutor queryExecutor;

    private static List<Long> poIds = new ArrayList<Long>();

    private static List<String> createdTimeList = new ArrayList<String>();

    @PostConstruct
    public void createNE() {
        LOGGER.info(" Creating The Network Element  and Alarms Under it for Testing ");
        for (Integer i = 1; i < 4; i++) {
            createRoute("LTE01ERBS0000" + i);
        }
    }

    @PreDestroy
    public void removeall() {
        LOGGER.info(" Clearing the Network Element  and Alarms Under it for Testing ");
        removeNetworkElement();
        removeTestAlarms();
        deleteData();
    }

    public void createNetworkElement(final String nodeName) {
        try {
            LOGGER.info("createNetworkElement******************");
            final DataBucket liveBucket = service.getLiveBucket();
            final Map<String, Object> moAttributes = new HashMap<String, Object>();
            moAttributes.put("networkElementId", "testId");
            moAttributes.put("neType", "ERBS");
            moAttributes.put("platformType", "CPP");
            moAttributes.put("ossModelIdentity", "1294-439-662");
            moAttributes.put("ossPrefix", "MeContext=" + nodeName);

            final ManagedObject networkElement = liveBucket.getMibRootBuilder().type("NetworkElement").namespace("OSS_NE_DEF").version("2.0.0")
                    .name(nodeName).addAttributes(moAttributes).create();

            LOGGER.info(" Network Element Created with FDN " + networkElement.getFdn());

            for (int i = 1; i < 101; i++) {
                poIds.add(createAlarm(NETWORK_ELEMENT_PREFIX.concat(nodeName), 1, ProcessedEventState.ACTIVE_UNACKNOWLEDGED.toString(), "CRITICAL",
                        "SpecificProblem1", "eventType1", "ProbableCause1", i));
            }

        } catch (final Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    public void removeNetworkElement() {
        final DataBucket liveBucket = service.getLiveBucket();
        LOGGER.info(" Mo deleting from DPS with FDN MeContext=1 ");
        final QueryBuilder queryBuilder = service.getQueryBuilder();
        final Query<TypeRestrictionBuilder> typeQuery = queryBuilder.createTypeQuery(OSS_NE_DEF, "NetworkElement");
        final Iterator<PersistenceObject> iterator = liveBucket.getQueryExecutor().execute(typeQuery);
        while (iterator.hasNext()) {
            final PersistenceObject persistenceObject = iterator.next();
            liveBucket.deletePo(persistenceObject);
        }

    }

    public void removeTestAlarms() {
        final DataBucket liveBucket = service.getLiveBucket();
        LOGGER.info(" Removing All Alarms ");
        final QueryBuilder queryBuilder = service.getQueryBuilder();
        final Query<TypeRestrictionBuilder> typeQuery = queryBuilder.createTypeQuery(FM, OPEN_ALARM);
        queryExecutor = liveBucket.getQueryExecutor();

        final Iterator<PersistenceObject> poListIterator = queryExecutor.execute(typeQuery);

        while (poListIterator.hasNext()) {
            final PersistenceObject persistenceObject = poListIterator.next();
            LOGGER.info("alarmState is :: {}", persistenceObject.getAttribute(ALARM_STATE).toString());
            LOGGER.info("poId is :: {}", persistenceObject.getPoId());
            liveBucket.deletePo(persistenceObject);
        }

    }

    public Long createAlarm(final String neFdn, final long i, final String alarmState, final String presentSeverity, final String specificProblem,
                            final String eventType, final String probableCause, final long alarmNumber) {
        final DataBucket liveBucket = service.getLiveBucket();
        final Map<String, Object> OpenAlarmMap = new HashMap<String, Object>();
        OpenAlarmMap.put("objectOfReference", neFdn);
        OpenAlarmMap.put(FDN, neFdn);
        OpenAlarmMap.put("eventTime", new Date());
        OpenAlarmMap.put("presentSeverity", presentSeverity);
        OpenAlarmMap.put("probableCause", probableCause);
        OpenAlarmMap.put("specificProblem", specificProblem);
        OpenAlarmMap.put("alarmNumber", alarmNumber);
        OpenAlarmMap.put("eventType", eventType);
        OpenAlarmMap.put("backupObjectInstance", "Unknown");
        OpenAlarmMap.put("recordType", "ALARM");
        OpenAlarmMap.put("backupStatus", true);
        OpenAlarmMap.put("trendIndication", "LESS_SEVERE");
        OpenAlarmMap.put("previousSeverity", "CRITICAL");
        OpenAlarmMap.put("proposedRepairAction", "Unknown");
        OpenAlarmMap.put("alarmId", alarmNumber);
        OpenAlarmMap.put("alarmState", alarmState);
        OpenAlarmMap.put("ceaseOperator", " ");
        OpenAlarmMap.put("commentText", "Hello World ");
        OpenAlarmMap.put("ackTime", new Date());
        OpenAlarmMap.put("ackOperator", "APSOperator");
        OpenAlarmMap.put("syncState", true);
        OpenAlarmMap.put("additionalInformation", "additionalInformation");
        OpenAlarmMap.put("problemDetail", "problemDetail");
        OpenAlarmMap.put("problemText", "problemText");

        final PersistenceObject po = liveBucket.getPersistenceObjectBuilder().namespace(FM).type(OPEN_ALARM).version("1.0.1")
                .addAttributes(OpenAlarmMap).create();

        LOGGER.info("Alarm Created with po id : {} and sp {}", po.getPoId(), specificProblem);
        return po.getPoId();
    }

    public void createRoute(final String nodeName) {
        final Map<String, Object> routePO = getAlarmRouteAttributes(nodeName);

        final DataBucket liveBucket = service.getLiveBucket();
        final PersistenceObject createRoutePO = liveBucket.getPersistenceObjectBuilder().namespace(FM).type(ALARM_ROUTE_POLICY)
                .addAttributes(routePO).version("1.0.0").create();

        LOGGER.info("Successsfully created routes for the node : {}", createRoutePO.getAttribute(FDN).toString());
        final SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("GMT+5:30"));

        createdTimeList.add(formatter.format(createRoutePO.getCreatedTime()));
    }

    private List<String> buildFDNs(final String nodeName) {
        final List<String> fdns = new ArrayList<String>(1);
        fdns.add(nodeName);
        return fdns;

    }

    private Map<String, Object> getAlarmRouteAttributes(final String nodeName) {
        final Map<String, Object> routePO = new HashMap<String, Object>();
        routePO.put("name", "route" + nodeName);
        routePO.put("fdns", buildFDNs(NETWORK_ELEMENT_PREFIX.concat(nodeName)));
        routePO.put(FDN, nodeName);
        routePO.put("outputType", "Auto_Ack");
        routePO.put("description", "ATRTEST");
        routePO.put(ENABLE_POLICY, true);
        if ("LTE01ERBS00002".equals(nodeName)) {
            routePO.put("specificProblem", "TESTALARM,ACKSUCCESSSP");
            LOGGER.info("Adding extra sp to second node with map: {}", routePO);
        } else if ("LTE01ERBS00003".equals(nodeName)) {
            routePO.put("subordinateType", "NO_SUBORDINATES");
            routePO.put("fdns", buildFDNs(NETWORK_ELEMENT_PREFIX.concat(nodeName)));
            LOGGER.info("Adding extra subordinate type and oor with map: {}", routePO);
        }
        return routePO;
    }

    public String getAlarmState(final Long poId) {
        final DataBucket liveBucket = service.getLiveBucket();
        final PersistenceObject alarmPo = liveBucket.findPoById(poId);
        if (alarmPo != null) {
            LOGGER.info("Returning the alarm state as : {} for poId : {}", alarmPo.getAttribute(ALARM_STATE), poId);
            return alarmPo.getAttribute("alarmState");
        }
        return null;
    }

    public List<String> getDateList() {
        return createdTimeList;
    }

    public List<Long> getPoIdList() {
        return poIds;
    }

    public void deleteData() {
        final DataBucket liveBucket = service.getLiveBucket();
        LOGGER.info(" Removing All Routes ");
        for (int i = 0; i < poIds.size(); i++) {
            LOGGER.info("CreatedTime {}", createdTimeList.get(i));
        }
        final QueryBuilder queryBuilder = service.getQueryBuilder();
        final Query<TypeRestrictionBuilder> typeQuery = queryBuilder.createTypeQuery(FM, ALARM_ROUTE_POLICY);
        final QueryExecutor queryExecutor = liveBucket.getQueryExecutor();

        final Iterator<PersistenceObject> poListIterator = queryExecutor.execute(typeQuery);

        while (poListIterator.hasNext()) {
            final PersistenceObject persistenceObject = poListIterator.next();
            LOGGER.info("  name of the deleting route {}", persistenceObject.getAttribute("name").toString());
            LOGGER.info("  poId of the deleting route {}", persistenceObject.getPoId());
            liveBucket.deletePo(persistenceObject);
        }
    }

}
