package com.craftsman.eventsourcing.es.continuance.producer.config;


import com.craftsman.eventsourcing.es.ContractAggregate;
import com.craftsman.eventsourcing.es.ContractCommandGateway;
import com.craftsman.eventsourcing.es.continuance.producer.jpa.CustomEmbeddedEventStore;
import com.craftsman.eventsourcing.es.continuance.producer.jpa.CustomEventSourcingRepository;
import com.craftsman.eventsourcing.es.upcaster.ContractEventUpCaster;
import org.axonframework.commandhandling.SimpleCommandBus;
import org.axonframework.commandhandling.gateway.CommandGatewayFactory;
import org.axonframework.common.jdbc.PersistenceExceptionResolver;
import org.axonframework.common.jpa.EntityManagerProvider;
import org.axonframework.common.transaction.TransactionManager;
import org.axonframework.config.EventProcessingConfigurer;
import org.axonframework.eventsourcing.*;
import org.axonframework.eventsourcing.eventstore.EventStorageEngine;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.axonframework.eventsourcing.eventstore.jpa.JpaEventStorageEngine;
import org.axonframework.messaging.annotation.ParameterResolverFactory;
import org.axonframework.serialization.Serializer;
import org.axonframework.serialization.upcasting.event.EventUpcaster;
import org.axonframework.spring.config.AxonConfiguration;
import org.axonframework.springboot.util.RegisterDefaultEntities;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

/**
 * 自定义的一些配置，由于 EventStorageEngine 的 Auto Config 在自定义了 eventStore 之后就不起作用了，所以这里把 JpaEventStoreAutoConfiguration 中的内容搬过来了
 */
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
                                                                                        SnapshotTriggerDefinition snapshotTriggerDefinition,
                                                                                        ParameterResolverFactory parameterResolverFactory) {
        return CustomEventSourcingRepository.builder(ContractAggregate.class)
            .eventStore(eventStore)
            .snapshotTriggerDefinition(snapshotTriggerDefinition)
            .parameterResolverFactory(parameterResolverFactory)
            .build();
    }

    @Bean
    public SnapshotTriggerDefinition snapshotTriggerDefinition(Snapshotter snapshotter) {
        return new EventCountSnapshotTriggerDefinition(snapshotter, 5);
    }

    @Bean
    public AggregateSnapshotter snapShotter(CustomEmbeddedEventStore eventStore, ParameterResolverFactory parameterResolverFactory) {
        return AggregateSnapshotter.builder()
            .eventStore(eventStore)
            .parameterResolverFactory(parameterResolverFactory)
            .aggregateFactories(Collections.singletonList(new GenericAggregateFactory<>(ContractAggregate.class)))
            .build();
    }

    @Bean
    public EventStorageEngine eventStorageEngine(Serializer defaultSerializer,
                                                 PersistenceExceptionResolver persistenceExceptionResolver,
                                                 @Qualifier("eventSerializer") Serializer eventSerializer,
                                                 EntityManagerProvider entityManagerProvider,
                                                 EventUpcaster contractUpCaster,
                                                 TransactionManager transactionManager) {
        return JpaEventStorageEngine.builder()
            .snapshotSerializer(defaultSerializer)
            .upcasterChain(contractUpCaster)
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

    @Bean
    public EventUpcaster contractUpCaster() {
        return new ContractEventUpCaster();
    }

    @Bean
    public ContractCommandGateway getCommandGateway(SimpleCommandBus simpleCommandBus, CommandInterceptor commandInterceptor) {
        return CommandGatewayFactory.builder()
            .commandBus(simpleCommandBus)
            .retryScheduler(new CommandRetryScheduler())
            .dispatchInterceptors(commandInterceptor)
            .build()
            .createGateway(ContractCommandGateway.class);
    }
}
