package com.craftsman.eventsourcing.controller;

import com.craftsman.eventsourcing.es.ContractAggregate;
import com.craftsman.eventsourcing.es.ContractCommandGateway;
import com.craftsman.eventsourcing.es.command.CreateContractCommand;
import com.craftsman.eventsourcing.es.command.DeleteContractCommand;
import com.craftsman.eventsourcing.es.command.QueryContractCommand;
import com.craftsman.eventsourcing.es.command.UpdateContractCommand;
import lombok.AllArgsConstructor;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.Instant;

@RestController
@RequestMapping("/contracts")
@AllArgsConstructor
public class ContractController {

    private final ContractCommandGateway contractCommandGateway;
    private final QueryGateway queryGateway;

    @PostMapping
    public Long createContract(@RequestBody @Valid CreateContractCommand command) {
        return contractCommandGateway.sendCommandAndWaitForAResult(command);
    }

    @PutMapping("/{id}")
    public void updateContract(@PathVariable("id") Long id, @RequestBody @Valid UpdateContractCommand command) {
        command.setIdentifier(id);
        contractCommandGateway.sendCommandAndWait(command);
    }

    @DeleteMapping("/{id}")
    public void deleteContract(@PathVariable("id") Long id) {
        contractCommandGateway.sendCommandAndWait(new DeleteContractCommand(id));
    }

    @GetMapping("/{id}")
    public ContractAggregate getContract(@PathVariable("id") Long id) {
        QueryContractCommand command = new QueryContractCommand(id, Instant.now());

        return queryGateway.query(command, ContractAggregate.class).join();
    }
}
