package nl.ivoka.EventArgs.PlayerEvents;

import nl.ivoka.EventArgs.EventArgs;

public class PlayerLeftEventArgs extends EventArgs {
    public String name;

    public PlayerLeftEventArgs(String name) {
        this.name = name;
    }
}
