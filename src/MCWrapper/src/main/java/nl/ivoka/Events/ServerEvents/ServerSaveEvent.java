package nl.ivoka.Events.ServerEvents;

import nl.ivoka.Events.EventArgs;

public class ServerSaveEvent extends EventArgs {
    public enum Event {
        SAVING,
        SAVED,
        ON,
        OFF
    }
    public Event event;

    public ServerSaveEvent(Event event) {
        this.event = event;
    }
}
