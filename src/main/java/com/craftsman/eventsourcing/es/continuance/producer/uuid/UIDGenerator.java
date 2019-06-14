package com.craftsman.eventsourcing.es.continuance.producer.uuid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class UIDGenerator {

    private final WorkerIdService workerIdService;
    private SnowFlake flake;

    @Autowired
    public UIDGenerator(WorkerIdService workerIdService) {
        this.workerIdService = workerIdService;
    }

    public Long getId() {
        return flake.nextId();
    }

    @PostConstruct
    public void init() {
        this.flake = new SnowFlake(workerIdService.getWorkerId());
    }
}
