package com.craftsman.eventsourcing.stream.channel;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;


public interface InputChannel {

    @Input(ChannelDefinition.CONTRACTS_INPUT)
    SubscribableChannel contract();
}
