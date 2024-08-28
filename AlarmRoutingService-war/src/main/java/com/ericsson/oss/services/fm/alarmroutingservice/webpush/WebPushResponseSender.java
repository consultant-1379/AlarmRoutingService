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

package com.ericsson.oss.services.fm.alarmroutingservice.webpush;

import java.util.HashMap;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.services.fm.alarmroutingservice.models.AlarmRouteFileExportResponse;
import com.ericsson.oss.uisdk.restsdk.webpush.api.WebPushRestEvent;
import com.ericsson.oss.uisdk.restsdk.webpush.api.impl.WebPushRestEventImpl;

/**
 * Responsible for broadcasting webpush response in {@link WebPushRestEvent} with filter attributes.
 */
@Stateless
public class WebPushResponseSender {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebPushResponseSender.class);
    private static final String SUBSCRIPTION_ID = "subscriptionId";

    @Inject
    private WebPushChannelPublisher webpushChannelPublisher;

    public void broadcastMessage(final AlarmRouteFileExportResponse routeFileExportResponse) {
        final ObjectMapper objectMapper = new ObjectMapper();
        String jsonAsString = "";
        try {
            jsonAsString = objectMapper.writeValueAsString(routeFileExportResponse);
        } catch (final Exception exception) {
            LOGGER.error("Failed to send RouteFileExport status completed message to WebPush due to : ", exception);
        }

        final WebPushRestEvent event = new WebPushRestEventImpl(jsonAsString);
        final HashMap<String, String> filterMap = new HashMap<>();
        filterMap.put(SUBSCRIPTION_ID, routeFileExportResponse.getResponseMap().get(SUBSCRIPTION_ID));
        try {
            webpushChannelPublisher.getWebPushClient().broadcast(event, filterMap);
            LOGGER.debug("The route file export is done and sent completed message to WebPush: {}", event);
        } catch (final Exception exception) {
            LOGGER.error("Failed to send RouteFileExport status completed message to WebPush due to : ", exception);
        }
    }
}
