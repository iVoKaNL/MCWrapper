package nl.ivoka.events.handler;

import nl.ivoka.events.EventArgs;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public abstract class Handler {
    protected Map<String, Consumer<EventArgs>> eventListeners = new HashMap<>();

    public boolean addListener(String name, Consumer<EventArgs> listener) {
        if (eventListeners.containsKey(name))
            return false;

        eventListeners.put(name, listener);
        return true;
    }

    public boolean removeListener(String name) {
        if (!eventListeners.containsKey(name))
            return false;

        eventListeners.remove(name);
        return true;
    }

    public void fireEvent(EventArgs args) { eventListeners.forEach((x, y) -> y.accept(args)); }
    public void clearListeners() { eventListeners.clear(); }

    public int getListenerSize() { return eventListeners.size(); }
    public String[] getListeners() { return eventListeners.keySet().toArray(new String[0]); }
    public boolean containsListener(String name) { return eventListeners.containsKey(name); }
}
