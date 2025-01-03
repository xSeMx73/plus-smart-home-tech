package service.sensor;

import kafkaConfig.Config;
import kafkaConfig.Producer;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.MotionSensorAvro;
import sensor.MotionSensorEvent;
import sensor.SensorEvent;
import sensor.SensorEventType;
import service.handler.BaseEventHandler;

@Service
public class MotionSensorEventHandler extends BaseEventHandler<MotionSensorAvro> {
    public MotionSensorEventHandler(Config config, Producer producer) {
        super(config, producer);
    }

    @Override
    protected MotionSensorAvro mapToAvro(SensorEvent event) {
        var motionEvent = (MotionSensorEvent) event;

        return new MotionSensorAvro(
                motionEvent.getLinkQuality(),
                motionEvent.isMotion(),
                motionEvent.getVoltage()
        );
    }

    @Override
    public SensorEventType getMessageType() {
        return SensorEventType.MOTION_SENSOR_EVENT;
    }
}
