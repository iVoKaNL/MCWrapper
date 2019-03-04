package nl.ivoka.Events.Handlers;

import nl.ivoka.Events.EventArgs;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public abstract class Handler {
    Map<String, Consumer<EventArgs>> eventListeners = new HashMap<>();
    private Map<Consumer<EventArgs>, String> reverseEventListeners = new HashMap<>();

    public void addListener(String name, Consumer<EventArgs> listener) { eventListeners.put(name, listener); reverseEventListeners.put(listener, name); }
    public void removeListener(String name) { reverseEventListeners.remove(eventListeners.get(name)); eventListeners.remove(name); }
    public void fireEvent(EventArgs args) { eventListeners.forEach((x, y) -> y.accept(args)); }
    public void clearListeners() { eventListeners.clear(); reverseEventListeners.clear(); }

    public int getEventListenersSize() { return eventListeners.size(); }
    public String[] getListeners() { return reverseEventListeners.values().toArray(new String[0]); }
    public boolean containsListener(String name) { return eventListeners.containsKey(name); }
}
