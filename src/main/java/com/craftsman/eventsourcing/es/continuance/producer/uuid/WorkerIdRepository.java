package com.craftsman.eventsourcing.es.continuance.producer.uuid;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkerIdRepository extends JpaRepository<WorkerId, Long> {
    WorkerId findByServiceKey(String serviceKey);
}
