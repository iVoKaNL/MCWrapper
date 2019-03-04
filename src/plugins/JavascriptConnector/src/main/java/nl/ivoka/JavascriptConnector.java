package nl.ivoka;

import nl.ivoka.API.Console;
import nl.ivoka.Events.*;
import nl.ivoka.Events.Handlers.EventHandler;
import nl.ivoka.Events.PlayerEvents.*;
import nl.ivoka.Events.ServerEvents.*;
import nl.ivoka.Plugin.IMCWrapperPlugin;
import nl.ivoka.Plugin.PluginManager;
import org.kohsuke.MetaInfServices;

@MetaInfServices
public class JavascriptConnector implements IMCWrapperPlugin {
    private static String name = "JavascriptConnector";
    private static boolean running = false;

    private JavascriptPluginManager manager;
    private PluginManager pluginManager;

    public JavascriptConnector() { manager = new JavascriptPluginManager(); }

    private void EventListener(EventArgs e) {
        if (e instanceof PlayerChatEvent)
            PlayerChat((PlayerChatEvent) e);
        else if (e instanceof PlayerJoinedEvent)
            PlayerJoin((PlayerJoinedEvent) e);
        else if (e instanceof PlayerLeftEvent)
            PlayerLeft((PlayerLeftEvent) e);
        else if (e instanceof PlayerPositionEvent)
            PlayerPosition((PlayerPositionEvent) e);
        else if (e instanceof ServerStatusEvent) {
            ServerStatusEvent x = (ServerStatusEvent) e;

            if (x.event == ServerStatusEvent.Event.START)
                ServerStart(x);
            else if (x.event == ServerStatusEvent.Event.STOP)
                ServerStop(x);
            else if (x.event == ServerStatusEvent.Event.RESTART)
                ServerRestart(x);
            else if (x.event == ServerStatusEvent.Event.KILL)
                ServerKill(x);
            else if (x.event == ServerStatusEvent.Event.DONE)
                ServerDone(x);
        } else if (e instanceof ServerSaveEvent) {
            ServerSaveEvent x = (ServerSaveEvent) e;

            if (x.event == ServerSaveEvent.Event.SAVING)
                ServerSaving(x);
            else if (x.event == ServerSaveEvent.Event.SAVED)
                ServerSaved(x);
            else if (x.event == ServerSaveEvent.Event.ON)
                SavingON(x);
            else if (x.event == ServerSaveEvent.Event.OFF)
                SavingOFF(x);
        } else if (e instanceof ServerOutputEvent)
            ServerOutput((ServerOutputEvent)e);
    }

    public String getName() { return name; }
    public boolean isRunning() { return running; }

    public PluginManager getPluginManager() { return pluginManager; }
    public void setPluginManager(PluginManager pluginManager) { this.pluginManager=pluginManager; }

    @Override
    public void initialize(PluginManager pluginManager) {
        if (pluginManager==null) {
            writeError(name+" pluginManager variable is NULL!!! Shouldn't be possible, please report! Add log file!");
        } else
            this.pluginManager=pluginManager;
    }
    public void start() {
        if (running)
            writeError(name+" is already running!");
        else if (pluginManager==null) {
            writeError(name+" pluginManager variable is NULL!!! Shouldn't be possible, please report! Add log file!");
        } else {
            running=true;
            manager.loadPlugins();
            EventHandler.instance().addListener(name, this::EventListener);
        }

        checkState();
    }
    public void reload() {
        if (!running)
            writeError("JavascriptConnector is not running! You can use start instead of reload.");
        else {
            stop();
            start();
        }
    }
    public void stop() {
        if (!running)
            writeError(name+" is not running!");
        else {
            EventHandler.instance().removeListener(name);
            manager.stopPlugins();
            running=false;
        }

        checkState();
    }

    @Override
    public void checkState() {
        if (running && !pluginManager.getPluginState(name).equals(PluginManager.PLUGINSTATE.RUNNING))
            pluginManager.changePluginState(name, PluginManager.PLUGINSTATE.RUNNING);
        else if (!running && !pluginManager.getPluginState(name).equals(PluginManager.PLUGINSTATE.STOPPED))
            pluginManager.changePluginState(name, PluginManager.PLUGINSTATE.STOPPED);
    }

    private void PlayerJoin(PlayerJoinedEvent e) { TriggerEvent("PlayerJoin", e.name); }
    private void PlayerLeft(PlayerLeftEvent e) { TriggerEvent("PlayerLeave", e.name); }
    private void PlayerPosition(PlayerPositionEvent e) { TriggerEvent("PlayerPosition", e.name, e.position); }
    private void PlayerChat(PlayerChatEvent e) { TriggerEvent("ChatReceived", e.name, e.chat); }

    private void ServerStart(ServerStatusEvent e) { TriggerEvent("ServerStart"); }
    private void ServerStop(ServerStatusEvent e) { TriggerEvent("ServerStop"); }
    private void ServerRestart(ServerStatusEvent e) { TriggerEvent("ServerRestart"); }
    private void ServerKill(ServerStatusEvent e) { TriggerEvent("ServerKill"); }
    private void ServerDone(ServerStatusEvent e) { TriggerEvent("ServerDone"); }

    private void ServerSaving(ServerSaveEvent e) { TriggerEvent("ServerSaving"); }
    private void ServerSaved(ServerSaveEvent e) { TriggerEvent("ServerSaved"); }
    private void SavingON(ServerSaveEvent e) { TriggerEvent("SavingON"); }
    private void SavingOFF(ServerSaveEvent e) { TriggerEvent("SavingOFF"); }

    private void ServerOutput(ServerOutputEvent e) { TriggerEvent("ServerOutput"); }

    private void TriggerEvent(String name, Object... args) {
        if (manager.getPluginCount()>=1) {
            for (JavascriptPlugin plugin : manager.getPlugins()) {
                String _javascript = "if(typeof "+name+" != 'undefined') "+name+"(";

                for (int i=0; i<args.length; i++) {
                    plugin.runtimePut("arg"+i, args[i]);

                    _javascript += "arg"+i+", ";
                }

                if (args.length>0)
                    _javascript = _javascript.substring(0, _javascript.length()-2);
                _javascript += ");";

                plugin.run(_javascript);
            }
        }
    }
}
