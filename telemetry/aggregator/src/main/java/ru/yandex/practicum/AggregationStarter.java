package ru.yandex.practicum;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.configuration.Kafka;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class AggregationStarter {

    private final KafkaProducer<String, SensorsSnapshotAvro> producer;
    private final KafkaConsumer<String, SensorEventAvro> consumer;
    private final Map<String, SensorsSnapshotAvro> snapshots = new HashMap<>();
    private final EnumMap<Kafka.TopicType, String> topics;
    private final Kafka kafkaConfig;


    @Autowired
    public AggregationStarter(EnumMap<Kafka.TopicType, String> topics, Kafka kafkaConfig) {
        this.topics = topics;
        this.kafkaConfig = kafkaConfig;
        this.producer = new KafkaProducer<>(getPropertiesProducerSensor());
        this.consumer = new KafkaConsumer<>(getPropertiesConsumerSensor());
    }


    public void start() {
        final String telemetrySensors = topics.get(Kafka.TopicType.TELEMETRY_SENSORS);
        final String telemetrySnapshots = topics.get(Kafka.TopicType.TELEMETRY_SNAPSHOTS);

        try {
            consumer.subscribe(Collections.singletonList(telemetrySensors));
            log.info("подписка на топик : {}", telemetrySensors);

            while (true) {
                ConsumerRecords<String, SensorEventAvro> records = consumer.poll(Duration.ofMillis(100));

                if (records.isEmpty()) {
                    continue;
                }

                for (ConsumerRecord<String, SensorEventAvro> record : records) {
                    SensorEventAvro event = record.value();

                    updateState(event).ifPresent(snapshot -> {
                        try {

                            producer.send(new ProducerRecord<>(telemetrySnapshots, snapshot.getHubId(), snapshot), (metadata, exception) -> {
                            });
                            log.info("Snapshot hubId {} -> топик {}", snapshot.getHubId(), telemetrySnapshots);
                        } catch (Exception e) {
                            log.error("Ошибка при отправке snapshot в топик", e);
                        }
                    });
                }

                try {
                    consumer.commitSync();
                } catch (Exception e) {
                    log.error("ошибка в commitSync", e);
                }
            }
        } catch (Exception e) {
            log.error("ошибка ", e);
        } finally {
            try {
                producer.flush();
                producer.close();
                consumer.close();
            } catch (Exception e) {
                log.error("ошибка при закрытии ", e);
            }
        }
    }



    public Optional<SensorsSnapshotAvro> updateState(SensorEventAvro event) {
        SensorsSnapshotAvro snapshot = snapshots.getOrDefault(event.getHubId(),
                SensorsSnapshotAvro.newBuilder()
                        .setHubId(event.getHubId())
                        .setTimestamp(Instant.ofEpochSecond(System.currentTimeMillis()))
                        .setSensorsState(new HashMap<>())
                        .build());

        SensorStateAvro oldState = snapshot.getSensorsState().get(event.getId());
        if (oldState != null
            && !oldState.getTimestamp().isBefore(Instant.ofEpochSecond(event.getTimestamp()))
            && oldState.getData().equals(event.getPayload())) {
            return Optional.empty();
        }

        SensorStateAvro newState = SensorStateAvro.newBuilder()
                .setTimestamp(Instant.ofEpochSecond(event.getTimestamp()))
                .setData(event.getPayload())
                .build();

        snapshot.getSensorsState().put(event.getId(), newState);

        snapshot.setTimestamp(Instant.ofEpochSecond(event.getTimestamp()));

        snapshots.put(event.getHubId(), snapshot);

        return Optional.of(snapshot);
    }

    private Properties getPropertiesConsumerSensor() {
        Properties config = new Properties();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfig.getConsumer().getBootstrapServers());
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, kafkaConfig.getConsumer().getKeyDeserializer());
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, kafkaConfig.getConsumer().getValueDeserializer());
        config.put(ConsumerConfig.CLIENT_ID_CONFIG, kafkaConfig.getConsumer().getClientId());
        config.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaConfig.getConsumer().getGroupId());
        return config;
    }

    private Properties getPropertiesProducerSensor() {
        Properties config = new Properties();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfig.getProducer().getBootstrapServers());
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, kafkaConfig.getProducer().getKeySerializer());
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, kafkaConfig.getProducer().getValueSerializer());
        return config;
    }



}