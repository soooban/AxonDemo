package com.craftsman.eventsourcing.stream;

import com.craftsman.eventsourcing.es.continuance.common.DomainEvent;
import com.craftsman.eventsourcing.es.continuance.producer.jpa.CustomDomainEventEntry;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.HashMap;

@Component
@AllArgsConstructor
@Slf4j
public class ContractPublisher {

    public void sendEvent(DomainEvent event) {

        // use com.craftsman.eventsourcing.stream to send message here
        log.info(MessageFormat.format("prepare to send message : {0}]", new Gson().toJson(event)));
    }

    public void sendEvent(CustomDomainEventEntry event) {
        // use stream to send message here
        ObjectMapper mapper = new ObjectMapper();

        HashMap payload = null;
        HashMap metaData = null;
        try {
            payload = mapper.readValue(event.getPayload().getData(), HashMap.class);
            metaData = mapper.readValue(event.getMetaData().getData(), HashMap.class);
        } catch (Exception exception) {
            log.error(MessageFormat.format("byte[] to string failed; exception: {0}", exception));
        }

        if (payload == null || metaData == null) {
            log.warn(MessageFormat.format("nothing to send; exception: {0}", event.getEventIdentifier()));
            return;
        }

        DomainEvent<HashMap, HashMap> domainEvent = new DomainEvent<>(
            event.getType(),
            event.getAggregateIdentifier(),
            event.getPayload().getType().getName(),
            event.getPayload().getType().getRevision(),
            event.getSequenceNumber(),
            event.getEventIdentifier(),
            event.getTimestamp(),
            payload,
            metaData);

        this.sendEvent(domainEvent);
    }

}
