package nl.ivoka.EventArgs.ServerEvents;

import nl.ivoka.EventArgs.EventArgs;

public class ServerStatusEventArgs extends EventArgs {
    public enum Event {
        START,
        STOP,
        RESTART,
        KILL,
        DONE
    }
    public Event event;

    public ServerStatusEventArgs(Event event) {
        this.event = event;
    }
}
