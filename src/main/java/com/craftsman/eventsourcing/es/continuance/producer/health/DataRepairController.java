package com.craftsman.eventsourcing.es.continuance.producer.health;

import com.craftsman.eventsourcing.service.ContractViewService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/repair")
public class DataRepairController {

    private static final String SECRET = "e248b98418db4cdcb069e8a1c08f6bb7";
    private final ScheduleService scheduleService;
    private final ContractViewService contractViewService;

    @GetMapping("/message")
    @Async
    public void repairMessage(@RequestParam("secret") String secret) {
        if (!StringUtils.equals(secret, SECRET)) {
            return;
        }

        scheduleService.failedMessageDiscovery();
    }

    @PostMapping("/aggregate")
    @Async
    public void repairAggregate(@RequestParam("secret") String secret, Long aggregateIdentifier) {
        if (!StringUtils.equals(secret, SECRET)) {
            return;
        }
        contractViewService.updateViewFromAggregateById(aggregateIdentifier, Instant.now());
    }
}
