package com.craftsman.eventsourcing.es.event;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ContractDeletedEvent extends AbstractEvent {

    public ContractDeletedEvent(Long identifier) {
        super(identifier);
    }
}
