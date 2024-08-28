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

import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.RETRY_ATEMPTS;
import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.WAIT_INTERVAL;

import java.util.concurrent.TimeUnit;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.itpf.sdk.core.retry.RetriableCommand;
import com.ericsson.oss.itpf.sdk.core.retry.RetryContext;
import com.ericsson.oss.itpf.sdk.core.retry.RetryManager;
import com.ericsson.oss.itpf.sdk.core.retry.RetryPolicy;
import com.ericsson.oss.itpf.sdk.resources.Resource;
import com.ericsson.oss.itpf.sdk.resources.Resources;

/**
 * Responsible for getting file system resource for the given resource location using {@link RetryManager}.
 */
@ApplicationScoped
public class ResourceRetryManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceRetryManager.class);
    @Inject
    private RetryManager retryManager;

    /**
     * Method that tries to get the resource.
     * <P>
     * If the resource obtained is null ,then will retry on {@link ResourceNotFoundException}
     *
     * @param resourceLocation
     *            The resource location data to be written.
     * @return {@link Resource}
     */
    public Resource tryToGetResource(final String resourceLocation) {
        final RetryPolicy policy = RetryPolicy.builder().attempts(RETRY_ATEMPTS).waitInterval(WAIT_INTERVAL, TimeUnit.SECONDS)
                .retryOn(ResourceNotFoundException.class).build();

        return retryManager.executeCommand(policy, new RetriableCommand<Resource>() {
            @Override
            public Resource execute(final RetryContext retryContext) throws Exception {
                return getResource(resourceLocation);
            }
        });
    }

    /**
     * Returns the resource with the given resourceLocation.
     *
     * @param resourceLocation
     *            : resourceLocation
     * @return {@link Resource}
     * @throws ResourceNotFoundException
     *             : ResourceNotFoundException
     */
    private Resource getResource(final String resourceLocation) throws ResourceNotFoundException {
        Resource resource = null;
        try {
            resource = Resources.getFileSystemResource(resourceLocation);
        } catch (final Exception exception) {
            LOGGER.error("Exception :  caught, setting resource to null", exception);
            resource = null;
        }

        if (resource == null) {
            // Have to throw an exception as RetryPolicy can only retry based on a exception
            throw new ResourceNotFoundException("Could not find resource at " + resourceLocation);
        }
        return resource;
    }
}
