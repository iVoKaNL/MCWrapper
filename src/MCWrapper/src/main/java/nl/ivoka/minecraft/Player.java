package nl.ivoka.minecraft;

public class Player {
    private final String uuid;
    private final String playerName;

    public Player(String uuid, String playerName) {
        this.uuid = uuid;
        this.playerName = playerName;
    }

    // region getters
    public String getUUID() { return uuid; }
    public String getPlayerName() { return playerName; }

    @Override
    public String toString() { return playerName+":"+uuid; }
    // endregion
}
