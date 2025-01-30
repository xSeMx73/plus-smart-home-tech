package service.handler;

import kafkaConfig.Config;
import kafkaConfig.Producer;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;

@Slf4j
@AllArgsConstructor
public abstract class BaseHubHandler<T extends SpecificRecordBase> implements HubHandler {

    private final Config config;
    private final Producer producer;
    private static final String HUB_TOPIC = "telemetry.hubs.v1";

    protected abstract T mapToAvro(HubEventProto event);

    @Override
    public void handle(HubEventProto event) {
        T protoEvent = mapToAvro(event);
        log.info("Отправка события {} в топик {}", getMessageType(), HUB_TOPIC);
        producer.send(HUB_TOPIC, event.getHubId(), protoEvent);
    }
}
