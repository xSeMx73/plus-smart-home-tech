package service.sensor;

import kafkaConfig.Config;
import kafkaConfig.Producer;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.SwitchSensorAvro;
import service.handler.BaseSensorHandler;

@Service
public class SwitchSensorHandler extends BaseSensorHandler<SwitchSensorAvro> {
    public SwitchSensorHandler(Config config, Producer producer) {
        super(config, producer);
    }

    @Override
    protected SwitchSensorAvro mapToAvro(SensorEventProto event) {
        var switchEvent = event.getSwitchSensorEvent();
        return new SwitchSensorAvro(switchEvent.getState());
    }

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.SWITCH_SENSOR_EVENT;
    }

}
