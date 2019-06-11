package com.craftsman.eventsourcing.controller;

import com.craftsman.eventsourcing.es.ContractAggregate;
import com.craftsman.eventsourcing.es.command.CreateContractCommand;
import com.craftsman.eventsourcing.es.command.DeleteContractCommand;
import com.craftsman.eventsourcing.es.command.QueryContractCommand;
import com.craftsman.eventsourcing.es.command.UpdateContractCommand;
import lombok.AllArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.Instant;

@RestController
@RequestMapping("/contracts")
@AllArgsConstructor
public class ContractController {

    private final CommandGateway commandGateway;
    private final QueryGateway queryGateway;

    @PostMapping
    public Long createContract(@RequestBody @Valid CreateContractCommand command) {
        return commandGateway.sendAndWait(command);
    }

    @PutMapping("/{id}")
    public void updateContract(@PathVariable("id") Long id, @RequestBody @Valid UpdateContractCommand command) {
        command.setIdentifier(id);
        commandGateway.send(command);
    }

    @DeleteMapping("/{id}")
    public void deleteContract(@PathVariable("id") Long id) {
        commandGateway.send(new DeleteContractCommand(id));
    }

    @GetMapping("/{id}")
    public ContractAggregate getContract(@PathVariable("id") Long id) {
        QueryContractCommand command = new QueryContractCommand(id, Instant.now());

        return queryGateway.query(command, ContractAggregate.class).join();
    }
}
