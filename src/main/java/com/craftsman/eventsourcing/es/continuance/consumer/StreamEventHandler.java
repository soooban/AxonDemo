package com.craftsman.eventsourcing.es.continuance.consumer;

import java.lang.annotation.*;

@Target( {ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface StreamEventHandler {

    String[] payloadTypes() default {""};

    String[] types();
}
