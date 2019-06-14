package com.craftsman.eventsourcing.es.continuance.producer.health;

import com.craftsman.eventsourcing.es.continuance.producer.jpa.CustomDomainEventEntryRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class EventHealthContributor implements InfoContributor {

    private final CustomDomainEventEntryRepository customDomainEventEntryRepository;

    @Override
    public void contribute(Info.Builder builder) {
        Long count = customDomainEventEntryRepository.countBySentFalse();

        builder.withDetail("failedMessage", count);
    }
}
