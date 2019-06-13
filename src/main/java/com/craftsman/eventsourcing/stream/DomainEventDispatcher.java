package com.craftsman.eventsourcing.stream;


import com.craftsman.eventsourcing.es.continuance.common.DomainEvent;
import com.craftsman.eventsourcing.es.continuance.consumer.StreamDomainEventDispatcher;
import com.craftsman.eventsourcing.stream.channel.ChannelDefinition;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@Transactional
@AllArgsConstructor
public class DomainEventDispatcher {

    private final StreamDomainEventDispatcher streamDomainEventDispatcher;

    @StreamListener(target = ChannelDefinition.CONTRACTS_INPUT, condition = "headers['messageType']=='eventSourcing'")
    public void handleBuilding(@Payload DomainEvent event) {
        streamDomainEventDispatcher.dispatchEvent(event, ChannelDefinition.CONTRACTS_INPUT);
    }
}
