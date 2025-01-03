package service.sensor;

import kafkaConfig.Config;
import kafkaConfig.Producer;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.ClimateSensorAvro;
import sensor.ClimateSensorEvent;
import sensor.SensorEvent;
import sensor.SensorEventType;
import service.handler.BaseEventHandler;

@Service
public class ClimateSensorEventHandler extends BaseEventHandler<ClimateSensorAvro> {

    public ClimateSensorEventHandler(Config config, Producer producer) {
        super(config, producer);
    }

    @Override
    protected ClimateSensorAvro mapToAvro(SensorEvent event) {
        var climateEvent = (ClimateSensorEvent) event;
        return new ClimateSensorAvro(
                climateEvent.getTemperatureC(),
                climateEvent.getHumidity(),
                climateEvent.getCo2Level()
        );
    }

    @Override
    public SensorEventType getMessageType() {
        return SensorEventType.CLIMATE_SENSOR_EVENT;
    }
}

