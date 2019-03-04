package nl.ivoka.Events.PlayerEvents;

import nl.ivoka.Events.EventArgs;

public class PlayerPositionEvent extends EventArgs {
    public String name;
    public String position;

    public PlayerPositionEvent(String name, String position) {
        this.name = name;
        this.position = position;
    }
}
