package com.craftsman.eventsourcing.helper;

import com.craftsman.eventsourcing.entity.ContractView;
import com.craftsman.eventsourcing.es.ContractAggregate;

public class ContractAggregateViewMapper {

    public static void mapAggregateToView(ContractAggregate aggregate, ContractView view) {
        view.setId(aggregate.getIdentifier());
        view.setPartyA(aggregate.getPartyA());
        view.setPartyB(aggregate.getPartyB());
        view.setDeleted(aggregate.isDeleted());
        view.setName(aggregate.getName());
        view.setIndustryName(aggregate.getIndustryName());
    }
}
