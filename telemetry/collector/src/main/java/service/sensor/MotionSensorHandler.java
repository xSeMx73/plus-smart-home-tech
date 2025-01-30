package service.sensor;

import kafkaConfig.Config;
import kafkaConfig.Producer;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.MotionSensorAvro;
import service.handler.BaseSensorHandler;

@Service
public class MotionSensorHandler extends BaseSensorHandler<MotionSensorAvro> {
    public MotionSensorHandler(Config config, Producer producer) {
        super(config, producer);
    }

    @Override
    protected MotionSensorAvro mapToAvro(SensorEventProto event) {
        var motionEvent = event.getMotionSensorEvent();

        return new MotionSensorAvro(
                motionEvent.getLinkQuality(),
                motionEvent.getMotion(),
                motionEvent.getVoltage()
        );
    }

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.MOTION_SENSOR_EVENT;
    }
}
