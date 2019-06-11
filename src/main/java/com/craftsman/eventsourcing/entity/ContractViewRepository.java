package com.craftsman.eventsourcing.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContractViewRepository extends JpaRepository<ContractView, Long> {

}
