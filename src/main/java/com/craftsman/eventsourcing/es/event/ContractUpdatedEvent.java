package com.craftsman.eventsourcing.es.event;

import com.craftsman.eventsourcing.es.ContractInterface;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ContractUpdatedEvent extends AbstractEvent implements ContractInterface {

    private String name;

    private String partyA;

    private String partyB;

    private String industryName;

    public ContractUpdatedEvent(Long identifier, String name, String partyA, String partyB, String industryName) {
        super(identifier);
        this.name = name;
        this.partyA = partyA;
        this.partyB = partyB;
        this.industryName = industryName;
    }
}
