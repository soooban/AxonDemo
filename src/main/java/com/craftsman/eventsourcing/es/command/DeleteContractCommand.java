package com.craftsman.eventsourcing.es.command;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class DeleteContractCommand extends AbstractCommand {
    public DeleteContractCommand(Long identifier) {
        super(identifier);
    }
}
