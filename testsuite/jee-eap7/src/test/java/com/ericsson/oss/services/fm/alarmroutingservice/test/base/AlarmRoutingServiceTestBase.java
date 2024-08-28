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

import java.io.File;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.Node;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.itpf.sdk.security.accesscontrol.classic.AccessControlServiceMockImpl;
import com.ericsson.oss.services.fm.alarmroutingservice.integration.test.AlarmRoutingServiceIT;
import com.ericsson.oss.services.fm.alarmroutingservice.test.util.Artifact;

public class AlarmRoutingServiceTestBase {
    private static final Logger logger = LoggerFactory.getLogger(AlarmRoutingServiceTestBase.class);

    public static EnterpriseArchive createEnterpriseArchiveDeployment(final String artifactName) {
        final EnterpriseArchive ear = ShrinkWrap
                .createFromZipFile(EnterpriseArchive.class, Artifact.resolveArtifactWithoutDependencies(artifactName));
        return ear;
    }

    public final static WebArchive createTestArchive() {
        logger.debug("******Creating test archive******");

        final WebArchive testArchive = ShrinkWrap.create(WebArchive.class, "test_initiationarms.war");
        testArchive.addClass(AlarmRoutingServiceIT.class);
        testArchive.addClass(DummyDataCreator.class);
        testArchive.addClass(AccessControlServiceMockImpl.class);
        testArchive.addAsLibraries(Maven.resolver().loadPomFromFile("pom.xml").resolve(Artifact.ACCESS_CONTROL_SERVICE_API).withoutTransitivity().asFile());
        testArchive.addAsLibraries(Maven.resolver().loadPomFromFile("pom.xml").resolve(Artifact.ALARM_ROUTING_SERVICE_API).withoutTransitivity().asFile());
        testArchive.addAsLibraries(Maven.resolver().loadPomFromFile("pom.xml").resolve(Artifact.FM_PROCESSED_EVENT_MODEL).withoutTransitivity().asFile());

        testArchive.addAsResource("META-INF/beans.xml", "META-INF/beans.xml");

        testArchive.setManifest(new StringAsset("Dependencies: com.ericsson.oss.itpf.datalayer.dps.api export\n"));

        return testArchive;
    }

}
