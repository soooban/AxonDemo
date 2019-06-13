package com.craftsman.eventsourcing.es.continuance.consumer;

class StreamEventSequenceException extends RuntimeException {
    StreamEventSequenceException(String message) {
        super(message);
    }
}
