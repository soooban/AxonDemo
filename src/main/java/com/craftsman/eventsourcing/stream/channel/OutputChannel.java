package com.craftsman.eventsourcing.stream.channel;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

/**
 * Created by Troy on 12/05/2017.
 */
public interface OutputChannel {

    @Output(ChannelDefinition.CONTRACTS)
    MessageChannel contract();
}
