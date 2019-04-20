package nl.ivoka.events.player;

public class PlayerChatEventArgs extends PlayerEventArgs {
    public final String chat;

    public PlayerChatEventArgs(String playerName, String chat) { this(null, playerName, chat); }
    public PlayerChatEventArgs(String uuid, String playerName, String chat) {
        super(uuid, playerName);
        this.chat = chat;
    }
}
