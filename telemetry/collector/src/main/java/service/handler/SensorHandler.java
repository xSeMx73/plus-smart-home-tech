package service.handler;

import sensor.SensorEvent;
import sensor.SensorEventType;

public interface SensorHandler {
    SensorEventType getMessageType();

    void handle(SensorEvent event);
}
