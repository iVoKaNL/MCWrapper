package nl.ivoka.EventArgs.PlayerEvents;

import nl.ivoka.EventArgs.EventArgs;

public class PlayerJoinedEventArgs extends EventArgs {
    public String name;

    public PlayerJoinedEventArgs(String name) {
        this.name = name;
    }
}
