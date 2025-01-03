package service;

import hub.HubEvent;
import hub.HubEventType;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sensor.SensorEvent;
import sensor.SensorEventType;
import service.handler.HubHandler;
import service.handler.SensorHandler;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/events")
public class EventController {

    private final Map<SensorEventType, SensorHandler> sensorEventHandlers;
    private final Map<HubEventType, HubHandler> hubEventHandlers;

    public EventController(Set<SensorHandler> sensorEventHandlers, Set<HubHandler> hubEventHandlers) {
        this.sensorEventHandlers = sensorEventHandlers.stream()
                .collect(Collectors.toMap(SensorHandler::getMessageType, Function.identity()));
        this.hubEventHandlers = hubEventHandlers.stream()
                .collect(Collectors.toMap(HubHandler::getMessageType, Function.identity()));
    }

    @PostMapping("/sensors")
    public void collectSensorEvent(@Valid @RequestBody SensorEvent event) {
        SensorHandler handler = sensorEventHandlers.get(event.getType());
        if (handler == null) {
            throw new RuntimeException(event.getType() + " не существует");
        }
        handler.handle(event);
    }

    @PostMapping("/hubs")
    public void collectHubEvent(@Valid @RequestBody HubEvent event) {
        HubHandler handler = hubEventHandlers.get(event.getType());
        if (handler == null) {
            throw new RuntimeException(event.getType() + " не существует");
        }
        handler.handle(event);
    }
}
