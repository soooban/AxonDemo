package com.craftsman.eventsourcing.stream;

import com.craftsman.eventsourcing.entity.ContractView;
import com.craftsman.eventsourcing.entity.ContractViewRepository;
import com.craftsman.eventsourcing.es.ContractAggregate;
import com.craftsman.eventsourcing.es.command.QueryContractCommand;
import com.craftsman.eventsourcing.es.continuance.common.DomainEvent;
import com.craftsman.eventsourcing.es.continuance.consumer.StreamEventHandler;
import com.craftsman.eventsourcing.es.event.ContractCreatedEvent;
import com.craftsman.eventsourcing.stream.channel.ChannelDefinition;
import lombok.AllArgsConstructor;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;

@Component
@AllArgsConstructor
@Transactional
public class ContractEventHandler {

    private final ContractViewRepository contractViewRepository;
    private final QueryGateway queryGateway;

    @StreamEventHandler(types = ChannelDefinition.CONTRACTS_INPUT)
    public void handle(ContractCreatedEvent event, DomainEvent<ContractCreatedEvent, HashMap> domainEvent) {
        QueryContractCommand command = new QueryContractCommand(event.getIdentifier(), domainEvent.getTimestamp());

        ContractAggregate aggregate = queryGateway.query(command, ContractAggregate.class).join();

        ContractView view = new ContractView();
        view.setIndustryName(aggregate.getIndustryName());
        view.setId(aggregate.getIdentifier());
        view.setPartyB(aggregate.getPartyB());
        view.setPartyA(aggregate.getPartyA());
        view.setName(aggregate.getName());
        view.setDeleted(aggregate.isDeleted());

        contractViewRepository.save(view);
    }
}
