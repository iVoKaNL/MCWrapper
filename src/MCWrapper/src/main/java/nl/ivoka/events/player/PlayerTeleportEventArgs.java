package nl.ivoka.events.player;

public class PlayerTeleportEventArgs extends PlayerEventArgs {
    public final String position;

    public final double x, y, z;
    public final float yaw, pitch;

    private final boolean useCoordinates;

    public PlayerTeleportEventArgs(String uuid, String position) { this(uuid, null, position); }
    public PlayerTeleportEventArgs(String uuid, String playerName, String position) {
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
    public PlayerTeleportEventArgs(String uuid, double x, double y, double z) { this(uuid, null, x, y, z); }
    public PlayerTeleportEventArgs(String uuid, double x, double y, double z, float yaw, float pitch) { this(uuid, null, x, y, z, yaw, pitch); }
    public PlayerTeleportEventArgs(String uuid, String playerName, double x, double y, double z) { this(uuid, playerName, x, y, z, 0, 0); }
    public PlayerTeleportEventArgs(String uuid, String playerName, double x, double y, double z, float yaw, float pitch) {
        super(uuid, playerName);
        position = "x:"+x+
                " y:"+y+
                " z:"+z+
                " yaw:"+yaw+
                " pitch:"+pitch;

        this.x      = x;
        this.y      = y;
        this.z      = z;
        this.yaw    = yaw;
        this.pitch  = pitch;

        useCoordinates=true;
    }

    public boolean useCoordinates() { return useCoordinates; }

    @Override
    public String toString() {
        return "PlayerTeleportEventArgs -" +
                " uuid:" + super.uuid +
                " playerName:" + super.playerName +
                (useCoordinates?(" "+position):(" position:"+position));
    }
}
