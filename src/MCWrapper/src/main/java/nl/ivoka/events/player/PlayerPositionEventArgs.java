package nl.ivoka.events.player;

public class PlayerPositionEventArgs extends PlayerEventArgs {
    public final String position;

    public final int x;
    public final int y;
    public final int z;
    public final int yaw;
    public final int pitch;

    private final boolean useCoordinates;

    public PlayerPositionEventArgs(String playerName, String position) { this(null, playerName, position); }
    public PlayerPositionEventArgs(String uuid, String playerName, String position) {
        super(uuid, playerName);
        this.position = position;

        this.x      = 0;
        this.y      = 0;
        this.z      = 0;
        this.yaw    = 0;
        this.pitch  = 0;

        useCoordinates=false;
    }

    // TODO check if yaw and pitch set to 0 is OK
    public PlayerPositionEventArgs(String uuid, String playerName, int x, int y, int z) { this(uuid, playerName, x, y, z, 0, 0); }
    public PlayerPositionEventArgs(String uuid, String playerName, int x, int y, int z, int yaw, int pitch) {
        super(uuid, playerName);
        this.position = null;

        this.x      = x;
        this.y      = y;
        this.z      = z;
        this.yaw    = yaw;
        this.pitch  = pitch;

        useCoordinates=true;
    }

    public boolean useCoordinates() { return useCoordinates; }
}
