package com.craftsman.eventsourcing.es.continuance.consumer;

import com.craftsman.eventsourcing.es.continuance.common.DomainEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@AllArgsConstructor
public class StreamDomainEventDispatcher implements BeanPostProcessor {

    private final ObjectMapper mapper;

    private final DomainAggregateSequenceRepository domainAggregateSequenceRepository;

    private HashMap<Object, List<Method>> beanHandlerMap = new HashMap<>();

    @Autowired
    public StreamDomainEventDispatcher(ObjectMapper mapper, DomainAggregateSequenceRepository domainAggregateSequenceRepository) {
        this.mapper = mapper;
        this.domainAggregateSequenceRepository = domainAggregateSequenceRepository;
    }

    @Transactional
    public void dispatchEvent(DomainEvent event, String type) {

        log.info(MessageFormat.format("message [{0}] received", event.getEventIdentifier()));

        // 1. 检查是否是乱序事件或者重复事件
        Long aggregateIdentifier = Long.parseLong(event.getAggregateIdentifier());
        String eventType = event.getType();
        Long eventSequence = event.getSequenceNumber();

        DomainAggregateSequence sequenceObject = domainAggregateSequenceRepository.findByAggregateIdentifierAndType(aggregateIdentifier, eventType);

        if (sequenceObject == null) {
            sequenceObject = new DomainAggregateSequence();
            sequenceObject.setSequenceNumber(eventSequence);
            sequenceObject.setAggregateIdentifier(aggregateIdentifier);
            sequenceObject.setType(eventType);
        } else if (sequenceObject.getSequenceNumber() + 1 != eventSequence) {
            // 重复事件，直接忽略
            if (sequenceObject.getSequenceNumber().equals(eventSequence)) {
                log.warn(MessageFormat.format("repeat event ignored, type[{0}] aggregate[{1}] seq[{2}] , ignored",
                    event.getType(),
                    event.getAggregateIdentifier(),
                    event.getSequenceNumber()));
                return;
            }
            throw new StreamEventSequenceException(MessageFormat.format("sequence error, db [{0}], current [{1}]",
                sequenceObject.getSequenceNumber(),
                eventSequence));
        } else {
            sequenceObject.setSequenceNumber(eventSequence);
        }

        domainAggregateSequenceRepository.save(sequenceObject);

        // 2. 分发事件到各个 handler
        beanHandlerMap.forEach((key, value) -> {
            Optional<Method> matchedMethod = getMatchedMethods(value, type, event.getPayloadType());

            matchedMethod.ifPresent(method -> {
                try {
                    invoke(key, method, event);
                } catch (IllegalAccessException | InvocationTargetException e) {

                    throw new StreamHandlerException(MessageFormat.format("[{0}] invoke error", method.getName()), e);
                }
            });

            if (!matchedMethod.isPresent()) {
                log.info(MessageFormat.format("message [{0}] has no listener", event.getEventIdentifier()));
            }
        });

        log.info(MessageFormat.format("message [{0}] handled", event.getEventIdentifier()));
    }

    @Transactional
    public Optional<Method> getMatchedMethods(List<Method> methods, String type, String payloadType) {
        // 这里应该只有一个方法，因为将 stream 的单个事件分成多个之后，无法保证一致性
        List<Method> results = methods.stream().filter(m -> {
            StreamEventHandler handler = m.getAnnotation(StreamEventHandler.class);
            List<String> types = new ArrayList<>(Arrays.asList(handler.types()));
            List<String> payloadTypes = new ArrayList<>(Arrays.asList(handler.payloadTypes()));

            types.removeIf(StringUtils::isBlank);
            payloadTypes.removeIf(StringUtils::isBlank);

            if (CollectionUtils.isEmpty(payloadTypes) && m.getParameterTypes().length != 0) {
                payloadTypes = Collections.singletonList(m.getParameterTypes()[0].getSimpleName());
            }

            boolean isTypeMatch = types.contains(type);

            String checkedPayloadType = payloadType;
            if (StringUtils.contains(checkedPayloadType, ".")) {
                checkedPayloadType = StringUtils.substringAfterLast(checkedPayloadType, ".");
            }
            boolean isPayloadTypeMatch = payloadTypes.contains(checkedPayloadType);

            return isTypeMatch && isPayloadTypeMatch;
        }).collect(Collectors.toList());

        if (results.size() > 1) {
            throw new StreamHandlerException(MessageFormat.format("type[{0}] event[{1}] has more than one handler", type, payloadType));
        }

        return results.size() == 1 ? Optional.of(results.get(0)) : Optional.empty();
    }

    @Transactional
    public void invoke(Object bean, Method method, DomainEvent event) throws IllegalAccessException, InvocationTargetException {

        int count = method.getParameterCount();

        if (count == 0) {
            method.invoke(bean);
        } else if (count == 1) {
            Class<?> payloadType = method.getParameterTypes()[0];

            if (payloadType.equals(DomainEvent.class)) {
                method.invoke(bean, mapper.convertValue(event.getPayload(), DomainEvent.class));
            } else {
                method.invoke(bean, mapper.convertValue(event.getPayload(), payloadType));
            }

        } else if (count == 2) {
            Class<?> payloadType0 = method.getParameterTypes()[0];
            Class<?> payloadType1 = method.getParameterTypes()[1];

            Object firstParameterValue = mapper.convertValue(event.getPayload(), payloadType0);
            Object secondParameterValue = event.getMetaData();

            // 如果是 DomainEvent 类型则优先传递该类型，另外一个参数按照 payloadType > metaData 优先级传入
            if (payloadType0.equals(DomainEvent.class)) {
                firstParameterValue = mapper.convertValue(event, payloadType0);
                secondParameterValue = mapper.convertValue(event.getPayload(), payloadType1);
            }
            if (payloadType1.equals(DomainEvent.class)) {
                secondParameterValue = mapper.convertValue(event, payloadType1);
            }
            method.invoke(bean, firstParameterValue, secondParameterValue);
        }
    }


    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> targetClass = AopUtils.isAopProxy(bean) ? AopUtils.getTargetClass(bean) : bean.getClass();
        Method[] uniqueDeclaredMethods = ReflectionUtils.getUniqueDeclaredMethods(targetClass);

        List<Method> methods = new ArrayList<>();
        for (Method method : uniqueDeclaredMethods) {
            StreamEventHandler streamListener = AnnotatedElementUtils.findMergedAnnotation(method,
                StreamEventHandler.class);
            if (streamListener != null) {
                methods.add(method);
            }
        }
        if (!CollectionUtils.isEmpty(methods)) {
            beanHandlerMap.put(bean, methods);
        }
        return bean;
    }

}

