package com.craftsman.eventsourcing;

import com.craftsman.eventsourcing.stream.channel.InputChannel;
import com.craftsman.eventsourcing.stream.channel.OutputChannel;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;

@SpringBootApplication
@EnableBinding( {InputChannel.class, OutputChannel.class})
public class EventSourcingApplication {

	public static void main(String[] args) {
		SpringApplication.run(EventSourcingApplication.class, args);
	}

}
