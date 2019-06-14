package com.craftsman.eventsourcing.es.continuance.producer.jpa;

public interface AggregateSequenceNumberInterface {

    String getAggregateIdentifier();

    Long getSequenceNumber();
}
