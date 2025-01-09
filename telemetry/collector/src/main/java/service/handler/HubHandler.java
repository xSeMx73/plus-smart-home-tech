package service.handler;

import hub.HubEvent;
import hub.HubEventType;

public interface HubHandler {
    HubEventType getMessageType();

    void handle(HubEvent event);
}
