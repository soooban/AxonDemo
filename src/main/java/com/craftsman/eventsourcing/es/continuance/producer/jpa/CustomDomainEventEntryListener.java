package com.craftsman.eventsourcing.es.continuance.producer.jpa;

import com.craftsman.eventsourcing.stream.ContractPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.persistence.PostPersist;
import javax.transaction.Transactional;
import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
public class CustomDomainEventEntryListener {
    private static CustomDomainEventEntryRepository customDomainEventEntryRepository;

    private static ContractPublisher contractPublisher;

    @Autowired
    public void init(CustomDomainEventEntryRepository customDomainEventEntryRepository, ContractPublisher contractPublisher) {
        this.customDomainEventEntryRepository = customDomainEventEntryRepository;
        this.contractPublisher = contractPublisher;
    }

    @PostPersist
    void onPersist(CustomDomainEventEntry entry) {

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {

            @Override
            public void afterCompletion(int status) {
                if (status == TransactionSynchronization.STATUS_COMMITTED) {
                    CompletableFuture.runAsync(() -> sendEvent(entry.getEventIdentifier()));
                }
            }
        });
    }

    @Transactional
    public void sendEvent(String identifier) {
        CustomDomainEventEntry eventEntry = customDomainEventEntryRepository.findByEventIdentifier(identifier);

        if (!eventEntry.isSent()) {
            contractPublisher.sendEvent(eventEntry);
            eventEntry.setSent(true);
            customDomainEventEntryRepository.save(eventEntry);
        }
    }
}
