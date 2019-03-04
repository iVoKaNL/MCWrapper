package nl.ivoka.Events.Handlers;

public class EventHandler extends Handler {
    private static EventHandler instance = null;

    public static synchronized EventHandler instance() {
        if (instance == null)
            instance = new EventHandler();
        return instance;
    }
    private EventHandler() {}
}
