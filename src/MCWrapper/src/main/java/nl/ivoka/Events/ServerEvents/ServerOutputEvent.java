package nl.ivoka.Events.ServerEvents;

import nl.ivoka.Events.EventArgs;

public class ServerOutputEvent extends EventArgs {
    public String message;

    public ServerOutputEvent(String message) {
        this.message = message;
    }
}
