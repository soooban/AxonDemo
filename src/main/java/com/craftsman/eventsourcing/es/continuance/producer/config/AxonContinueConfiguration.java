package com.craftsman.eventsourcing.es.continuance.producer.config;


import com.craftsman.eventsourcing.es.ContractAggregate;
import com.craftsman.eventsourcing.es.continuance.producer.jpa.CustomEmbeddedEventStore;
import com.craftsman.eventsourcing.es.continuance.producer.jpa.CustomEventSourcingRepository;
import org.axonframework.common.jdbc.PersistenceExceptionResolver;
import org.axonframework.common.jpa.EntityManagerProvider;
import org.axonframework.common.transaction.TransactionManager;
import org.axonframework.config.EventProcessingConfigurer;
import org.axonframework.eventsourcing.eventstore.EventStorageEngine;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.axonframework.eventsourcing.eventstore.jpa.JpaEventStorageEngine;
import org.axonframework.messaging.annotation.ParameterResolverFactory;
import org.axonframework.serialization.Serializer;
import org.axonframework.spring.config.AxonConfiguration;
import org.axonframework.springboot.util.RegisterDefaultEntities;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RegisterDefaultEntities(packages = {
    "org.axonframework.eventsourcing.eventstore.jpa"
})
public class AxonContinueConfiguration {

    @Bean
    public CustomEmbeddedEventStore eventStore(EventStorageEngine storageEngine, AxonConfiguration configuration) {
        return CustomEmbeddedEventStore.builder()
            .storageEngine(storageEngine)
            .messageMonitor(configuration.messageMonitor(EventStore.class, "eventStore"))
            .build();
    }

    @Bean
    public CustomEventSourcingRepository<ContractAggregate> contractAggregateRepository(CustomEmbeddedEventStore eventStore,
                                                                                        ParameterResolverFactory parameterResolverFactory) {
        return CustomEventSourcingRepository.builder(ContractAggregate.class)
            .eventStore(eventStore)
            .parameterResolverFactory(parameterResolverFactory)
            .build();
    }

    @Bean
    public EventStorageEngine eventStorageEngine(Serializer defaultSerializer,
                                                 PersistenceExceptionResolver persistenceExceptionResolver,
                                                 @Qualifier("eventSerializer") Serializer eventSerializer,
                                                 AxonConfiguration configuration,
                                                 EntityManagerProvider entityManagerProvider,
                                                 TransactionManager transactionManager) {
        return JpaEventStorageEngine.builder()
            .snapshotSerializer(defaultSerializer)
            .upcasterChain(configuration.upcasterChain())
            .persistenceExceptionResolver(persistenceExceptionResolver)
            .eventSerializer(eventSerializer)
            .entityManagerProvider(entityManagerProvider)
            .transactionManager(transactionManager)
            .build();
    }

    @Bean
    public EventProcessingConfigurer eventProcessingConfigurer(EventProcessingConfigurer eventProcessingConfigurer) {
        eventProcessingConfigurer.usingSubscribingEventProcessors();
        return eventProcessingConfigurer;
    }
}
