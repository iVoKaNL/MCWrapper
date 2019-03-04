package nl.ivoka.Events.PlayerEvents;

import nl.ivoka.Events.EventArgs;

public class PlayerChatEvent extends EventArgs {
    public String name;
    public String chat;

    public PlayerChatEvent(String name, String chat) {
        this.name = name;
        this.chat = chat;
    }
}
