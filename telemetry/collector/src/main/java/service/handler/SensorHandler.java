package service.handler;

import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;


public interface SensorHandler {
    SensorEventProto.PayloadCase getMessageType();
    void handle(SensorEventProto event);
}