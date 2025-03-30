package com.app.event;

import com.app.event.payload.FactAddedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class FactAddedEventProducer {
    private final KafkaTemplate<String, FactAddedEvent> kafkaTemplate;

    public FactAddedEventProducer(KafkaTemplate<String, FactAddedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendEvent(FactAddedEvent factAddedEvent) {
        this.kafkaTemplate.send("fact-added-event.v1", factAddedEvent);
        log.info("FactAddedEvent sent to fact-added-event.v1 from user [%s] with content [%s].".formatted(factAddedEvent.getAddedBy(), factAddedEvent.getContent()));
    }
}
