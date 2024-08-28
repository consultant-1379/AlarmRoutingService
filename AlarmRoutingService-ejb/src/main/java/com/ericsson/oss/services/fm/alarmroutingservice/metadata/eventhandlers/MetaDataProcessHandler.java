/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2015
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/

package com.ericsson.oss.services.fm.alarmroutingservice.metadata.eventhandlers;

import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.PAUSE_EVENT_VALUE;

import javax.inject.Named;

import com.ericsson.oss.itpf.common.Controllable;
import com.ericsson.oss.itpf.common.event.ControlEvent;
import com.ericsson.oss.itpf.common.event.handler.AbstractEventHandler;
import com.ericsson.oss.itpf.common.event.handler.EventInputHandler;
import com.ericsson.oss.itpf.sdk.core.annotation.EServiceRef;
import com.ericsson.oss.services.fm.alarmroutingservice.api.AlarmMetaDataInfo;
import com.ericsson.oss.services.fm.models.processedevent.AlarmMetadataInformation;

/**
 * MetaDataProcessHandler provides functionality to process alarm meta data event received from AlarmMetaDataChannel.
 */
@Named("metaDataProcessHandler")
public class MetaDataProcessHandler extends AbstractEventHandler implements EventInputHandler, Controllable {

    private static final boolean DESTROYED = false;
    private boolean paused;

    @EServiceRef
    private AlarmMetaDataInfo alarmMetaDataInfo;

    @Override
    public void react(final ControlEvent controlEvent) {
        if (controlEvent.getType() == PAUSE_EVENT_VALUE) {
            log.debug("Asked to pause - will do that");
            paused = true;
        }
    }

    @Override
    public void onEvent(final Object objInputEvent) {
        if (paused) {
            return;
        }
        if (DESTROYED) {
            throw new IllegalStateException("Component was already destroyed - should not be invoked again. Received event is " + objInputEvent);
        }
        if (objInputEvent == null) {
            log.error("Event received to MetaDataProcessHandler is null.");
            return;
        }
        final AlarmMetadataInformation metaDataInformation = (AlarmMetadataInformation) objInputEvent;
        log.debug("Event received to MetaDataProcessHandler {}", metaDataInformation);
        try {
            alarmMetaDataInfo.update(metaDataInformation);
        } catch (final Exception exception) {
            log.error("Exception occured while updating alarm meta data information :{}", exception.getMessage());
        }
    }

    @Override
    protected void doInit() {
    }

}
