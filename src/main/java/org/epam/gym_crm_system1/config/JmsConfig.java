package org.epam.gym_crm_system1.config;

import org.epam.gym_crm_system1.dto.request.TrainerWorkloadRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.support.converter.JacksonJsonMessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

import java.util.Map;

@Configuration
public class JmsConfig {

    private static final String TYPE_ID_PROPERTY = "_type";
    private static final String TRAINER_WORKLOAD_TYPE =
            "trainerWorkloadRequest";

    @Bean
    public MessageConverter jmsMessageConverter() {

        JacksonJsonMessageConverter converter =
                new JacksonJsonMessageConverter();

        converter.setTargetType(MessageType.TEXT);

        converter.setTypeIdPropertyName(TYPE_ID_PROPERTY);

        converter.setTypeIdMappings(
                Map.of(
                        TRAINER_WORKLOAD_TYPE,
                        TrainerWorkloadRequest.class
                )
        );

        return converter;
    }
}