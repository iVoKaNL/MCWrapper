package nl.ivoka.EventArgs.ServerEvents;

import nl.ivoka.EventArgs.EventArgs;

public class ServerOutputEventArgs extends EventArgs {
    public String message;

    public ServerOutputEventArgs(String message) {
        this.message = message;
    }
}
