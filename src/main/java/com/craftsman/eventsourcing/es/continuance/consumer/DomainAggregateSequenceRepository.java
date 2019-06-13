package com.craftsman.eventsourcing.es.continuance.consumer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DomainAggregateSequenceRepository extends JpaRepository<DomainAggregateSequence, Long> {

    /**
     * 根据 aggregate id 和 type 找到对应的记录
     *
     * @param identifier id
     * @param type       type
     *
     * @return entity
     */
    DomainAggregateSequence findByAggregateIdentifierAndType(Long identifier, String type);

}
