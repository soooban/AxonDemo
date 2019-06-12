package com.craftsman.eventsourcing.es.continuance.producer.config;

import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.commandhandling.gateway.RetryScheduler;

import java.text.MessageFormat;
import java.util.List;

@Slf4j
public class CommandRetryScheduler implements RetryScheduler {

    @Override
    public boolean scheduleRetry(CommandMessage commandMessage,
                                 RuntimeException lastFailure,
                                 List<Class<? extends Throwable>[]> failures,
                                 Runnable commandDispatch) {
        log.info(MessageFormat.format("aggregate [{0}] execute [{1}] retry [{2}] time",
            commandMessage.getIdentifier(),
            commandMessage.getCommandName(),
            failures.size()));

        if (failures.size() > 2) {
            return false;
        }

        commandDispatch.run();

        return true;
    }
}
