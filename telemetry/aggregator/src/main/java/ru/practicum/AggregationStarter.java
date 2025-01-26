package ru.practicum;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import ru.practicum.configuration.Kafka;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

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
            && !oldState.getTimestamp().isBefore(event.getTimestamp())
            && oldState.getData().equals(event.getPayload())) {
            return Optional.empty();
        }

        SensorStateAvro newState = SensorStateAvro.newBuilder()
                .setTimestamp(event.getTimestamp())
                .setData(event.getPayload())
                .build();

        snapshot.getSensorsState().put(event.getId(), newState);

        snapshot.setTimestamp(event.getTimestamp());

        snapshots.put(event.getHubId(), snapshot);

        return Optional.of(snapshot);
    }


}