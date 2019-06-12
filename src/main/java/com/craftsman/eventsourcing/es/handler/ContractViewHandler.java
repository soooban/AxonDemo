package com.craftsman.eventsourcing.es.handler;

import com.craftsman.eventsourcing.entity.ContractView;
import com.craftsman.eventsourcing.entity.ContractViewRepository;
import com.craftsman.eventsourcing.es.ContractAggregate;
import com.craftsman.eventsourcing.es.continuance.producer.jpa.CustomEventSourcingRepository;
import com.craftsman.eventsourcing.es.event.AbstractEvent;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.DomainEventMessage;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventsourcing.EventSourcedAggregate;
import org.axonframework.modelling.command.LockAwareAggregate;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.text.MessageFormat;

@Component
@Slf4j
@AllArgsConstructor
@Transactional
public class ContractViewHandler {

    private final CustomEventSourcingRepository<ContractAggregate> customEventSourcingRepository;

    private final ContractViewRepository contractViewRepository;


    /**
     * 任何 contract 事件发生之后，重新计算 aggregate 的最新状态，转换成 view 之后存储到本地
     *
     * @param event   any event from contract
     * @param message domain event wrapper
     */
    @EventHandler
    public void on(AbstractEvent event, DomainEventMessage<AbstractEvent> message) {


        log.info(MessageFormat.format("{0}: {1} , seq: {2}, payload: {3}", message.getType(), message.getAggregateIdentifier(), message.getSequenceNumber(), message.getPayload()));

        updateContractView(message.getAggregateIdentifier());
    }

    @Transactional
    public void updateContractView(String id) {
        LockAwareAggregate<ContractAggregate, EventSourcedAggregate<ContractAggregate>> lockAwareAggregate = customEventSourcingRepository.load(id);
        ContractAggregate aggregate = lockAwareAggregate.getWrappedAggregate().getAggregateRoot();


        ContractView contractView = contractViewRepository.findById(Long.valueOf(id)).orElse(new ContractView());
        contractView.setId(aggregate.getIdentifier());
        contractView.setDeleted(aggregate.isDeleted());
        contractView.setName(aggregate.getName());
        contractView.setPartyA(aggregate.getPartyA());
        contractView.setPartyB(aggregate.getPartyB());
        contractView.setIndustryName(aggregate.getIndustryName());
        contractViewRepository.save(contractView);
    }
}
