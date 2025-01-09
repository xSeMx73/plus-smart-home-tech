package service.hub;

import kafkaConfig.Config;
import kafkaConfig.Producer;
import hub.DeviceAddedEvent;
import hub.HubEvent;
import hub.HubEventType;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import org.springframework.stereotype.Service;

import ru.yandex.practicum.kafka.telemetry.event.DeviceTypeAvro;
import service.handler.BaseHubHandler;

@Service
public class DeviceAddedEventHandler extends BaseHubHandler<DeviceAddedEventAvro> {

    public DeviceAddedEventHandler(Config config, Producer producer) {
        super(config, producer);
    }


    @Override
    protected DeviceAddedEventAvro mapToAvro(HubEvent event) {
        var deviceAddedEvent = (DeviceAddedEvent) event;

        return new DeviceAddedEventAvro(
                deviceAddedEvent.getId(),
                DeviceTypeAvro.valueOf(deviceAddedEvent.getDeviceType().name())
        );
    }

    @Override
    public HubEventType getMessageType() {
        return HubEventType.DEVICE_ADDED;
    }
}
