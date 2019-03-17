package application.event;

import com.google.common.eventbus.EventBus;

public class EventBusFactory {

    private static EventBus eventBus = new EventBus();

    public static EventBus getEventBus() {
        return eventBus;
    }
}
