package com.craftsman.eventsourcing.es.continuance.producer.jpa;

import lombok.extern.slf4j.Slf4j;
import org.axonframework.common.AxonConfigurationException;
import org.axonframework.common.AxonThreadFactory;
import org.axonframework.eventhandling.DomainEventMessage;
import org.axonframework.eventhandling.EventMessage;
import org.axonframework.eventsourcing.eventstore.DomainEventStream;
import org.axonframework.eventsourcing.eventstore.EmbeddedEventStore;
import org.axonframework.eventsourcing.eventstore.EventStorageEngine;
import org.axonframework.eventsourcing.eventstore.IteratorBackedDomainEventStream;
import org.axonframework.monitoring.MessageMonitor;

import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

@Slf4j
public class CustomEmbeddedEventStore extends EmbeddedEventStore {

    public CustomEmbeddedEventStore(Builder builder) {
        super(builder);
    }

    public static CustomEmbeddedEventStore.Builder builder() {
        return new CustomEmbeddedEventStore.Builder();
    }

    public DomainEventStream readEvents(String aggregateIdentifier, Instant timestamp) {
        Optional<DomainEventMessage<?>> optionalSnapshot;
        try {
            optionalSnapshot = storageEngine().readSnapshot(aggregateIdentifier);
        } catch (Exception | LinkageError e) {
            log.warn("Error reading snapshot. Reconstructing aggregate from entire event com.craftsman.eventsourcing.stream.", e);
            optionalSnapshot = Optional.empty();
        }
        DomainEventStream eventStream;
        // 加上时间判断，如果 snapshot 在指定的时间之间，那么可以使用，否则直接读取所有的 events
        if (optionalSnapshot.isPresent() && optionalSnapshot.get().getTimestamp().compareTo(timestamp) <= 0) {
            DomainEventMessage<?> snapshot = optionalSnapshot.get();
            eventStream = DomainEventStream.concat(DomainEventStream.of(snapshot),
                storageEngine().readEvents(aggregateIdentifier,
                    snapshot.getSequenceNumber() + 1));
        } else {
            eventStream = storageEngine().readEvents(aggregateIdentifier);
        }

        eventStream = new IteratorBackedDomainEventStream(eventStream.asStream().filter(m -> m.getTimestamp().compareTo(timestamp) <= 0).iterator());

        Stream<? extends DomainEventMessage<?>> domainEventMessages = stagedDomainEventMessages(aggregateIdentifier);
        return DomainEventStream.concat(eventStream, DomainEventStream.of(domainEventMessages));
    }

    public static class Builder extends EmbeddedEventStore.Builder {


        @Override
        public CustomEmbeddedEventStore.Builder storageEngine(EventStorageEngine storageEngine) {
            super.storageEngine(storageEngine);
            return this;
        }

        @Override
        public CustomEmbeddedEventStore.Builder messageMonitor(MessageMonitor<? super EventMessage<?>> messageMonitor) {
            super.messageMonitor(messageMonitor);
            return this;
        }

        /**
         * Sets the maximum number of events in the cache that is shared between the streams of tracking event
         * processors. Defaults to {@code 10000}.
         *
         * @param cachedEvents an {@code int} specifying the maximum number of events in the cache that is shared
         *                     between the streams of tracking event processors
         *
         * @return the current Builder instance, for fluent interfacing
         */
        public CustomEmbeddedEventStore.Builder cachedEvents(int cachedEvents) {
            super.cachedEvents(cachedEvents);
            return this;
        }

        /**
         * Sets the time to wait before fetching new events from the backing storage engine while tracking after a
         * previous com.craftsman.eventsourcing.stream was fetched and read. Note that this only applies to situations in which no events from the
         * current application have meanwhile been committed. If the current application commits events then those
         * events are fetched without delay.
         * <p>
         * Defaults to {@code 1000}. Together with the {@link EmbeddedEventStore.Builder#timeUnit}, this will define the exact fetch delay.
         *
         * @param fetchDelay a {@code long} specifying the time to wait before fetching new events from the backing
         *                   storage engine while tracking after a previous com.craftsman.eventsourcing.stream was fetched and read
         *
         * @return the current Builder instance, for fluent interfacing
         */
        public CustomEmbeddedEventStore.Builder fetchDelay(long fetchDelay) {
            super.fetchDelay(fetchDelay);
            return this;
        }

        /**
         * Sets the delay between two clean ups of lagging event processors. An event processor is lagging behind and
         * removed from the set of processors that track cached events if the oldest event in the cache is newer than
         * the last processed event of the event processor. Once removed the processor will be independently fetching
         * directly from the event storage engine until it has caught up again. Event processors will not notice this
         * change during tracking (i.e. the com.craftsman.eventsourcing.stream is not closed when an event processor falls behind and is removed).
         * <p>
         * Defaults to {@code 1000}. Together with the {@link EmbeddedEventStore.Builder#timeUnit}, this will define the exact clean up
         * delay.
         *
         * @param cleanupDelay a {@code long} specifying the delay between two clean ups of lagging event processors
         *
         * @return the current Builder instance, for fluent interfacing
         */
        public CustomEmbeddedEventStore.Builder cleanupDelay(long cleanupDelay) {
            super.cleanupDelay(cleanupDelay);
            return this;
        }

        /**
         * Sets the {@link TimeUnit} for the {@link EmbeddedEventStore.Builder#fetchDelay} and {@link EmbeddedEventStore.Builder#cleanupDelay}. Defaults to
         * {@link TimeUnit#MILLISECONDS}.
         *
         * @param timeUnit the {@link TimeUnit} for the {@link EmbeddedEventStore.Builder#fetchDelay} and {@link EmbeddedEventStore.Builder#cleanupDelay}
         *
         * @return the current Builder instance, for fluent interfacing
         */
        public CustomEmbeddedEventStore.Builder timeUnit(TimeUnit timeUnit) {
            super.timeUnit(timeUnit);
            return this;
        }

        /**
         * Sets the {@link ThreadFactory} used to create threads for consuming, producing and cleaning up. Defaults to
         * a {@link AxonThreadFactory} with {@link ThreadGroup} {@link EmbeddedEventStore#THREAD_GROUP}.
         *
         * @param threadFactory a {@link ThreadFactory} used to create threads for consuming, producing and cleaning up
         *
         * @return the current Builder instance, for fluent interfacing
         */
        public CustomEmbeddedEventStore.Builder threadFactory(ThreadFactory threadFactory) {
            super.threadFactory(threadFactory);
            return this;
        }

        /**
         * Sets whether event consumption should be optimized between Event Stream. If set to {@code true}, distinct
         * Event Consumers will read events from the same com.craftsman.eventsourcing.stream as soon as they reach the head of the com.craftsman.eventsourcing.stream. If
         * {@code false}, they will stay on a private com.craftsman.eventsourcing.stream. The latter means more database resources will be used, but
         * no side threads are created to fill the consumer cache nor locking is done on consumer threads. This field
         * can also be configured by providing a system property with key {@code optimize-event-consumption}. Defaults
         * to {@code true}.
         *
         * @param optimizeEventConsumption a {@code boolean} defining whether to optimize event consumption of threads
         *                                 by introducing a Event Cache Production thread tailing the head of the com.craftsman.eventsourcing.stream
         *                                 for the consumers
         *
         * @return the current Builder instance, for fluent interfacing
         */
        public CustomEmbeddedEventStore.Builder optimizeEventConsumption(boolean optimizeEventConsumption) {
            super.optimizeEventConsumption(optimizeEventConsumption);
            return this;
        }

        /**
         * Initializes a {@link EmbeddedEventStore} as specified through this Builder.
         *
         * @return a {@link EmbeddedEventStore} as specified through this Builder
         */
        public CustomEmbeddedEventStore build() {
            return new CustomEmbeddedEventStore(this);
        }

        /**
         * Validates whether the fields contained in this Builder are set accordingly.
         *
         * @throws AxonConfigurationException if one field is asserted to be incorrect according to the Builder's
         *                                    specifications
         */
        @Override
        protected void validate() throws AxonConfigurationException {
            super.validate();
        }

    }
}
