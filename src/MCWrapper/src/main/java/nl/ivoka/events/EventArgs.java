package nl.ivoka.events;

public abstract class EventArgs {
    public int identifier;

    public void setIdentifier(int identifier) { this.identifier=identifier; }

    @Override
    public abstract String toString();
}
