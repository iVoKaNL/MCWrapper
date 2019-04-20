package nl.ivoka.events.player;

import nl.ivoka.events.EventArgs;

public abstract class PlayerEventArgs extends EventArgs {
    public final String uuid;
    public final String playerName;

    public PlayerEventArgs(String playerName) { this(null, playerName); }
    public PlayerEventArgs(String uuid, String playerName) {
        this.uuid = uuid;
        this.playerName = playerName;
    }
}
