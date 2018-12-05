package nl.ivoka.EventArgs.ServerEvents;

import nl.ivoka.EventArgs.EventArgs;

public class ServerSaveEventArgs extends EventArgs {
    public enum Event {
        SAVING,
        SAVED,
        ON,
        OFF
    }
    public Event event;

    public ServerSaveEventArgs(Event event) {
        this.event = event;
    }
}
