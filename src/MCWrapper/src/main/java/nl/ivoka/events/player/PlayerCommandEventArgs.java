package nl.ivoka.events.player;

public class PlayerCommandEventArgs extends PlayerEventArgs {
    public final String command;
    public final String[] args;

    public PlayerCommandEventArgs(String uuid, String command, String[] args) { this(uuid, null, command, args); }
    public PlayerCommandEventArgs(String uuid, String playerName, String command, String[] args) {
        super(uuid, playerName);
        this.command = command;
        this.args = args;
    }

    @Override
    public String toString() {
        return "PlayerCommandEventArgs -" +
                " uuid:"+super.uuid+
                " playerName:"+super.playerName+
                " command:"+command+
                " args:"+args.toString();
    }
}
