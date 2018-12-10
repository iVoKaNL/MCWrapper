package nl.ivoka;

import nl.ivoka.EventArgs.PlayerEvents.PlayerChatEventArgs;
import nl.ivoka.EventArgs.PlayerEvents.PlayerJoinedEventArgs;
import nl.ivoka.EventArgs.PlayerEvents.PlayerLeftEventArgs;
import nl.ivoka.EventArgs.PlayerEvents.PlayerPositionEventArgs;
import nl.ivoka.EventArgs.ServerEvents.ServerStatusEventArgs;
import nl.ivoka.Plugins.IMCWrapperPlugin;
import org.dom4j.DocumentException;

import java.io.IOException;

public class JavascriptConnector implements IMCWrapperPlugin {
    public String Name="JavascriptConnector";
    private JavascriptPluginManager manager;

    public JavascriptConnector(MinecraftConnector connector) throws DocumentException, IOException{
        manager = new JavascriptPluginManager();

        if (connector.events == null) {
            System.out.println(Name+": error while loading plugin. Events = null");
            return;
        } else {
            connector.events.addListener((x) -> {
                if (x instanceof PlayerChatEventArgs)
                    PlayerChat((PlayerChatEventArgs)x);
                else if (x instanceof PlayerJoinedEventArgs)
                    PlayerJoin((PlayerJoinedEventArgs)x);
                else if (x instanceof PlayerLeftEventArgs)
                    PlayerLeft((PlayerLeftEventArgs)x);
                else if (x instanceof PlayerPositionEventArgs)
                    PlayerPosition((PlayerPositionEventArgs)x);
                else if (x instanceof ServerStatusEventArgs) {
                    ServerStatusEventArgs e = (ServerStatusEventArgs) x;
                    if (e.event == ServerStatusEventArgs.Event.START)
                        ServerStart(e);
                    else if (e.event == ServerStatusEventArgs.Event.STOP)
                        ServerStop(e);
                } else
                    return;
            });
        }
    }

    private void PlayerJoin(PlayerJoinedEventArgs e) {
        TriggerEvent("PlayerJoin", e.name);
    }
    private void PlayerLeft(PlayerLeftEventArgs e) {
        TriggerEvent("PlayerLeave", e.name);
    }
    private void PlayerPosition(PlayerPositionEventArgs e) {
        TriggerEvent("PlayerPosition", e.name, e.position);
    }
    private void PlayerChat(PlayerChatEventArgs e) {
        TriggerEvent("ChatReceived", e.name, e.chat);
    }

    private void ServerStart(ServerStatusEventArgs e) {
        TriggerEvent("ServerStart");
    }
    private void ServerStop(ServerStatusEventArgs e) {
        TriggerEvent("ServerStop");
    }

    private void TriggerEvent(String name, Object... args) {
        for (JavascriptPlugin plugin : manager.plugins) {

            String _tmp = "if(typeof "+
                    name+
                    " != 'undefined') "+
                    name+
                    "(";

            for (int i = 0; i < args.length; i++) {
                plugin.runtime.put("arg" + i, args[i]);

                _tmp += "arg"+
                        i;
                if (i != args.length-1)
                    _tmp += ", ";
            }
            _tmp += ");";

            plugin.run(_tmp);
        }
    }
}
