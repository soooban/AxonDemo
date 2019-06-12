package com.craftsman.eventsourcing.es.continuance.producer.config;

import com.craftsman.eventsourcing.es.command.AbstractCommand;
import com.craftsman.eventsourcing.es.command.CreateContractCommand;
import com.craftsman.eventsourcing.es.continuance.common.MetaDataUser;
import com.craftsman.eventsourcing.es.continuance.common.MetaDataUserInterface;
import com.craftsman.eventsourcing.helper.UIDGenerator;
import lombok.AllArgsConstructor;
import org.axonframework.commandhandling.GenericCommandMessage;
import org.axonframework.messaging.MessageDispatchInterceptor;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

@AllArgsConstructor
@Configuration
public class CommandInterceptor implements MessageDispatchInterceptor {

    private final UIDGenerator uidGenerator;

    @Override
    public BiFunction<Integer, GenericCommandMessage<AbstractCommand>, GenericCommandMessage<AbstractCommand>> handle(List messages) {
        return (index, message) -> {

            // create command 自动生成 ID
            if (message.getPayload() instanceof CreateContractCommand) {
                CreateContractCommand payload = (CreateContractCommand) message.getPayload();
                payload.setIdentifier(uidGenerator.getId());
            }

            // 添加 user info 作为 MetaData，由于项目不设计 security 这里就简单的附加一个假的用户
            Map<String, MetaDataUserInterface> map = new HashMap<>();

            map.put("user", MetaDataUser.builder().customerId(1L).name("Test").userId(2L).build());
            return map.isEmpty() ? message : message.andMetaData(map);
        };
    }
}
