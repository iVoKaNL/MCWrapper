package nl.ivoka.minecraft;

public class Stats {
    private final Player player;
    private final int kills;
    private final int deaths;
    private final int logins;
    private final int teleports;

    public Stats(Player player, int kills, int deaths, int logins, int teleports) {
        this.player     = player;
        this.kills      = kills;
        this.deaths     = deaths;
        this.logins     = logins;
        this.teleports  = teleports;
    }

    // region Getters
    public Player getPlayer() { return player; }
    public int getKills() { return kills; }
    public int getDeaths() { return deaths; }
    public int getLogins() { return logins; }
    public int getTeleports() { return teleports; }

    @Override
    public String toString() { return player.getPlayerName() + "kills:"+kills+" deaths:"+deaths+" logins:"+logins+" teleports:"+teleports; }
    // endregion
}
