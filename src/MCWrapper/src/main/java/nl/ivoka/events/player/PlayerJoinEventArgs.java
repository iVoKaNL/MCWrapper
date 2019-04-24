package nl.ivoka.events.player;

public class PlayerJoinEventArgs extends PlayerEventArgs {

    public PlayerJoinEventArgs(String uuid) { this(uuid, null); }
    public PlayerJoinEventArgs(String uuid, String playerName) { super(uuid, playerName); }

    @Override
    public String toString() {
        return "PlayerJoinEventArgs -" +
                " uuid:"+uuid+
                " playerName:"+playerName;
    }
}
