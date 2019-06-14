package com.craftsman.eventsourcing.es.continuance.producer.jpa;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    /**
     * 查找未发送的事件
     *
     * @param pageable
     *
     * @return
     */
    Page<CustomDomainEventEntry> findBySentFalse(Pageable pageable);

    /**
     * 查询未发送事件的数量
     *
     * @return
     */
    Long countBySentFalse();
}
