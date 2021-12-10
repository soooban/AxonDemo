package com.craftsman.eventsourcing.es.continuance.common;

public interface MetaDataUserInterface {

    /**
     * 用户名
     */
    String getName();

    /**
     * 用户 id
     */
    Long getUserId();

    /**
     * 租户 id
     */
    Long getCustomerId();
}
