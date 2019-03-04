package nl.ivoka.Events;

import nl.ivoka.Events.ServerEvents.ServerSaveEvent;
import nl.ivoka.Events.ServerEvents.ServerStatusEvent;

public abstract class EventArgs {
    public Integer identifier;

    public EventArgs() {}
    public EventArgs(String str) {}
    public EventArgs(String str1, String str2) {}
    public EventArgs(ServerStatusEvent.Event event) {}
    public EventArgs(ServerSaveEvent.Event event) {}

    public void setIdentifier(Integer identifier) { this.identifier=identifier; }
}
