package com.craftsman.eventsourcing.service;

import com.craftsman.eventsourcing.entity.ContractView;
import com.craftsman.eventsourcing.entity.ContractViewRepository;
import com.craftsman.eventsourcing.es.ContractAggregate;
import com.craftsman.eventsourcing.es.command.QueryContractCommand;
import com.craftsman.eventsourcing.helper.ContractAggregateViewMapper;
import lombok.AllArgsConstructor;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@AllArgsConstructor
public class ContractViewService {

    private final QueryGateway queryGateway;
    private final ContractViewRepository contractViewRepository;

    public void updateViewFromAggregateById(Long aggregateIdentifier, Instant time) {

        QueryContractCommand command = new QueryContractCommand(aggregateIdentifier, time);
        ContractAggregate aggregate = queryGateway.query(command, ContractAggregate.class).join();
        ContractView view = contractViewRepository.findById(aggregateIdentifier).orElse(new ContractView());

        ContractAggregateViewMapper.mapAggregateToView(aggregate, view);
        contractViewRepository.save(view);
    }
}
