package nl.ivoka.events.player;

public class PlayerQuitEventArgs extends PlayerEventArgs {

    public PlayerQuitEventArgs(String uuid) { this(uuid, null); }
    public PlayerQuitEventArgs(String uuid, String playerName) { super(uuid, playerName); }

    @Override
    public String toString() {
        return "PlayerQuitEventArgs -" +
                " uuid:"+uuid+
                " playerName:"+playerName;
    }
}
