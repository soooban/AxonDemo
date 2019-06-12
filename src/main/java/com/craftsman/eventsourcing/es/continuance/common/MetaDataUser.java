package com.craftsman.eventsourcing.es.continuance.common;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MetaDataUser implements MetaDataUserInterface {

    private String name;

    private Long userId;

    private Long customerId;
}
