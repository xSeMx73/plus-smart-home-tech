package service.sensor;

import kafkaConfig.Config;
import kafkaConfig.Producer;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.SwitchSensorAvro;
import sensor.SensorEvent;
import sensor.SensorEventType;
import sensor.SwitchSensorEvent;
import service.handler.BaseEventHandler;

@Service
public class SwitchSensorEventHandler extends BaseEventHandler<SwitchSensorAvro> {
    public SwitchSensorEventHandler(Config config, Producer producer) {
        super(config, producer);
    }

    @Override
    protected SwitchSensorAvro mapToAvro(SensorEvent event) {
        var switchEvent = (SwitchSensorEvent) event;

        return new SwitchSensorAvro(switchEvent.isState());
    }

    @Override
    public SensorEventType getMessageType() {
        return SensorEventType.SWITCH_SENSOR_EVENT;
    }

}
