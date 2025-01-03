package service.hub;

import kafkaConfig.Config;
import kafkaConfig.Producer;
import hub.DeviceRemovedEvent;
import hub.HubEvent;
import hub.HubEventType;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;
import service.handler.BaseHubHandler;

@Service
public class DeviceRemovedEventHandler extends BaseHubHandler<DeviceRemovedEventAvro> {
    public DeviceRemovedEventHandler(Config config, Producer producer) {
        super(config, producer);
    }

    @Override
    protected DeviceRemovedEventAvro mapToAvro(HubEvent event) {
        var deviceRemovedEvent = (DeviceRemovedEvent) event;
        return new DeviceRemovedEventAvro(deviceRemovedEvent.getId());
    }

    @Override
    public HubEventType getMessageType() {
        return HubEventType.DEVICE_REMOVED;
    }

}
