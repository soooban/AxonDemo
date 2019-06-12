package com.craftsman.eventsourcing.es.command;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateContractCommand extends UpdateContractCommand {

    public CreateContractCommand(Long identifier, String name, String partyA, String partyB, String industryName) {
        super(identifier, name, partyA, partyB, industryName);
    }
}
