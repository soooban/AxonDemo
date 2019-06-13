package com.craftsman.eventsourcing.es.continuance.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;

@Getter
@Setter
@Slf4j
@NoArgsConstructor
@AllArgsConstructor
public class DomainEvent<P, M> {

    /**
     * 领域类型，用于区分事件领域，如 Building，不可为 null
     */
    private String type;

    /**
     * 聚合根 ID，如 building 的 ID，不可为 null
     */
    private String aggregateIdentifier;

    /**
     * 事件类型，如 BuildingCreatedEvent，不可为 null
     */
    private String payloadType;

    /**
     * 事件版本，标记 payload 结构版本，不可为 null
     */
    private String payloadRevision;

    /**
     * 同一聚合根下的事件序号，如果没有事件存储的服务可以为 null，optional
     */
    private Long sequenceNumber;

    /**
     * 事件唯一 ID，保证在服务内唯一即可，如果没有事件存储的服务可以为 null，optional
     */
    private String eventIdentifier;

    /**
     * 事件发生时间，不可为 null
     */
    private Instant timestamp;

    /**
     * 事件内容，optional
     */
    private P payload;

    /**
     * 事件附加信息，可以存放操作人等信息，optional
     */
    private M metaData;
}
