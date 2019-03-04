package nl.ivoka.Events.PlayerEvents;

import nl.ivoka.Events.EventArgs;

public class PlayerLeftEvent extends EventArgs {
    public String name;

    public PlayerLeftEvent(String name) {
        this.name = name;
    }
}
