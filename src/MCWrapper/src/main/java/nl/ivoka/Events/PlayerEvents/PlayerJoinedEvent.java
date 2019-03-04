package nl.ivoka.Events.PlayerEvents;

import nl.ivoka.Events.EventArgs;

public class PlayerJoinedEvent extends EventArgs {
    public String name;

    public PlayerJoinedEvent(String name) {
        this.name = name;
    }
}
