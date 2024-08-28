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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jboss.arquillian.protocol.servlet.arq514hack.descriptors.api.application.ApplicationDescriptor;
import org.jboss.shrinkwrap.api.Node;
import org.jboss.shrinkwrap.api.asset.Asset;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.descriptor.api.Descriptors;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolverSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Maven artifact constants for JEE tests
 */
public class Artifact {
    private static final Logger logger = LoggerFactory.getLogger(Artifact.class);

    /**
     * Artifacts needed for this test
     */
    public static final String FM_PROCESSED_EVENT_MODEL = "com.ericsson.oss.services.fm.models:fmprocessedeventmodel-jar:jar:?";
    public static final String ALARM_ACTION_SERVICE_EAR = "com.ericsson.nms.services:AlarmActionService-ear:ear:?";
    public static final String ALARM_ROUTING_SERVICE_API = "com.ericsson.nms.services:AlarmRoutingService-api:jar:?";
    public static final String ACCESS_CONTROL_SERVICE_API = "com.ericsson.oss.services.security.accesscontrol:access-control-service-api:jar:?";

    /**
     * Resolve artifact with given coordinates without any dependencies, this method should be used to resolve just the artifact with given name, and
     * it can be used for adding artifacts as modules into EAR
     *
     * If artifact can not be resolved, or the artifact was resolved into more then one file then the IllegalStateException will be thrown
     *
     *
     * @param artifactCoordinates
     *            in usual maven format
     *
     *            <pre>
     * {@code<groupId>:<artifactId>[:<extension>[:<classifier>]]:<version>}
     * </pre>
     * @return File representing resolved artifact
     */
    public static File resolveArtifactWithoutDependencies(final String artifactCoordinates) {
        final File[] artifacts = resolveFiles(artifactCoordinates);
        if (artifacts == null) {
            throw new IllegalStateException("Artifact with coordinates " + artifactCoordinates + " was not resolved");
        }

        if (artifacts.length != 1) {
            throw new IllegalStateException("Resolved more then one artifact with coordinates " + artifactCoordinates);
        }
        return artifacts[0];
    }

    /**
     * Resolve dependencies for artifact with given coordinates, if artifact can not be resolved IllegalState exception will be thrown
     *
     * @param artifactCoordinates
     *            in usual maven format
     *
     *            <pre>
     * {@code<groupId>:<artifactId>[:<extension>[:<classifier>]]:<version>}
     * </pre>
     *
     * @return resolved dependencies
     */
    public static File[] resolveArtifactDependencies(final String artifactCoordinates) {
        final MavenResolverSystem resolver = Maven.resolver();
        resolver.loadPomFromFile("pom.xml");
        final File[] artifacts = resolver.resolve(artifactCoordinates).withTransitivity().asFile();
        return artifacts;
    }

    /**
     * Resolve Files from coordinates.
     * @param coordinates the coordinates string.
     * @return the files.
     */
    public static File[] resolveFiles(final String coordinates) {
        return Maven.resolver().loadPomFromFile("pom.xml").resolve(coordinates).withoutTransitivity().asFile();

    }

    public static void createCustomApplicationXmlFile(final EnterpriseArchive serviceEar, final String webModuleName) {

        final Node node = serviceEar.get("META-INF/application.xml");
        ApplicationDescriptor desc = Descriptors.importAs(ApplicationDescriptor.class).fromStream(node.getAsset().openStream());

        desc.webModule(webModuleName + ".war", webModuleName);
        final String descriptorAsString = desc.exportAsString();

        serviceEar.delete(node.getPath());
        desc = Descriptors.importAs(ApplicationDescriptor.class).fromString(descriptorAsString);

        final Asset asset = new Asset() {
            @Override
            public InputStream openStream() {
                final ByteArrayInputStream bi = new ByteArrayInputStream(descriptorAsString.getBytes());
                return bi;
            }
        };
        serviceEar.addAsManifestResource(asset, "application.xml");
    }

}
