package nl.ivoka.events.player;

public class PlayerQuitEventArgs extends PlayerEventArgs {

    public PlayerQuitEventArgs(String playerName) { this(null, playerName); }
    public PlayerQuitEventArgs(String uuid, String playerName) { super(uuid, playerName); }
}
