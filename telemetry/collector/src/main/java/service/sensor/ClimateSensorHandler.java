package service.sensor;

import kafkaConfig.Config;
import kafkaConfig.Producer;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.ClimateSensorAvro;
import service.handler.BaseSensorHandler;

@Service
public class ClimateSensorHandler extends BaseSensorHandler<ClimateSensorAvro> {

    public ClimateSensorHandler(Config config, Producer producer) {
        super(config, producer);
    }

    @Override
    protected ClimateSensorAvro mapToAvro(SensorEventProto event) {
        var climateEvent = event.getClimateSensorEvent();
        return new ClimateSensorAvro(
                climateEvent.getTemperatureC(),
                climateEvent.getHumidity(),
                climateEvent.getCo2Level()
        );
    }

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.CLIMATE_SENSOR_EVENT;
    }
}

