package com.craftsman.eventsourcing.es.upcaster;

import org.axonframework.serialization.upcasting.event.IntermediateEventRepresentation;
import org.axonframework.serialization.upcasting.event.SingleEventUpcaster;

import java.util.Arrays;
import java.util.List;

public class ContractEventUpCaster extends SingleEventUpcaster {
    private static List<SameEventUpCaster> upCasters = Arrays.asList(
        new ContractCreatedEventUpCaster(),
        new ContractUpdatedEventUpCaster()
    );

    @Override
    protected boolean canUpcast(IntermediateEventRepresentation intermediateRepresentation) {
        return upCasters.stream().anyMatch(o -> o.canUpcast(intermediateRepresentation));
    }

    @Override
    protected IntermediateEventRepresentation doUpcast(IntermediateEventRepresentation intermediateRepresentation) {
        SameEventUpCaster upCaster = upCasters.stream()
            .filter(o -> o.canUpcast(intermediateRepresentation))
            .findAny().orElseThrow(RuntimeException::new);
        return upCaster.doUpcast(intermediateRepresentation);
    }
}
