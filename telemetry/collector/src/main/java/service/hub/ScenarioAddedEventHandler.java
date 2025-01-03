package service.hub;

import kafkaConfig.Config;
import kafkaConfig.Producer;
import hub.*;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;
import service.handler.BaseHubHandler;

@Service
public class ScenarioAddedEventHandler extends BaseHubHandler<ScenarioAddedEventAvro> {
    public ScenarioAddedEventHandler(Config config, Producer producer) {
        super(config, producer);
    }

    @Override
    protected ScenarioAddedEventAvro mapToAvro(HubEvent event) {
        var scenarioEvent = (ScenarioAddedEvent) event;

        return new ScenarioAddedEventAvro(
                scenarioEvent.getName(),
                scenarioEvent.getConditions().stream().map(this::mapConditionToAvro).toList(),
                scenarioEvent.getActions().stream().map(this::mapActionToAvro).toList()
        );
    }

    @Override
    public HubEventType getMessageType() {
        return HubEventType.SCENARIO_ADDED;
    }

    private ScenarioConditionAvro mapConditionToAvro(ScenarioCondition condition) {
        return new ScenarioConditionAvro(
                condition.getSensorId(),
                ru.yandex.practicum.kafka.telemetry.event.ConditionTypeAvro.valueOf(condition.getType().name()),
                ru.yandex.practicum.kafka.telemetry.event.ConditionOperationAvro.valueOf(condition.getOperation().name()),
                condition.getValue()
        );
    }

    private DeviceActionAvro mapActionToAvro(DeviceAction action) {
        return new DeviceActionAvro(
                action.getSensorId(),
                ru.yandex.practicum.kafka.telemetry.event.ActionTypeAvro.valueOf(action.getType().name()),
                action.getValue()
        );
    }
}
