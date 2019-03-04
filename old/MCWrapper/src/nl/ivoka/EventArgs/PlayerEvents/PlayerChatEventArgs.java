package nl.ivoka.EventArgs.PlayerEvents;

import nl.ivoka.EventArgs.EventArgs;

public class PlayerChatEventArgs extends EventArgs{
    public String name;
    public String chat;

    public PlayerChatEventArgs(String name, String chat) {
        this.name = name;
        this.chat = chat;
    }
}
