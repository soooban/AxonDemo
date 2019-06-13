package com.craftsman.eventsourcing.es.continuance.producer.jpa;

import org.axonframework.common.AxonConfigurationException;
import org.axonframework.common.jdbc.PersistenceExceptionResolver;
import org.axonframework.common.jpa.EntityManagerProvider;
import org.axonframework.common.transaction.TransactionManager;
import org.axonframework.eventhandling.DomainEventData;
import org.axonframework.eventhandling.EventMessage;
import org.axonframework.eventsourcing.eventstore.jpa.JpaEventStorageEngine;
import org.axonframework.eventsourcing.eventstore.jpa.SQLErrorCodesResolver;
import org.axonframework.serialization.Serializer;
import org.axonframework.serialization.upcasting.event.EventUpcaster;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.function.Predicate;

import static org.axonframework.eventhandling.EventUtils.asDomainEventMessage;

public class CustomJpaEventStorageEngine extends JpaEventStorageEngine {

    public CustomJpaEventStorageEngine(Builder builder) {
        super(builder);
    }

    public static CustomJpaEventStorageEngine.Builder builder() {
        return new CustomJpaEventStorageEngine.Builder();
    }

    @Override
    protected Object createEventEntity(EventMessage<?> eventMessage, Serializer serializer) {
        return new CustomDomainEventEntry(asDomainEventMessage(eventMessage), serializer);
    }

    public static class Builder extends JpaEventStorageEngine.Builder {

        @Override
        public JpaEventStorageEngine.Builder snapshotSerializer(Serializer snapshotSerializer) {
            super.snapshotSerializer(snapshotSerializer);
            return this;
        }

        @Override
        public JpaEventStorageEngine.Builder upcasterChain(EventUpcaster upcasterChain) {
            super.upcasterChain(upcasterChain);
            return this;
        }

        @Override
        public JpaEventStorageEngine.Builder persistenceExceptionResolver(PersistenceExceptionResolver persistenceExceptionResolver) {
            super.persistenceExceptionResolver(persistenceExceptionResolver);
            return this;
        }

        @Override
        public JpaEventStorageEngine.Builder eventSerializer(Serializer eventSerializer) {
            super.eventSerializer(eventSerializer);
            return this;
        }

        @Override
        public JpaEventStorageEngine.Builder snapshotFilter(Predicate<? super DomainEventData<?>> snapshotFilter) {
            super.snapshotFilter(snapshotFilter);
            return this;
        }

        @Override
        public JpaEventStorageEngine.Builder batchSize(int batchSize) {
            super.batchSize(batchSize);
            return this;
        }

        /**
         * Sets the {@link PersistenceExceptionResolver} as a {@link SQLErrorCodesResolver}, using the provided
         * {@link DataSource} to resolve the error codes. <b>Note</b> that the provided DataSource sole purpose in this
         * {@link org.axonframework.eventsourcing.eventstore.EventStorageEngine} implementation is to be used for
         * instantiating the PersistenceExceptionResolver.
         *
         * @param dataSource the {@link DataSource} used to instantiate a
         *                   {@link SQLErrorCodesResolver#SQLErrorCodesResolver(DataSource)} as the
         *                   {@link PersistenceExceptionResolver}
         *
         * @return the current Builder instance, for fluent interfacing
         * @throws SQLException if creation of the {@link SQLErrorCodesResolver} fails
         */
        public JpaEventStorageEngine.Builder dataSource(DataSource dataSource) throws SQLException {
            super.dataSource(dataSource);
            return this;
        }

        /**
         * Sets the {@link EntityManagerProvider} which provides the {@link EntityManager} used to access the
         * underlying database for this {@link org.axonframework.eventsourcing.eventstore.EventStorageEngine}
         * implementation.
         *
         * @param entityManagerProvider a {@link EntityManagerProvider} which provides the {@link EntityManager} used to
         *                              access the underlying database
         *
         * @return the current Builder instance, for fluent interfacing
         */
        public JpaEventStorageEngine.Builder entityManagerProvider(EntityManagerProvider entityManagerProvider) {
            super.entityManagerProvider(entityManagerProvider);
            return this;
        }

        /**
         * Sets the {@link TransactionManager} used to manage transaction around fetching event data. Required by
         * certain databases for reading blob data.
         *
         * @param transactionManager a {@link TransactionManager} used to manage transaction around fetching event data
         *
         * @return the current Builder instance, for fluent interfacing
         */
        public JpaEventStorageEngine.Builder transactionManager(TransactionManager transactionManager) {
            super.transactionManager(transactionManager);
            return this;
        }

        /**
         * Sets the {@code explicitFlush} field specifying whether to explicitly call {@link EntityManager#flush()}
         * after inserting the Events published in this Unit of Work. If {@code false}, this instance relies
         * on the TransactionManager to flush data. Note that the {@code persistenceExceptionResolver} may not be able
         * to translate exceptions anymore. {@code false} should only be used to optimize performance for batch
         * operations. In other cases, {@code true} is recommended, which is also the default.
         *
         * @param explicitFlush a {@code boolean} specifying whether to explicitly call {@link EntityManager#flush()}
         *                      after inserting the Events published in this Unit of Work
         *
         * @return the current Builder instance, for fluent interfacing
         */
        public JpaEventStorageEngine.Builder explicitFlush(boolean explicitFlush) {
            super.explicitFlush(explicitFlush);
            return this;
        }

        /**
         * Sets the {@code maxGapOffset} specifying the maximum distance in sequence numbers between a missing event and
         * the event with the highest known index. If the gap is bigger it is assumed that the missing event will not be
         * committed to the store anymore. This event storage engine will no longer look for those events the next time
         * a batch is fetched. Defaults to an integer of {@code 10000}
         * ({@link JpaEventStorageEngine#DEFAULT_MAX_GAP_OFFSET}.
         *
         * @param maxGapOffset an {@code int} specifying the maximum distance in sequence numbers between a missing
         *                     event and the event with the highest known index
         *
         * @return the current Builder instance, for fluent interfacing
         */
        public JpaEventStorageEngine.Builder maxGapOffset(int maxGapOffset) {
            super.maxGapOffset(maxGapOffset);
            return this;
        }

        /**
         * Sets the {@code lowestGlobalSequence} specifying the first expected auto generated sequence number. For most
         * data stores this is 1 unless the table has contained entries before. Defaults to a {@code long} of {@code 1}
         * ({@link JpaEventStorageEngine#DEFAULT_LOWEST_GLOBAL_SEQUENCE}).
         *
         * @param lowestGlobalSequence a {@code long} specifying the first expected auto generated sequence number
         *
         * @return the current Builder instance, for fluent interfacing
         */
        public JpaEventStorageEngine.Builder lowestGlobalSequence(long lowestGlobalSequence) {
            super.lowestGlobalSequence(lowestGlobalSequence);
            return this;
        }

        /**
         * Sets the amount of time until a 'gap' in a TrackingToken may be considered timed out. This setting will
         * affect the cleaning process of gaps. Gaps that have timed out will be removed from Tracking Tokens to improve
         * performance of reading events. Defaults to an  integer of {@code 60000}
         * ({@link JpaEventStorageEngine#DEFAULT_GAP_TIMEOUT}), thus 1 minute.
         *
         * @param gapTimeout an {@code int} specifying the amount of time in milliseconds until a 'gap' in a
         *                   TrackingToken may be considered timed out
         *
         * @return the current Builder instance, for fluent interfacing
         */
        public JpaEventStorageEngine.Builder gapTimeout(int gapTimeout) {
            super.gapTimeout(gapTimeout);
            return this;
        }

        /**
         * Sets the threshold of number of gaps in a token before an attempt to clean gaps up is taken. Defaults to an
         * integer of {@code 250} ({@link JpaEventStorageEngine#DEFAULT_GAP_CLEANING_THRESHOLD}).
         *
         * @param gapCleaningThreshold an {@code int} specifying the threshold of number of gaps in a token before an
         *                             attempt to clean gaps up is taken
         *
         * @return the current Builder instance, for fluent interfacing
         */
        public JpaEventStorageEngine.Builder gapCleaningThreshold(int gapCleaningThreshold) {
            super.gapCleaningThreshold(gapCleaningThreshold);
            return this;
        }

        /**
         * Initializes a {@link JpaEventStorageEngine} as specified through this Builder.
         *
         * @return a {@link JpaEventStorageEngine} as specified through this Builder
         */
        public CustomJpaEventStorageEngine build() {
            return new CustomJpaEventStorageEngine(this);
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
