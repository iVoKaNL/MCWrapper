package nl.ivoka.events.player;

public class PlayerChatEventArgs extends PlayerEventArgs {
    public final String message;

    public PlayerChatEventArgs(String uuid, String message) { this(uuid, null, message); }
    public PlayerChatEventArgs(String uuid, String playerName, String message) {
        super(uuid, playerName);
        this.message = message;
    }

    @Override
    public String toString() {
        return "PlayerChatEventArgs -" +
                " uuid:"+super.uuid+
                " playerName:"+super.playerName+
                " message:"+message;
    }
}
