package com.craftsman.eventsourcing.stream.channel;

/**
 * Created by frank on 2018/5/10.
 */
public class ChannelDefinition {

    public static final String CONTRACTS = "contract-events";

    public static final String CONTRACTS_INPUT = "contract-events-input";


    private ChannelDefinition() {
        throw new IllegalStateException("Utility class");
    }
}
