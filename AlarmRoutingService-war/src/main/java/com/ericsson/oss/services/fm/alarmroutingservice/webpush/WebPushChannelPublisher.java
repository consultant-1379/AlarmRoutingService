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

import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import com.ericsson.oss.uisdk.restsdk.webpush.api.WebPushClient;
import com.ericsson.oss.uisdk.restsdk.webpush.api.WebPushEndpoint;

/**
 * Publishes webpush channel for sending route file export operation progress to GUI clients.
 */
@Singleton
@Startup
public class WebPushChannelPublisher {

    private static final String EXPORT_URI = "/alarmRoutingExport";
    private static final String EXPORT_CHANNEL = "RouteFileExport";

    @Inject
    @WebPushEndpoint(resourceUrn = EXPORT_URI, channelName = EXPORT_CHANNEL)
    private WebPushClient webPushClient;

    public WebPushClient getWebPushClient() {
        return this.webPushClient;
    }

}