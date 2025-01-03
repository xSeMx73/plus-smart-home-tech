package kafkaConfig;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class Producer {

    private final KafkaProducer<String, SpecificRecordBase> kafkaProducer;

    public <T extends SpecificRecordBase> void send(String topic, String key, T event) {

        ProducerRecord<String, SpecificRecordBase> record =
                new ProducerRecord<>(topic, key, event);
        log.info("Отправка события в топик: {} с ключом: {} Событие: {}",
                topic, key, event);
        kafkaProducer.send(record);
    }
}