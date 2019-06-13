package com.craftsman.eventsourcing.es.continuance.producer.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomDomainEventEntryRepository extends JpaRepository<CustomDomainEventEntry, String> {

    /**
     * 查找事件
     *
     * @param identifier
     *
     * @return
     */
    CustomDomainEventEntry findByEventIdentifier(String identifier);
}
