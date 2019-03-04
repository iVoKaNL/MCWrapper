package nl.ivoka.Events.ServerEvents;

import nl.ivoka.Events.EventArgs;

public class ServerStatusEvent extends EventArgs {
    public enum Event {
        START,
        STOP,
        RESTART,
        KILL,
        DONE
    }
    public Event event;

    public ServerStatusEvent(Event event) {
        this.event = event;
    }
}
