package com.craftsman.eventsourcing.es.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class QueryContractCommand {
    @NotBlank
    @NotNull
    private Long id;

    private Instant endDate;
}
