package com.craftsman.eventsourcing.es;

import javax.validation.constraints.NotBlank;

public interface ContractInterface {
    Long getIdentifier();

    @NotBlank
    String getName();

    @NotBlank
    String getPartyA();

    @NotBlank
    String getPartyB();
}
