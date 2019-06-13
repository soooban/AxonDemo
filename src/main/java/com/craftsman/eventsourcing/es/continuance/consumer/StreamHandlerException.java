package com.craftsman.eventsourcing.es.continuance.consumer;

class StreamHandlerException extends RuntimeException {

    StreamHandlerException(String message) {
        super(message);
    }

    StreamHandlerException(String message, Throwable cause) {
        super(message, cause);
    }
}
