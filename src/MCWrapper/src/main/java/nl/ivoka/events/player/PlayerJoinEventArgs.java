package nl.ivoka.events.player;

public class PlayerJoinEventArgs extends PlayerEventArgs {

    public PlayerJoinEventArgs(String playerName) { this(null, playerName); }
    public PlayerJoinEventArgs(String uuid, String playerName) { super(uuid, playerName); }
}
