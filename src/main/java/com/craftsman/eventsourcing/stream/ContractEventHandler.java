package com.craftsman.eventsourcing.stream;

import com.craftsman.eventsourcing.es.continuance.common.DomainEvent;
import com.craftsman.eventsourcing.es.continuance.consumer.StreamEventHandler;
import com.craftsman.eventsourcing.es.event.ContractCreatedEvent;
import com.craftsman.eventsourcing.service.ContractViewService;
import com.craftsman.eventsourcing.stream.channel.ChannelDefinition;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;

@Component
@AllArgsConstructor
@Transactional
public class ContractEventHandler {

    private final ContractViewService contractViewService;

    @StreamEventHandler(types = ChannelDefinition.CONTRACTS_INPUT)
    public void handle(ContractCreatedEvent event, DomainEvent<ContractCreatedEvent, HashMap> domainEvent) {
        contractViewService.updateViewFromAggregateById(event.getIdentifier(), domainEvent.getTimestamp());
    }
}
