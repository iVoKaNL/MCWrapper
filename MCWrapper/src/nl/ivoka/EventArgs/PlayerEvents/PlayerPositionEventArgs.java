package nl.ivoka.EventArgs.PlayerEvents;

import nl.ivoka.EventArgs.EventArgs;

public class PlayerPositionEventArgs extends EventArgs {
    public String name;
    public String position;

    public PlayerPositionEventArgs(String name, String position) {
        this.name = name;
        this.position = position;
    }
}
