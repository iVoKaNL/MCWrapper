package nl.ivoka.minecraft;

public class Player {
    private String UUID;
    private String playerName;

    public Player(String UUID, String playerName) {
        this.UUID = UUID;
        this.playerName = playerName;
    }

    // region getters
    public String getUUID() { return UUID; }
    public String getPlayerName() { return playerName; }

    @Override
    public String toString() { return playerName+":"+UUID; }
    // endregion
}
